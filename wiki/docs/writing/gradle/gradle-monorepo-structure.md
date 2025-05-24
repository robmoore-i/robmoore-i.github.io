# Gradle monorepo structure

This tutorial will describe how I would generally go about structuring a Gradle monorepo project as someone who works at Gradle, and so knows the insider idioms of the tool fairly well.

## Start simple

The minimum viable Gradle build consists of a settings file, a single subproject and that subproject's build script. All the build logic is in Kotlin.

```
rob@Robs-MacBook-Pro-2 teashop % tree .
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── teashop.iml
└── teasite
    └── build.gradle.kts
```

```
/////////////////////////////
//// settings.gradle.kts ////
/////////////////////////////
rootProject.name = "teashop"
include("teasite")
```

```
//////////////////////////////////
//// teasite/build.gradle.kts ////
//////////////////////////////////
plugins {
    base
}
```

With this you can run e.g. `./gradlew :teasite:tasks --all`

> The root project's build script is not included in my minimum viable Gradle build. You don't need it, and in fact I would avoid adding one because it encourages you and your team to do things that hurt your configuration time, in particular, using `allprojects` and `subprojects` blocks. This is called [cross project configuration](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html#sec:convention_plugins_vs_cross_configuration).
{.is-info}

A practical consideration when editing build logic is that you should be running IDE syncs fairly frequently, so that Intellij can keep its model of your project up to date with the changes you're making to the project's structure. If you have a large build with bad IDE sync times, then this can hurt. At the same time, it is possible to batch together several changes that affect IDE sync, and the sync them all at once. I don't advise doing this unless you're confident with what you're doing.

## More projects with dependencies between each other

Add more projects by creating more directories under the root project directory, giving them build scripts, and including them in the root settings script. That's all there is to that. Something worth bearing in mind is that as you reach a high number of projects, your configuration time is going to increase. It can sometimes be appropriate to add additional source sets, rather than additional projects. I can also recommend using the [jvm-test-suites](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html) plugin for creating multiple test source sets in a project.

When you start to have many projects with dependencies on each other, there may come a point where you want to share something between projects that isn't a JAR. [This sample in the docs](https://docs.gradle.org/current/samples/sample_cross_project_output_sharing.html) gives a minimal example (sharing a single file) of the simplest way to do that.

> Gradle pros may encourage you to use something called variant-aware dependency selection and constrain your dependencies using attributes, rather than using the name of configurations directly. This is not hard to do, but unfortunately the docs on it are sparse and I don't want to get into that level of detail here. Perhaps I will submit a PR at some point to add that.
> {.is-info}

## Custom build logic

My favourite thing about Gradle compared to other build tools is that you can program your build as though it were any other JVM-language program. Gradle provides a framework for modeling tasks, their dependencies and the artefacts they produce. The framework implements work avoidance, and provides a few common build task behaviours out-of-the-box. Since you can provide your model as code, it can benefit from flexibility and reuse in the same way as any other program you write, meaning that as your build gets larger and more complex, you can define ever more powerful abstractions for modeling your build.

The documentation for this capability is unfortunately quite sparse. There are two main ways to do it, and the better way, which I will describe here, was introduced fairly recently.

The best way to add custom build logic to your Gradle monorepo is to use [included build plugins](https://docs.gradle.org/current/userguide/composite_builds.html#included_plugin_builds). That is, you have a directory underneath the root project, which is not a normal subproject. Instead, it is [an included build](https://docs.gradle.org/current/userguide/composite_builds.html#settings_defined_composite). This directory contains its own settings script, to indicate that it is its own Gradle build, and like its parent, this is a [multi-project build](https://docs.gradle.org/current/userguide/multi_project_builds.html). The projects it contains define script Gradle plugins, which you can use to extract abstractions from project builds within its parent, in order to reuse them across many projects.

> The more well-known way to provide custom build logic in a Gradle build is to [use buildSrc](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:packaging_a_plugin). This was the recommendation for a long time, but it has since become outdated. The reason for this is that any change you make to buildSrc invalidates up-to-date checks on every project in the build. As your build grows, this invalidation starts to become very expensive and makes it difficult to work productively on build logic.
>
> At even larger scale (i.e. literally thousands of subprojects), it can become economical to start publishing plugins to an internal repository and consuming them as external dependencies (i.e. using a [buildscript block](https://docs.gradle.org/current/javadoc/org/gradle/api/initialization/dsl/ScriptHandler.html)), rather than rebuilding the sources directly from within your monorepo. May you never experience that 🙏.
{.is-info}

Let's say I have a monorepo that contains two projects which both define AWS Lambda functions, `teasite` and `teapayments`:

```
rob@Robs-MacBook-Pro-2 teashop % tree .
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── teapayments
│   ├── build.gradle.kts
│   └── src
│       └── ...
├── teashop.iml
└── teasite
    ├── build.gradle.kts
    └── src
        └── ...
```

For both projects, we have a Gradle task that packages the code and uploads it as a zip to AWS for use in the lambda. So for both projects, the build script is defining the same AWS dependencies, and the same custom task for uploading the lambda to AWS. The two project build scripts look almost identical. Here is one of them:

```
//////////////////////////////////
//// teasite/build.gradle.kts ////
//////////////////////////////////
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import javax.inject.Inject

plugins {
  id("java")
}

repositories {
    mavenCentral()
}

dependencies {
  ...
}

// Specify Amazon Corretto JDK 11
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

// Definition of a task that uploads an AWS Lambda zip
@UntrackedTask(because = "We want to run this every time it is invoked")
abstract class UploadLambda @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val functionName: Property<String>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val zip: RegularFileProperty

    @TaskAction
    fun upload() {
        execOps.exec {
            commandLine(
                "aws", "lambda", "update-function-code",
                "--function-name", functionName.get(),
                "--zip-file", "fileb://${zip.get().asFile.absolutePath}"
            )
        }
    }
}

// Task for putting the compiled Lambda's classes and dependencies into a zip of the right structure.
val buildZip by tasks.registering(Zip::class) {
    archiveBaseName.set("lambda")
    from(tasks.named<JavaCompile>("compileJava").flatMap { it.destinationDirectory })
    from(tasks.named<ProcessResources>("processResources").map { it.destinationDir })
    into("lib") {
        from(configurations.getByName("runtimeClasspath"))
    }
}

// Task for uploading zip to AWS
tasks.register<UploadLambda>("uploadLambda") {
  functionName.set("teasite")
  zip.set(buildZip.flatMap { it.archiveFile })
}
```

There are a few things going on here. There a toolchain definition, a custom task definition, and the registration of a couple of tasks. All of this is duplicated between the two projects in our build for no benefit, and you're not really thrilled about this.

There is a way for us to elegantly extract appropriate abstractions and make both builds differ only in the way that matters. In this case, the projects differ in two dimensions:
- They represent different Lambda functions
- Their dependencies might be slightly different depending on what they do.

The idiomatic way to extract common build logic is to use Gradle plugins. In this case, what we'd like is for both builds to declare that they are AWS Lambda functions by applying an appropriate plugin, and then configure some DSL that lets them specify the name of the function that they are. Below shows a nice possible outcome:

```
//////////////////////////////////////////////////
//// Possible future teasite/build.gradle.kts ////
//////////////////////////////////////////////////
plugins {
    id("teashop.aws-lambda")
}

awsLambda {
    functionName.set("teasite")
}

repositories {
    mavenCentral()
}

dependencies {
    ...
}
```

## Creating a plugin that does nothing

I think it's important to be able to take an arbitrary first step when working in unfamiliar terrain. In this section I'll show how to create a Gradle plugin in your monorepo that does pretty much nothing.

First, create the included build for your monorepo's custom plugins, as described at the start of this subsection. For the teashop example, I'll make a directory under "gradle", called "plugins", which results in having this structure:

```
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│   └── plugins
│       └── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── teapayments
│   ├── build.gradle.kts
│   └── src
│       └── ...
├── teashop.iml
└── teasite
    ├── build.gradle.kts
    └── src
        └── ...
```

```
/////////////////////////////
//// settings.gradle.kts ////
/////////////////////////////
rootProject.name = "teashop"

pluginManagement {
    // Specifies an included build for defining custom plugins in the multi-project build.
    includeBuild("gradle/plugins")
}

include("teasite")
include("teapayments")
```

```
/////////////////////////////////////
//// plugins/settings.gradle.kts ////
/////////////////////////////////////
rootProject.name = "plugins"
```

At this point, the teashop build specifies an included build called "plugins" from which to draw custom Gradle plugins. However, it currently doesn't contain any plugins. We will now add the "no-op" plugin: A plugin that does nothing.

I'll first create an empty subproject in the included build for Gradle plugins:

```
rob@Robs-MacBook-Pro-2 teashop % tree plugins
plugins
├── no-op
│   └── build.gradle.kts
└── settings.gradle.kts
```

```
/////////////////////////////////////
//// plugins/settings.gradle.kts ////
/////////////////////////////////////
rootProject.name = "plugins"
include(":no-op")
```

The next step is to turn the empty `no-op` project into a project that defines a Gradle plugin in Kotlin. This is not trivial, especially if you have never done it before. The first step is to apply the built-in `kotlin-dsl` Gradle plugin to the `no-op` project build:

```
////////////////////////////////////////
//// gradle/plugins/no-op/build.gradle.kts ////
////////////////////////////////////////
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
```

Next, I run an Intellij sync, wait for it to complete, and then use Intellij to generate the main Kotlin source root by hovering over the project in Intellij's Project panel, using 'New' via CMD-N (or CTRL-N on Linux/Windows), selecting 'Directory' and then 'src/main/kotlin'. Whether by this method or another, the resuting directory structure of the plugins included build should be this:

```
rob@Robs-MacBook-Pro-2 teashop % tree gradle/plugins 
plugins
├── no-op
│   ├── build.gradle.kts
│   └── src
│       └── main
│           └── kotlin
└── settings.gradle.kts
```

The last step towards making an included build Gradle plugin is making a Gradle script under the newly created src/main/kotlin directory. The file's name needs to end with `.gradle.kts`. I have seen, and myself use the format: `<org>.<plugin-name>.gradle.kts`. In this example, I will create the file `teashop.no-op.gradle.kts`. Inside it, I'm going to put some unimportant build logic that proves that the plugin is being applied.

```
////////////////////////////////////////////////////////////////
//// gradle/plugins/no-op/src/main/kotlin/teashop.no-op.gradle.kts ////
////////////////////////////////////////////////////////////////
tasks.register("sayHello") {
    doFirst {
        logger.lifecycle("Hello!")
    }
}
```

Without doing anything else, this plugin can now be applied by the projects of the build. For example:

```
//////////////////////////////////////
//// teapayments/build.gradle.kts ////
//////////////////////////////////////
plugins {
  id("teashop.no-op")
}
```

Which allows me to do this:

```
rob@Robs-MacBook-Pro-2 teashop % ./gradlew :teapay:sayHello

> Task :teapayments:sayHello
Hello!

BUILD SUCCESSFUL in 731ms
11 actionable tasks: 1 executed, 10 up-to-date
```

## Creating a plugin that does something

As is sometimes the case when going from an arbitrary first step to a meaningful follow-up step, the hard work is already done. From where we are with the `no-op` plugin, we need only to rename it and add some useful Gradle build configuration to the plugin, which is presently defined in `gradle/plugins/no-op/src/main/kotlin/teashop.no-op.gradle.kts`. Whatever Gradle code we add to that script will be ran for any project which applies the plugin. This includes any plugins that are specified for inclusion in the plugin. A plugin can even simply proxy other plugin(s), by containing only a `plugins { ... }` block.

In the AWS Lambda example above, there was a class definition inlined into the project build. When pulling that code into a plugin, I would not leave the class definition in the script, I'd move it to its own file. To do that, create a package underneath the Kotlin source root of the plugin, and define classes in it. You'll need to import those classes into the plugin script in order to use them. I'll show an example by refactoring the `teashop.no-op` plugin:

```
///////////////////////////////////////////////////////////////////
//// gradle/plugins/no-op/src/main/kotlin/com/teashop/SayHelloTask.kt ////
///////////////////////////////////////////////////////////////////
package com.teashop

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "By default you should say hello.")
abstract class SayHelloTask : DefaultTask() {

    @TaskAction
    fun sayHello() {
        logger.lifecycle("Hello!")
    }
}
```

```
////////////////////////////////////////////////////////////////
//// gradle/plugins/no-op/src/main/kotlin/teashop.no-op.gradle.kts ////
////////////////////////////////////////////////////////////////
import com.teashop.SayHelloTask

tasks.register<SayHelloTask>("sayHello")
```

As before:

```
rob@Robs-MacBook-Pro-2 teashop % ./gradlew :teapay:sayHello

> Task :teapayments:sayHello
Hello!

BUILD SUCCESSFUL in 1s
11 actionable tasks: 4 executed, 7 up-to-date
```

The final project layout is this:

```
rob@Robs-MacBook-Pro-2 teashop % tree .                    
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│   └── plugins
│       ├── no-op
│       │   ├── build.gradle.kts
│       │   └── src
│       │       └── main
│       │           └── kotlin
│       │               ├── com
│       │               │   └── teashop
│       │               │       └── SayHelloTask.kt
│       │               └── teashop.no-op.gradle.kts
│       └── settings.gradle.kts
├── gradlew
├── gradlew.bat
│   └── settings.gradle.kts
├── settings.gradle.kts
├── teapayments
│   ├── build.gradle.kts
│   └── src
│       └── ...
├── teashop.iml
└── teasite
    ├── build.gradle.kts
    └── src
        └── ...
```

## Patterns for plugins

Sometimes, a plugin is highly reusable and flexible. Other times, a plugin may be quite specific to one particular project build. Sometimes, a plugin registers tasks itself and the applying project doesn't need to do anything. Other times, it may be more appropriate for the plugin to instead add custom types onto the classpath of the applying project, so that the applying project can register and configure tasks for itself. Do whatever works in your context.

One of the idiomatic ways to organise Gradle plugins is to describe what a project *is*. For example, when applying the `teashop.aws-lambda` plugin to the `teasite` project, you might read that as "The teasite project is an AWS Lambda project". Of course, many built-in plugins don't conform to that idiom, so I don't feel at all constrained by it.

A pattern I often use is to define a script plugin that is completely empty, but defines useful classes and Kotlin functions that are added to the classpath of a applying project's build script. For example, if my `teashop.no-op` plugin was totally empty, I would still have been able to register tasks of type `SayHelloTask` within the teasite project, since it applies the no-op plugin.

Similarly, you can define a completely empty script plugin which brings along extension methods for existing Gradle types and puts them on your project builds' classpath. With this pattern it becomes possible to extend the Gradle DSL to provide abstractions of your choosing.

## Summary

So here are the elements that I think are important for the ideal, idiomatic layout of a monorepo whose build tool is Gradle:

- A multi-project build with no root build script.
- Shared build logic provided by plugins defined in an included build.

## Links and further reading

- A worked example of this structure, made by my dad by following this tutorial: https://github.com/ivanmoore/gradle-monorepo-setup
- Gradle source: https://github.com/gradle/gradle
- Gradle docs: https://docs.gradle.org
- Gradle site: https://gradle.org
- Part 1 of a 2-part series on Square's build woes: https://developer.squareup.com/blog/herding-elephants/
- Part 2 of a 2-part series on Square's build woes: https://developer.squareup.com/blog/stampeding-elephants
- A cute little trick you can do with Gradle (although it would be better if supplied by a plugin, and with tests): https://jonnyzzz.com/blog/2016/03/06/gradle-all-maven-runner/
- What I'm working on at Gradle: https://gradle.com
