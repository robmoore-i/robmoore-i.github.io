# Secure Dependencies

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

To fix this, you need to tell Gradle that when it is resolving a dependency on guava, it should always use a version specified by you, that does not have any vulnerabilities. You can do this by appending the below snippet to the project build script. More commonly, you can [define a plugin within your build](/writing/gradle/gradle-monorepo-structure) that specifies this for all projects.

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

My real use case here is in building a custom variant of the Apache Hive container image that removes all its security vulnerabilities.

Apache Hive is borderline abandonware, but somehow is also a key player in the ecosystem for building data lakehouse applications using distributed query engines like Trino. If you want to build a query engine for unstructured data, on-prem (rather than using a managed service like AWS Glue), then you may end up faced with the prospect of running Apache Hive.

When I first ran the Hive image through our image scanner of choice, Trivy, it came up with literally about 800 vulnerabilities, including a comical number of critical severity vulnerabilities, such as RCEs (Remote Code Execution vulnerabilities). If you are an on-prem software vendor, as my employer is, this is a showstopper, because your biggest and most important customers will struggle to onboard your product.

For this page though and for the purposes of this example, I can't be bothered to replicate the nonsense I have done at work. Instead, I will demonstrate what is possible using some of Gradle's most fundamental but also least understood constructions: Configurations and Task types which manipulate Files and FileCollections.

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

With this build logic in place, when building our Docker image based on Apache Hive, part of the Dockerfile included instructions for overwriting the JAR files in the image with these downloaded JAR files having newer versions of the same dependencies. Of course, this doesn't work if you have a dependency with a breaking change, so if Hive is actually never updated, forking it may be necessary at some point. This is a productivity risk that we live with.

## A JAR included in an image you build has shaded classes of dependencies that need to be replaced by classes from a newer version of the same dependency

#### Concrete description of the problem

As above, my use case is in patching Apache Hive so that it is acceptable to run in our customers' environments.

To illustrate the problem, here is a real example. Within the Apache Hive Docker image there is a JAR file called the `hive-exec.jar`. This is a shaded JAR file which contains class files for a number of its dependencies. Unfortunately, some of those dependencies are old and have published severe vulnerabilities. At the same time though, we need this JAR for the program to function. The vulnerable dependencies are Apache Avro, Avatica Core, Aircompressor, Protobuf, and Guava. We need to replace the shaded class files in the hive-exec JAR for all of these dependencies with class files coming from newer (but compatible) versions.

Now obviously other solutions exist, such as forking Apache Hive, or re-architecting the product to not require Apache Hive. These options come with their own trade-offs. Thankfully, with a bit of build wizardry, we have the option of doing this, which we think was probably faster and easier than either of these other choices.

#### The solution in bullet points

1. Download the JAR ("base JAR") you need to reconstruct. Use a Configuration and the `dependencies { ... }` block.
2. Download the replacement versions of the dependencies (in JAR form) that you need to replace in the base JAR. Use a Configuration and the `dependencies { ... }` block.
3. We have a task of type `Sync` to put the base JAR in a directory so we can start working on it as a file rather than some abstract dependency.
4. Unzip the base JAR, removing all the class files you want to replace from the unzipped JAR. You also may want to remove any generated metadata files that are used by vulnerability scanners so that vulnerability scans aren't just based on outdated dependency metadata. This is a task of type `Sync` using `zipTree(...)`, and includes an `exclude(...)` in the task's configuration.
5. Rezip the base JAR, which is now missing many class files. This is a task of type `Jar`
6. Rebuild the JAR file by combining the rezipped base JAR and the downloaded replacement dependency JARs. This is a task of type `ShadowJar`.
7. Tada! You have replaced the vulnerable shaded classes of this JAR file with patched versions of the same classes, and now you have a new JAR file.

I haven't included the code here because it's quite tied into some of our internal structures and build logic libraries, so it wouldn't be immediately comprehensible, and I don't want to do the work to unwire it all and then test if it still works. I also don't want to put code here that I haven't tested for correctness. The description above does really summarise what we do.

## An important note on automatic tests

In the last two cases, we are modifying third-party software after it has been released. This means the quality control that they may have done prior to shipping the software no longer applies to what we are running, due to the internal JAR transformations we've done. This is where I think a different and more expansive kind of testing can help.

Quite often, developers say "Don't test third-party dependencies", by which they mean the database, the container runtime etc. I see this slightly more subtly. If you can write fast, deterministic, easily maintainable tests that give you great confidence by incorporating your third-party dependencies, I think you should do it. However, if those tests would be flaky, slow and burdensome, then you should not do it. With modern hardware and software, I find that it is often possible to write nice tests that incorporate certain elements of your software's runtime that traditionally have been excluded from the scope of "unit tests" by puritans. These include the database or the container runtime. My suggestion is that you experiment and do what is most delightful for you and your team.

---
Created on 2025-05-25

Updated on 2025-05-25
