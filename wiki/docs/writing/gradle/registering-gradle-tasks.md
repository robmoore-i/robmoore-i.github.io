# Registering Gradle tasks

The official documentation on authoring tasks starts here: https://docs.gradle.org/current/userguide/more_about_tasks.html.

## Simple project builds don't need to register tasks

If you're like most Gradle users, you are probably writing some Java application, and using Gradle to create artifacts and run tests.

These most common journeys are supported by tasks which are added by Gradle plugins you have applied e.g. the `java` plugin adds the `compileJava` task.

Using these built-in tasks from plugins is good, because it means that you can benefit from improvements to the implementation of these tasks without putting in any effort yourself.

You may have to configure these built-in tasks slightly, for example, you may have your source code in an unusual, different place, in which case you need to configure the `compileJava` task to compile the sources in that directory.

Configuring tasks in this way is also good. You still take advantage of improvements to the task implementation which happen over time, and you don't need to do anything, which is ideal. If you are configuring a task in an unusual way though, you run the risk of having difficulty in upgrading your plugin version later, if the plugin's public interface changes in an incompatible way.

## When might you need to register tasks

#### You are writing a one-off task

In your build, you might need to do one little thing e.g. run some shell script. You can do this easily in Gradle by writing an `Exec` task, or even a `DefaultTask` that uses `project.exec`.

For example:

```
# my-app/build.gradle.kts
plugins {
  java
}

# Runs the script my-app/foo.sh using bash.
tasks.register<Exec>("runFoo") {
  commandLine(listOf("bash", "foo.sh"))
}
```

Alternatively, you may need to do a custom thing. You might be tempted to do this:


```
# my-app/build.gradle.kts
plugins {
  java
}

tasks.register<MyCustomTaskType>("doThing")

open class MyCustomTaskType : DefaultTask() {
  @TaskAction
  fun doThing() {
    ...
  }
}
```

I have come to dislike this and pretty much never do it. I don't place any overhead cost on defining a custom plugin even if just to to hold a custom task type and nothing else. It improves the declarativity of the project build script, making it easier for me to read and understand at a glance, and also puts the task type in its own source set in the project, where I can, for example, write tests for it, or factor it into multiple different objects, perhaps some completely separated from Gradle. It also opens the door more clearly for re-use in other projects.

#### You are writing a plugin

Sometimes you want your build to do something that is not provided by any plugin you can find, or the plugins that are available are rubbish, or you can't use them for whatever reason.

In this scenario, you have reached the point where you may need to author your own plugin, and therefore author the corresponding tasks.

```
# my-app/build.gradle.kts
plugins {
  java
  
  # My custom Docker plugin
  id("com.mycompany.docker")
}
```

For example, there are many publicly available Gradle plugins for Docker out there, but as of this writing (early 2024) none of them are sufficiently good or general purpose enough that I would choose them when I want to work with Docker images in my Gradle build, which I often do.

### Library plugins

A library plugin is a plugin created specifically to hold custom task definitions. This is a fairly uncommon pattern in the community of Gradle users, but it's one a really like and would like to proselytize among all Gradle users.

Traditional Gradle plugins work like frameworks. The plugin defines everything and whrn a project build applies that plugin, they pick up everything as defined already by the plugin. The project build might configure a few things, but in general the plugin is quite rigid in the interface that it offers.

The kind of plugins I like to write, which I refer to as 'library plugins' are different. The plugin configures little-to-no build logic, but the application of the plugin is expected to bring in a sweeping cast of helpful Gradle constructs, especially task definitions.

## Where to define new tasks

When you author your own tasks, you can choose to write them within project build scripts (i.e. in my-app/build.gradle.kts), or within custom plugins. The most accessible approach is to write all your build logic in your project build, because you don't need to know anything about build logic organisation to do that - you just dump your code into the project build script. Sometimes this is fine. It's a matter of taste and judgement, which you can pick up with a small amount of practice of using both locations for custom task definitions.

In general, I avoid defining new task types (i.e. new classes which extend DefaultTask) within a project build, because doing so is ugly and unidiomatic. I mentioned this above too. A project build script should ideally contain only the application of plugins, configuration of build objects they define, and perhaps the creation of a handful of small bespoke tasks.

If I'm defining a task that is specific to the project, then I register the task in the project build script. If the task is generic, or I know or anticipate that abstracting it into a plugin would be useful, then I would define the new task in a plugin that is applied. The task is then registered by the application of the plugin. If the task needs to be configured for each project that applies it, then the project build script can do that.

To use an analogy of application programming in an object-oriented language, if I'm writing behaviour that is truly specific to the implementation of a specific class, it makes sense to capture that behaviour in a private method of that class. If however the implementation is a special case of a more generic behaviour that should be abstracted for re-use, I would at least consider extracting it into another class. To return from the analogy of application programming back to Gradle build development, if I want to add a task that is truly specific to a particular project's role in the build, then it makes sense to register it within the project build script. On the other hand if the task I want to add is actually a special case of something more generic that exists in other projects (and perhaps considering Kent Beck's [Rule of Three](https://en.wikipedia.org/wiki/Rule_of_three_(computer_programming))), then I might prefer to define the task in a plugin which is applied to the project instead, and have the project configure the task in a special way if necessary.
