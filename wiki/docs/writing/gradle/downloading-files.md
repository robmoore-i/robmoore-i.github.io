# Downloading files

This article is about how to download arbitrary files for use in your Gradle build.

What I want you to take away is that you don't need to mess about with any third-party plugins to download files in Gradle. There are perfectly good ways of downloading files using mechanisms that are built into Gradle.

The ways I mention are:

- Use a custom repository
- Use `resources.text.fromUri`
- Use a custom task definition
- Use the `de.undercouch.download` plugin
- Use Kotlin/Groovy code directly

### Download the file as a dependency using a custom repository

This is the method I would recommend using by default if you have the patience for the syntax, because it is the most idiomatic, provides the best configurability, and is fully compatible with Gradle's various build acceleration features.

I describe in detail how to use it in [my page on custom repositories](/writing/gradle/custom-repositories).

### Download the file using `resources.text.fromUri(uri)`

This method is attractive because it doesn't require a third-party plugin, executes lazily in the configuration phase if required by the build, and most notably, it is succinct.

Unlike using repositories though, I don't know if/how it's possible to configure this to provide authentication, dedicated HTTP proxies or other sorts of things.

Another way in which this approach differs from using repositories in that it creates a _detached configuration_, which essentially means that it populates a _configuration_ ([Gradlespeak for 'a collection of files'](https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.Configuration.html)) with the downloaded file but without associating the configuration with a _dependency_ of the project. My explanation of this is woolly because my understanding is woolly.

Here's how you can use this in a project build script:

```
val textResource = resources.text.fromUri("https://getsamplefiles.com/download/txt/sample-1.txt")
```

You can then use it in tasks, for example:

```
tasks.register("printText") {
    // Note: We need to redeclare project build script variables as 
    //       local variables within a task definition in order for 
    //       the the task to be configuration-cache compatible.
    //       This is the case for any top-level variable you define 
    //       in a project build script.
    val resource = textResource
    doLast {
        val text = resource.asFile().readText()
        logger.lifecycle(text)
    }
}
```

### Write your own custom task type for downloading files

For example, you might define a task for downloading files from S3 using specific credentials using the AWS Java SDK. I think this approach can make sense for cases like that. Indeed, this example comes from one of the builds I work on. I'm sure there are other cases where a similar approach can make sense. This is an adequate escape hatch when configuring a custom repository for a specific file download proves to be too difficult or complicated.

To do this, you need to write your own plugin and [define a custom task type within it](/writing/gradle/custom-task-types). In [my page on Gradle monorepo structure](/writing/gradle/gradle-monorepo-structure) I describe how to set up a custom plugin.

### Use the 'de.undercouch.download' Gradle plugin and register Download tasks

This approach is by far the most common. I encourage you to consider the above two approaches before jumping straight to this. Before I joined Gradle and had some time to accumulate tribal knowledge about the build tool, this is the only approach I was aware of, because it's the only approach that is easily findable using a search engine.

A number of online tutorials already exist for this plugin. To summarise, you need to apply a plugin, and then register tasks of type Download. You can create your own custom task type to wrap the plugin-provided type if you wish, which I have seen work before too, although fundamentally this is the same approach.

### Using plain old Kotlin/Groovy, with or without 3rd party libraries

This is fairly unhinged - almost certainly don't do this. You can control whether your code runs either in the configuration or execution phase by placing your code either within the action of a task you define (execution phase), or just dumping it into a project build script so that it will be run whenever Gradle evaluates that project (configuration phase). In either case, there are more idiomatic options than trying to roll your own file download code.

---
Created on 2025-08-09

Updated on 2025-08-09
