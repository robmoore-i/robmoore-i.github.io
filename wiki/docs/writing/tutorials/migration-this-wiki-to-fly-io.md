# Migrating this Wiki to fly.io

**Note**: My wiki now runs on GitHub pages, which is a better fit. I no longer have the power to edit my website inline, but that wasn't really an important feature. GitHub pages is just as free, and unlike running a wiki docker container on fly.io, I don't have to think about resources, like memory.

## Problem

AWS is expensive. I have been paying 15 SGD/month to run a small EC2 instance. I had installed Docker on this EC2 instance, and was running this Wiki as a container, with appropriate exposed ports and environment variables. I was using the Wiki's integration with Letsencrypt to maintain the SSL certificate for the domain I was using for it: wiki.rrmoore.com.

It's not breaking the bank, but it's more than necessary. If I were ever in a pinch, I would be cross with myself for not having saved these dollars earlier.

## Solution

There are many cloud hosting providers now, all with their various niches. I have had a little experience with fly.io, and I love it. The documentation is complete, relevant and discoverable. I find the tool easy to use. Applications are configured using files, and not in a GUI. The CLI tool's output contains useful information. I'm a big fan of fly.io.

Not so long ago, they introduced a postgres application offering. I like the way they did it - there is not very much abstraction. It is provided more as a layer on top of the ordinary container application offering. That is, it will run a postgres app as a container, and provide additional application-specific information to you. It is a great example of something I love: Design patterns that find usage in abstract ways outside of just code. Fly's postgres offering is an example of the decorator pattern applied to their product offering. The benefits are similar to those we find in code, flexibility and reuse.

## Implementation

Having decided that I was going to move my Wiki to fly.io using their postgres offering, I took the following steps to complete it.

1. Take a logical backup of the existing Wiki's database.
2. Create the new database using Fly
3. Restore the backup into the new database
4. Prepare the new Wiki image
5. Deploy the new Wiki application
6. $$$

### 1. Take a logical backup

I took a logical backup (rather than a physical backup) for a couple of reasons.

(a) The database is pretty small
(b) I don't want to think about how I'm going to access the data volumes for the target database

This later turned out to be necessary also because the target database is PG14 whereas the source database was PG11.

I took the logical backup like this:

```
rob@Robs-MacBook-Pro-2 aws % ssh -i ~/.aws/wiki-ec2-keypair.pem ubuntu@54.169.244.121
Welcome to Ubuntu 20.04.3 LTS (GNU/Linux 5.11.0-1022-aws x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/advantage

  System information as of Sat Jan 14 05:25:44 UTC 2023

  System load:                      0.0
  Usage of /:                       75.6% of 7.69GB
  Memory usage:                     50%
  Swap usage:                       0%
  Processes:                        130
  Users logged in:                  0
  IPv4 address for br-e6e84ec28b7a: 172.18.0.1
  IPv4 address for docker0:         172.17.0.1
  IPv4 address for eth0:            172.31.26.15

 * Ubuntu Pro delivers the most comprehensive open source security and
   compliance features.

   https://ubuntu.com/aws/pro

88 updates can be applied immediately.
21 of these updates are standard security updates.
To see these additional updates run: apt list --upgradable


*** System restart required ***
Last login: Sun Nov  6 13:24:20 2022 from 218.186.139.102
ubuntu@ip-172-31-26-15:~$
ubuntu@ip-172-31-26-15:~$ docker exec db pg_dump wiki -U wiki > backup.sql
ubuntu@ip-172-31-26-15:~$ head backup.sql 
--
-- PostgreSQL database dump
--

-- Dumped from database version 11.15 (Debian 11.15-1.pgdg90+1)
-- Dumped by pg_dump version 11.15 (Debian 11.15-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
ubuntu@ip-172-31-26-15:~$
ubuntu@ip-172-31-26-15:~$ logout
Connection to 54.169.244.121 closed.
rob@Robs-MacBook-Pro-2 aws %
rob@Robs-MacBook-Pro-2 aws % scp -i ~/.aws/wiki-ec2-keypair.pem ubuntu@54.169.244.121:~/backup.sql db/
...
```

At this point, I had the `backup.sql` for restoring the database available to me. Next, I needed to create the new database, and restore the backup into it.

### 2. Create the new database using Fly

I created a postgres database using the `fly` CLI tool.

```
rob@Robs-MacBook-Pro-2 db % fly pg create --name rob-wiki-pg --region sin                
automatically selected personal organization: Rob Moore
? Select configuration: Development - Single node, 1x shared CPU, 256MB RAM, 1GB disk
Creating postgres cluster in organization personal
Creating app...
Setting secrets on app rob-wiki-pg...Provisioning 1 of 1 machines with image flyio/postgres:14.6
Waiting for machine to start...
Machine 3287d97b035d85 is created
==> Monitoring health checks
  Waiting for 3287d97b035d85 to become healthy (started, 3/3)

Postgres cluster rob-wiki-pg created
  Username:    postgres
  Password:    <<redacted>>
  Hostname:    rob-wiki-pg.internal
  Proxy port:  5432
  Postgres port:  5433
  Connection string: postgres://postgres:<<redacted>>@rob-wiki-pg.internal:5432

Save your credentials in a secure place -- you won't be able to see them again!

Connect to postgres
Any app within the Rob Moore organization can connect to this Postgres using the following credentials:
For example: postgres://postgres:<<redacted>>@rob-wiki-pg.internal:5432


Now that you've set up postgres, here's what you need to understand: https://fly.io/docs/reference/postgres-whats-next/
rob@Robs-MacBook-Pro-2 db % 
```

By default, the database is accessible only from another Fly app. In order to restore the database backup into this new database, I'll need to be able to connect to it from my machine using `psql`.

To make the database externall accessible, there are some convenient instructions here: https://fly.io/docs/postgres/connecting/connecting-external/. Here's what I did:

```
rob@Robs-MacBook-Pro-2 db % fly ips allocate-v4 -a rob-wiki-pg                               
VERSION IP              TYPE    REGION  CREATED AT 
v4      137.66.26.46    public  global  6s ago          

rob@Robs-MacBook-Pro-2 db %
rob@Robs-MacBook-Pro-2 db % fly config save -a rob-wiki-pg
Wrote config file fly.toml
rob@Robs-MacBook-Pro-2 db %
```

When making Fly's postgres external accessible, you need to edit the `fly.toml` to add some config. Here are the changes, which are described in better detail in the documentation page I shared above.

```
- internal_port = 80
+ internal_port = 5432 
...
+   [[services.ports]]
+     handlers = ["pg_tls"]
+    port = 5432
```

Having updated the configuration, I needed to apply it, which is done by redeploying the application. The documentation notes the importance of using the correct postgres major version for the image, which you need to re-specify when using this command.

```
rob@Robs-MacBook-Pro-2 wiki % fly deploy . -a rob-wiki-pg --image flyio/postgres:14
...
rob@Robs-MacBook-Pro-2 wiki %
```

Having done this, I was able to test my connection using my local `psql`, and it worked fine.

```
rob@Robs-MacBook-Pro-2 wiki % psql --version
psql (PostgreSQL) 14.5
rob@Robs-MacBook-Pro-2 db % psql postgres://postgres:<<redacted>>@rob-wiki-pg.fly.dev:5432     
psql (14.5, server 14.6 (Debian 14.6-1.pgdg110+1))
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
Type "help" for help.

postgres=#
```

With the ability to connect to the new database, I needed to set it up with a few things.

I want a specific logical database for the Wiki application, and it should use its own user.

This can be done using `psql`:

```
rob@Robs-MacBook-Pro-2 db % psql postgres://postgres:<<redacted>>@rob-wiki-pg.fly.dev:5432     
psql (14.5, server 14.6 (Debian 14.6-1.pgdg110+1))
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
Type "help" for help.

postgres=# CREATE DATABASE wiki;
CREATE DATABASE 
postgres=# CREATE USER wiki;
CREATE ROLE
postgres=# ALTER USER wiki WITH ENCRYPTED PASSWORD '<<redacted>>';
ALTER ROLE
postgres=# GRANT ALL PRIVILEGES ON DATABASE wiki TO wiki;
GRANT
postgres=# ^D\q
rob@Robs-MacBook-Pro-2 db %
```

### 3. Restore the backup into the new database

A logical postgres backup is a SQL script, which you can execute on your database server in order to recreate the original database.

So you can restore a logical backup to the database of your choice, using the user of your choice, like this:

```
rob@Robs-MacBook-Pro-2 db % psql postgres://wiki:<<redacted>>@rob-wiki-pg.fly.dev:5432/wiki -f backup.sql 
SET
SET
...
ALTER TABLE
rob@Robs-MacBook-Pro-2 db %
```

To satisfy yourself, you can check the relations inside the database. In this case, the application places its relations in the public schema.

```
rob@Robs-MacBook-Pro-2 db % psql postgres://wiki:<<redacted>>@rob-wiki-pg.fly.dev:5432/wiki              
psql (14.5, server 14.6 (Debian 14.6-1.pgdg110+1))
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
Type "help" for help.

wiki=> \dt
             List of relations
 Schema |       Name       | Type  | Owner 
--------+------------------+-------+-------
 public | analytics        | table | wiki
             List of relations
 Schema |       Name       | Type  | Owner 
--------+------------------+-------+-------
 public | analytics        | table | wiki
 public | apiKeys          | table | wiki
 public | assetData        | table | wiki
 public | assetFolders     | table | wiki
 public | assets           | table | wiki
 public | authentication   | table | wiki
 public | brute            | table | wiki
 public | commentProviders | table | wiki
 public | comments         | table | wiki
 public | editors          | table | wiki
 public | groups           | table | wiki
 public | locales          | table | wiki
 public | loggers          | table | wiki
 public | migrations       | table | wiki
 public | migrations_lock  | table | wiki
 public | navigation       | table | wiki
 public | pageHistory      | table | wiki
 public | pageHistoryTags  | table | wiki
 public | pageLinks        | table | wiki
 public | pageTags         | table | wiki
 public | pageTree         | table | wiki
 public | pages            | table | wiki
 public | renderers        | table | wiki
 public | searchEngines    | table | wiki
 public | sessions         | table | wiki
 public | settings         | table | wiki
 public | storage          | table | wiki
 public | tags             | table | wiki
 public | userAvatars      | table | wiki
 public | userGroups       | table | wiki
 public | userKeys         | table | wiki
 public | users            | table | wiki
(32 rows)

wiki=> 
wiki=> ^D\q
rob@Robs-MacBook-Pro-2 db % 
```

### 4. Prepare the new Wiki image

I used this as my `Dockerfile`:

```
FROM ghcr.io/requarks/wiki:2

ENV DB_TYPE postgres
ENV DB_HOST rob-wiki-pg.fly.dev
ENV DB_PORT 5432
ENV DB_USER wiki
ENV DB_NAME wiki

ENV UPGRADE_COMPANION 1
```

For the database password (environment variable `DB_PASS`), we'll set that later using a secret.

### 5. Deploy the new Wiki application

I fearlessly deployed the application. It did not work first time, because I had overlooked a couple of things.

```
rob@Robs-MacBook-Pro-2 app % fly launch --dockerfile Dockerfile --name rob-wiki --region sin --now
==> Verifying app config
--> Verified app config
==> Building image
...
097b20a4c610: Pushed 
ded7a220bb05: Pushed 
deployment-01GPQ78SG6S8Q1H669KZVNKS73: digest: sha256:211ed0fd1302d95e1b002af205b387ecf62ae3ef19581407e50aa35d846d8896 size: 3042
--> Pushing image done
image: registry.fly.io/rob-wiki:deployment-01GPQ78SG6S8Q1H669KZVNKS73
image size: 466 MB
==> Creating release
--> release v2 created

--> You can detach the terminal anytime without stopping the deployment
==> Monitoring deployment
Logs: https://fly.io/apps/rob-wiki/monitoring

v0 is being deployed
...
^C
rob@Robs-MacBook-Pro-2 app %
```

This first deployment did not work for two reasons.

1. I needed to edit the `fly.toml` to update the internal port for the container (it is 3000, rather than the default vlaue, 8080).
2. I needed to set the database secret so that the application can actually connect to the new database.

After editing the fly.toml, I added a database secret using the appropriate Fly CLI command, which then immediately redeployed the application, having seen that the configuration had changed.

```
rob@Robs-MacBook-Pro-2 app % flyctl secrets set DB_PASS="<<redacted>>"

Release v1 created
==> Monitoring deployment
Logs: https://fly.io/apps/rob-wiki/monitoring

 1 desired, 1 placed, 1 healthy, 0 unhealthy [health checks: 1 total, 1 passing]
--> v1 deployed successfully

rob@Robs-MacBook-Pro-2 app %
```

While the deployment was happening, I was monitoring the logs at the URL provided to me by the CLI tool: https://fly.io/apps/rob-wiki/monitoring. Here I was able to see the following encouraging log messages:

```
 2023-01-14T04:57:05.026 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.025Z [MASTER] info: No new search engines found: [ SKIPPED ]
2023-01-14T04:57:05.215 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.215Z [MASTER] info: No new storage targets found: [ SKIPPED ]
2023-01-14T04:57:05.216 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.216Z [MASTER] info: Checking for installed optional extensions...
2023-01-14T04:57:05.229 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.229Z [MASTER] info: Optional extension git is installed. [ OK ]
2023-01-14T04:57:05.238 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.238Z [MASTER] info: Optional extension pandoc was not found on this system. [ SKIPPED ]
2023-01-14T04:57:05.244 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.244Z [MASTER] info: Optional extension puppeteer was not found on this system. [ SKIPPED ]
2023-01-14T04:57:05.246 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.245Z [MASTER] info: Optional extension sharp was not found on this system. [ SKIPPED ]
2023-01-14T04:57:05.252 app[1d9b7b2d] sin [info] 2023-01-14T04:57:05.251Z [MASTER] info: Authentication Strategy Local: [ OK ]
2023-01-14T04:57:06.170 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.170Z [MASTER] info: (COMMENTS/DEFAULT) Initializing...
2023-01-14T04:57:06.170 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.170Z [MASTER] info: (COMMENTS/DEFAULT) Initialization completed.
2023-01-14T04:57:06.224 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.223Z [MASTER] info: Purging orphaned upload files...
2023-01-14T04:57:06.225 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.225Z [MASTER] info: Syncing locales with Graph endpoint...
2023-01-14T04:57:06.226 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.226Z [MASTER] info: Fetching latest updates from Graph endpoint...
2023-01-14T04:57:06.248 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.248Z [MASTER] info: Purging orphaned upload files: [ COMPLETED ]
2023-01-14T04:57:06.357 app[1d9b7b2d] sin [info] Loading configuration from /wiki/config.yml... OK
2023-01-14T04:57:06.463 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.462Z [JOB] info: Rebuilding page tree...
2023-01-14T04:57:06.546 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.545Z [MASTER] info: Fetching latest updates from Graph endpoint: [ COMPLETED ]
2023-01-14T04:57:06.897 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.896Z [MASTER] info: Pulled latest locale updates for English from Graph endpoint: [ COMPLETED ]
2023-01-14T04:57:06.902 app[1d9b7b2d] sin [info] 2023-01-14T04:57:06.901Z [MASTER] info: Syncing locales with Graph endpoint: [ COMPLETED ]
2023-01-14T04:57:07.175 app[1d9b7b2d] sin [info] 2023-01-14T04:57:07.174Z [JOB] info: Using database driver pg for postgres [ OK ]
2023-01-14T04:57:07.422 app[1d9b7b2d] sin [info] 2023-01-14T04:57:07.421Z [JOB] info: Rebuilding page tree: [ COMPLETED ] 
```

### 6. $$$

My new Wiki is now available at: https://rob-wiki.fly.dev/ and is now free for me to run, versus the comparitive fortune I was paying on AWS.

The first thing I did was write up this tutorial. I created the material as I was doing the migration. Continuous documentation in this way is a useful practice and I recommend it to everyone. My process involved a little more trial and error than is reflected in this log, but it did essentially go like this.

## Backups

Here is my patented backup technology:

```
#!/bin/bash

if [[ $0 != "./backup" ]]; then
  echo "Please run me from the db/ directory."
  exit 1
fi

DB_PASS=$(cat db-password.txt)
PREFIX=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_FILE="backups/${PREFIX}-backup-FLY.sql"
pg_dump "postgres://wiki:${DB_PASS}@rob-wiki-pg.fly.dev:5432/wiki" > "$BACKUP_FILE"
echo "Saved $BACKUP_FILE"
```

I execute it sometimes. I am destined for inconvenient data loss in the future.

---
Created on 2023-01-13

Updated on 2023-01-14
