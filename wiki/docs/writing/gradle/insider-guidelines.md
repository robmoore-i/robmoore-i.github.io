# Insider guidelines

These are some things I've learned since working at Gradle, that I don't know for sure I would have ever learned from just reading the docs as a Java developer anywhere else.

### Use the Kotlin DSL

As much as the magic and mystery of Groovy is fun and exotic, most of us want to have a standard, tool-assisted programming experience.

The trade-off is that Kotlin requires compilation, and therefore is slower. For a very large build, Groovy DSL scripts will result in a faster overall build. You probably don't have that problem. Your problem is probably that you don't understand your build, or your build is incorrectly defined and you have to frequently run `clean` to deal with incorrectly defined inputs and outputs in misconfigured tasks from third party plugins that are doing insane things. Your problem is possibly also that your build does a lot of unnecessary work because you aren't taking advantage of the features of Gradle that enable your build to be much faster than it is. In the big, prioritised list of "things that could speed up your build", I suspect (although this is just my uninformed opinion) that using Groovy DSL realistically belongs quite low on that list.

### Don't have a root project build

At the root of your Gradle build, you need a `settings.gradle(.kts)`. Don't have `build.gradle(.kts)` at this level though, because it ends up being a gravity well for all sorts of nonsense that you don't want, like `subprojects { ... }` and `allproject { ... }` blocks.

### Don't use `subprojects {}` or `allprojects {}` blocks

These constructions defeat optimizations that I am not smart enough to understand, but which are pests in a way that I do understand, which is that they provide poor locality for build logic. They enable the root project build to influence the build logic of all other projects in a multi-project build in ways that are difficult to reason about. That property is probably related to why these blocks are bad for performance, although I'm not completeley sure.

### Running `clean` should only be done if you have a serious bug in your build

I pretty much never use the `clean` task. To habitually run it is a bad habit that is probably hurting your development experience, unless you are working within a build that is broken. If you live with such a broken build, you should get on top of that because it is really hurting your build time.

Your nuclear option is `git clean -fdx`. If this doesn't fix the problem then God help you.

### If you are editing build-logic, you need to run IDEA sync often

I often have the following development flow: Apply a new plugin to a project build script => immediately run IDEA sync => start using the types and Gradle objects defined by the plugin I just applied. If I didn't run that sync, I wouldn't be able to use those types in the IDE without it shouting in red at me. For builds with long syncs, this is a painful experience.

I have written [a dedicated page](/writing/gradle/kotlin-dsl) for some things I've learned about using the Kotlin DSL.

### Don't use buildSrc, use included-build plugins

Like a root project build script, buildSrc can quickly become a dumping ground for all sorts of things. Much worse, it is terrible for your build's performance, because every single change to buildSrc invalidates work avoidance for subsequent builds. On the Develocity project, this was a terrible toll for a long time, until the wizards of the developer productivity team initiated and sustained a migration to included-build plugins, taking many months and consuming much effort. Fortunately, this is the kind of migration project which yields progressive benefits: Things get slowly better as more is migrated to included-build plugins.

The reason included-build plugins are better than buildSrc, and indeed another reason why using plugins is better than using `subprojects` and `allprojects`, is that when a central, global source of build logic changes, such as buildSrc, it forces recompilation for all project builds. For a project with many modules, this is massively expensive: All developers and CI machines must do this. If these sources don't exist on the other hand, a change in a single included-build plugin forces the recompilation only of those modules that transitively apply it - much less expensive!

You can of course define Gradle plugins in buildSrc, and this is definitely much better in terms of build logic organisation and performance than using `subprojects` or `allprojects`, but it is worse than defining plugins in an included build, because changes in buildSrc plugins invalidate more projects than just those that apply the changed plugin.

### When sharing task outputs across projects, use attribute matching

There are two safe ways to share task outputs across projects. I say "safe" because of course you can always just pass the correct output path to a consuming task and cross your fingers that Gradle schedules the producing task before the consuming task (note: that is not a good idea at all).

The two safe ways are:
- Declaring a dependency on another project's exported configuration by name (i.e. using the name of the configuration within the producing project)
- Declaring a dependency on another project, and declaring a common attribute in both the exported (i.e. consumable i.e. producer) configuration and the imported (i.e. resolvable i.e. consumer) configuration.

Both of these ways are demonstrated in a working example in my [gradle-share-outputs-between-projects](https://github.com/robmoore-i/gradle-share-outputs-between-projects/tree/kotlin) GitHub repository.

Favour attribute matching over referencing configurations by name, because using attribute matching makes it more convenient to tightly couple references to the same exported configuration, by using shared code (i.e. in a plugin) for attribute values. Tightly
coupled references to the same thing are desirable.

### Use Java Toolchains to manage target JVM versions

[Understand and use Java Toolchains](/writing/gradle/jvms#java-toolchains). This will spare you significant grief, as long as you understand what it is doing and what it is not doing.

The version of Java used to invoke Gradle is not necessarily the same as the version of Java used to compile your Java code or run your tests. To control the version of Java used to compile your Java code, use Gradle's Java Toolchains feature. If you don't do this, Gradle will just use whatever JVM is being used to run itself when compiling and running Java code. Unless you have a good (and I imagine really quite interesting and unique) reason to preseve that behaviour, you should always be using Java Toolchains. I find the [official documentation](https://docs.gradle.org/current/userguide/toolchains.html) for this feature to be pretty useful and descriptive.

### The info and debug log levels for Gradle are mostly not very useful, and they probably won't help you, but `--stacktrace` is often helpful

When Gradle fails, it suggests you may want to rerun using `--info` or `--debug`. In my experience this is almost always just useless noise. Using `--stacktrace` however is often quite helpful to isolate the line of build logic code that causes the build failure. I use it quite often.

### Print to console during the build using `logger.lifecycle(...)`

You can write logs in Gradle that show up at the default log level by using `logger.lifecycle(...)`. Yes, the default log level is bizarrely called "lifecycle". I have never seen or heard of "lifecycle" as a log level in any context other than in the Gradle build tool.

### Make Intellij IDEA download the javadocs and sources for your third-party dependencies

It is enormously helpful to be able to navigate and inspect third-party code as if you had the sources for the version you're using checked out and available right in your IDE.

You can achieve this experience by applying the following build logic to your projects:

```
plugins {
    // other plugins
    idea
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

// other build logic
```

### Register tasks using `by` syntax

Use Kotlin's `by` syntax to avoid duplicating a task name when registering it. You can use the `val` later on in various other method calls in other tasks, such as `dependsOn(...)` or `mustRunAfter(...)`. You can also use it when declaring which task an artifact is built by, or use it as the source of a task input using `taskName.map { ... }` to turn it into a `org.gradle.api.Provider`.

Rather than writing:
```
val taskName = tasks.register<TaskType>("taskName") { ... }
```

Prefer to write:
```
val taskName by tasks.registering(TaskType::class) { ... }
```

### Don't always use the `tasks {}` block

There's nothing inherently wrong with the `tasks {}` block, but it's just not always the right fit for a build script. It encourages you to colocate all tasks together, which may not be the best organisation of code for the project's build logic. It also encourages new tasks to be added to the `tasks {}` block, even if a different organisation of code would be better. In this sense it can act as a gravity well, like `*Utils` classes (I hate `*Utils` classes). Sandwiching back to my first point though, it's sometimes fine and good to use the `tasks {}` block. It isn't a thought crime, like 

### Use the `jvm-test-suite` plugin for describing tests

Use the `jvm-test-suite` \[[official docs](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html)\] in Java projects for configuring your test task(s) and test dependencies. It makes it easy to colocate the build logic related to tests, and separate it from the build logic related to the application code. Some aspects of the plugin's configurability are a bit arcane (what's a "target"? It's probably described in the docs), but it nevertheless provides a better experience than using the provided-by-default configurations from the `java` plugin.

Rather than writing:
```
dependencies {
  implementation(...)
  ...

  testImplementation(...)
  ...
}

tasks.test {
  useJUnitJupiter()
  systemProperty("test.property", "foo")
}
```

Prefer to write:
```
plugins {
  `jvm-test-suite`
}

dependencies {
  implementation(...)
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) { 
      useJUnitJupiter()
      dependencies {
        implementation(...)
      }
      targets {
        all {
          testTask.configure {
            systemProperty("test.property", "foo")
          }
        }
      }
    }
  }
}
```

---
Created on 2024-05-15

Updated on 2025-12-13
