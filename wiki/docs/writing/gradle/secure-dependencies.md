# WIP: Secure Dependencies

This page describes how to use Gradle to deal with vulnerable dependencies in applications.

## A direct dependency used in a program that you build needs to be updated to a newer version

If your build uses an old version of a dependency and that version has some CVEs against its name, you need to update the version declared in the build so that your program doesn't use it. This is the easiest and most straight forward case to fix.

#### Minimal example build

A JAR built by this build (using the `shadowJar` task) will contain a vulnerable version of guava, which will result in failed security scans for your JAR and any container image that contains this JAR.

```
plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")
}

application {
    mainClass.set("com.rrm.app.Main")
}
```

#### Fix

You need to update the declaration of this direct dependency in my build to use a newer version.

```
dependencies {
    implementation("com.google.guava:guava:33.3.1-jre")
}
```

## A transitive dependency used in a program that you build needs to be updated to a newer version

#### Minimal example build

This build depends on the classes from the above build. This means that transitively, it includes that vulnerable version of guava. We need to amend the build so that this build instead resolves a newer version of guava chosen by us.

```
plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":direct"))
}

application {
    mainClass.set("com.rrm.transitive.Main")
}
```

#### Fix

To fix this, you need to tell Gradle that when it is resolving a dependency on guava, it should always use a version specified by you, that does not have any vulnerabilities. You can do this by appending the below snippet to the project build script. More commonly, you can define a plugin within your build that specifies this for all projects.

```
configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.google.guava" && requested.name == "guava") {
                useVersion("33.3.1-jre")
            }
        }
    }
}
```

## A JAR included in a container image you build needs to be replaced by a newer version of the same JAR

#### My use case: Securing Apache Hive

My real use case here is in building a custom variant of the Apache Hive image that removes all its security vulnerabilities.

Apache Hive is borderline abandonware, but somehow is also an important element in data lakehouse applications using distributed query engines like Trino. If you want to build a query engine for unstructured data, on-prem (rather than using a managed service like AWS Glue), then you may end up faced with the prospect of running Apache Hive.

When I first ran the Hive image through our image scanner of choice, Trivy, it came up with literally hundreds of vulnerabilities, including a very large number of critical vulnerabilities such as RCEs. If you are an on-prem software vendor, as my employer is, this is a showstopper, because your biggest and most important customers will struggle to onboard your product.

For this page though and for the purposes of this example, I can't be bothered to replicate the nonsense I have done at work. Instead, I will demonstrate what is possible using some of Gradle's most fundemental but also least understood constructions: Configurations and Task types which manipulate Files and FileCollections.

#### Downloading a single JAR and putting it somewhere

This build defines a single task, `putGuavaJarSomewhere`, which downloads the latest version of the guava JAR and puts it into the directory `build/somewhere`.

When I needed to overwrite a JAR baked into the Apache Hive image, I did something like this.

```
plugins {
    base
}

repositories {
    mavenCentral()
}

val guava by configurations.creating

dependencies {
    guava("com.google.guava:guava:33.3.1-jre")
}

tasks {
    val putGuavaJarSomewhere by registering(Sync::class) {
        from(guava)
        into(layout.buildDirectory.dir("somewhere"))
    }
}
```

## A JAR included in an image you build has shaded classes of transitive dependencies that need to be replaced by classes from a newer version of the same transitive dependency

TODO

#### Minimal example build

#### Fix

## A note on automatic tests

In the last two cases, we are modifying third-party software after it has been released. This means the quality control that they may have done prior to shipping the software no longer applies to what we are running, due to the shelleyan JAR transformations we've done. This is where I think a different and more expansive kind of testing can help.

So often, developers say "Don't test third-party dependencies", by which they mean the database, the container runtime etc. I see this slightly more subtely. If you can write fast, deterministic, easily maintainable tests that give you great confidence by incorporating your third-party dependencies, I think you should do it. However, if those tests would be flaky, slow and burdensome, then you should not do it. With modern hardware and software, I find that it is often possible to write nice tests that incorporate certain elements of your software's runtime that traditionally have been excluded from the scope of "unit tests" by puritans. These include the database or the container runtime. My suggestion is that you experiment and do what is most delightful for you and your team.

---
This page is not yet finished.

Created on 2023-10-30

Updated on 2025-02-23
