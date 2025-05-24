# Custom task types

This short article shows a custom task definition, and uses it to describe how in general to author a useful custom Gradle task, whose cacheability properties you understand.

## The example task class

Here's a custom task type definition:

```
@DisableCachingByDefault
abstract class DockerBuild @Inject constructor(
    objects: ObjectFactory,
    private val execOps: ExecOperations,
    private val layout: ProjectLayout
) : DefaultTask() {

    @get:InputFile
    val dockerfile: RegularFileProperty = objects.fileProperty().convention(layout.projectDirectory.file("Dockerfile"))

    @get:InputFiles
    abstract val resources: ConfigurableFileCollection

    @get:Input
    abstract val t: Property<String>

    @TaskAction
    fun build() {
        val d = layout.buildDirectory.dir(name).get().asFile
        d.mkdirs()
        dockerfile.get().asFile.copyTo(d.resolve("Dockerfile"), overwrite = true)
        resources.forEach { it.copyRecursively(d.resolve(it.name), overwrite = true) }
        execOps.exec {
            commandLine("docker", "build", d.absolutePath, "-t", t.get())
        }.assertNormalExitValue()
    }
}
```

**Take note of these several aspects this class.**

### It is abstract

This is because Gradle is resposible for instantiating task instances at runtime, when they are needed.

### It has a constructor annotated with `@Inject`

Since Gradle is responsible for instantiating instances of the task class, it also provides a way to provide useful build-logic-related objects to its instances. For example, the above class uses `ExecOperations` for running arbitray shell commands, and it uses `ProjectLayout` to get a convenient reference for the build directory.

There are many other types that can be injected into a custom task class definition. Here are a few of the most useful ones:

- `ExecOperations`: For running shell commands.
- `ProjectLayout`: For convenient access to the project directory (i.e. the directory containing the `build.gradle(.kts)` file)
- `FileSystemOperations`: Provides access to some Gradle-defined file-related methods, like `copy`, `sync`, and `delete`.
- `ObjectFactory`: Can be used to instantiate various Gradle-defined types, such as the various kinds of `Property<T>` (`ListProperty<T>`, `MapProperty<K, V>`, `SetProperty<T>`, `RegularFileProperty`, `DirectoryProperty`), `ConfigurableFileCollection`, and generally any Gradle-registered type. This should only be used for creating these Gradle-specific types. In general, creating objects should be done by invoking a constructor.

### It extends `DefaultTask`

This is the only Gradle-defined task type that I have ever extended in a custom task type definition. That's not to say that using other Gradle-defined tasks as a base is a bad idea, but certainly it's not something I would expect to do often. `DefaultTask` is unopinionated and I like that.

### It declares its default cacheability

This task is not cacheable *by default*. A subclass of this task type could be cacheable, and I could even make a task of this type cacheable by explicitly setting it to be cachable using `cacheIf { ... }` when registering it.

If I wanted to absolutely preclude caching for this task, I would mark it `@UntrackedTask`. If I wanted it say it is cacheable, I'd mark it `@Cacheable`.

Note however that a task cannot be cacheable unless it declares at least one input and output. An input is required because without it you cannot compare an imminent execution to previous executions (well, you could, but Gradle doesn't). An output is required because without it, what would be taken from the cache? The notion of cacheability doesn't make sense without an output. Of course, you may want to skip work done by a task which doesn't have any outputs per se. In such a case, you can create an empty text file (or perhaps a text file summarising the work that was done) to use as your cacheable "output".

### Its fields are annotated to indicate their relevance to work avoidance

For tasks declared as cacheable, Gradle avoids work by taking a fingerprint (i.e. a hash) of a task's inputs and comparing it to a hash of inputs from previous task executions. If a match is found between the current input hash and a previous execution's hash, then the task is said to have found a cache hit, the execution is skipped, and the declared output artifacts corresponding to the identical task input hash are restored to the file system, for consumption either by you, or by other tasks.

This mechanism of work avoidance requires tasks to declare their inputs and outputs explicitly. This can be done either when registering a tasks (using the inputs.file(...) / outputs.file(...) methods and their friends), or within the definition of the task type, which I think is often better because it encapsulates the work avoidance role of certain configurable properties of the task.

If a property bears no relevance to caching, it should be annotated with `@Internal`. Otherwise, it should have an annotation such as `@InputProperty`, `@InputFile`, `@InputFiles`, `@InputDirectory`, `@OutputFile`, `@OutputDirectory`. Note that a property unfortunately cannot be an output. The way to get around this in Gradle if you need this capability, is to put the value in a file and mark that file as an `@OutputFile`.

### It has configurable inputs

The field `t`, corresponding to the docker build parameter `-t`, is configurable. It has a  type of `Property<String>`. This means that when registering the task, the build author will need to set its value by using `t.set(...)`, passing a `String`.

Note that for the `dockerfile` input, it has been instantiated to a property value directory using the `ObjectFactory`. This has been done so that the task class definition can include the conventional value for this input - a file in the project directory called "Dockerfile". This use of `ObjectFactory` to provide conventional values for properties can be done for both inputs and outputs.

### It has a method annotated with `@TaskAction`

There should exactly one method in a task class definition annotated with `@TaskAction`, and this is the method that Gradle will call to execute the task, if it needs to be executed. It needs to accept no arguments. It should have a meaningful name.

## Further notes

Custom task types should be declared within included-build plugins, as described in my longer article on [structuring a Gradle build](/writing/gradle/gradle-monorepo-setup).
