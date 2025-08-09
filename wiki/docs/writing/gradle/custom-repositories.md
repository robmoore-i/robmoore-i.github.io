# Custom repositories

This page describes how you can download any files using Gradle dependency management.

Downloading files using Gradle should be really easy, it shouldn't require any third party shit, and it should be compatible with all stable optimization features of the build tool. There is a technique that conforms to these basic constraints. It is pretty convoluted, and I have never seen official documentation for it. We use it in our build in several places, and I have applied it myself. It's satisfying once it works. This page will explain how to download arbitrary files from the internet using Gradle in a way that doesn't require you to apply any plugins, register any tasks and isn't a performance footgun.

## Terminology

### Ivy

Ivy refers to [Apache Ivy](https://ant.apache.org/ivy/) - a dated but highly influential dependency manager, developed as a subproject of Apache Ant.

### Configuration

The poorly named 'Configuration' in Gradle, is a subtype of the much better-named 'ConfigurableFileCollection'. That is, a Configuration is an object that just represents a bunch of files. That is the most common use of a Configuration, in my experience, and provides the simplest mental model.

#### Resolvable Configuration vs Consumable Configuration

Configurations are either resolvable or consumable.

Resolving a configuration refers to taking out its files in order to do something with them. So a resolvable configuration is one that you can directly use as a collection of files. You use resolvable configurations when you want to declare and make use of a dependency.

The "consumable" in "consumable configuration" refers to dependency consumption. A consumable configuration can be exported from one project to another as a project dependency (i.e. `implementation(project(":foo"))`). It can't be resolved, so it isn't useful to the project that produces it. You use consumable configurations when you want to export the output of a task to other projects in your multi-project build.

### Artifact Transform

To be honest I still don't quite understand what a Gradle artifact transform is, but I will present the inevitably incorrect mental model I use (in the spirit of "all models are wrong, some are useful"). There is [official documentation](https://docs.gradle.org/current/userguide/artifact_transforms.html) covering this, which I think explains the concept to some degree.

An artifact transform is a Gradle object that knows how to perform an operation on a dependency, and in doing so, alters a piece of its associated metadata. When Gradle resolves a requested dependency, it requests it with certain metadata, and this metadata is used by Gradle to determine whether it can satisfy a dependency consumer's request by transforming an available dependency using a registered artifact transform.

## Custom repository declaration

This custom repository declaration tells Gradle that it can find artifacts with the group "trinodb" in this specified Ivy repository. It has a base URL and a path pattern, based on the coordinates provided in the dependency block.

The URL to download one of the files that this repository declaration downloads is: `https://github.com/trinodb/grafana-trino/releases/download/v1.0.6/trino-datasource-1.0.6.zip`.

```
repositories {
    exclusiveContent {
        forRepository {
            ivy {
                url = uri("https://github.com/trinodb/grafana-trino")
                patternLayout { artifact("releases/download/v[revision]/[artifact]-[revision].[ext]") }
                metadataSources {
                    artifact()
                }
            }
        }
        filter {
            includeGroup("trinodb")
        }
    }
}
```

To download and use the file we want to download, we need to declare a dependency on it. To do that, we need a resolvable configuration to attach our dependency to.

This code creates a resolvable configuration, and declares that the files inside it need to be directories. Gradle will not populate this configuration with non-directory files.

```
val trinoDatasourcePlugin by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
}
```

This code first registers an in-built artifact transform that converts a zip archive into a directory. The `UnzipTransform` artifact transform is in-built in Gradle. I'm not sure why you therefore need to declare it explicitly, but evidently you do.

After regisering the transform, we declare our dependency. We say that our resolvable configuration has a dependency on an artifact with coordinates "trinodb:trino-datasource:1.0.6" and we are expecting a zip file. We also say that we do not want this dependency to be resolved with any of its transitive dependencies, because that doesn't make sense - we are downloading a single file.

```
dependencies {
    registerTransform(UnzipTransform::class) {
        from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.ZIP_TYPE)
        to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
    }

    trinoDatasourcePlugin("trinodb:trino-datasource:1.0.6") {
        artifact {
            type = "zip"
            isTransitive = false
        }
    }
}
```

To start actually using the files inside that unzipped directory that we've downloaded, we can register a task that uses the files. For example:

```
tasks.register("printDownloadedDirectoryPath") {
    val inputDir = configurations.named("trinoDatasourcePlugin").map { it.singleFile }
    doFirst {
        logger.lifecycle("Found file: " + inputDir.get().path)
    }
}
```

When I run this, I see that Gradle downloads the file for me, and the task prints out the text I expected:

```
Starting a Gradle Daemon...
...
> :printFiles > Resolve files of configuration ':trinoDatasourcePlugin' > trino-datasource-1.0.6.zip > 9.3 MiB/44.6 MiB downloaded
...
> Task :printDownloadedDirectoryPath
Found file: /Users/rob/.gradle/caches/transforms-4/6494f3751f7959da86816b0d7720b563/transformed/trino-datasource-1.0.6
```

If I look inside it, that directory has what I wanted:

```
rob@Robs-MacBook-Pro-2 custom-gradle-repositories-demo % tree /Users/rob/.gradle/caches/transforms-4/6494f3751f7959da86816b0d7720b563/transformed/trino-datasource-1.0.6
/Users/rob/.gradle/caches/transforms-4/6494f3751f7959da86816b0d7720b563/transformed/trino-datasource-1.0.6
└── trino-datasource
    ├── CHANGELOG.md
    ├── LICENSE
    ├── MANIFEST.txt
    ├── README.md
    ├── go_plugin_build_manifest
    ├── gpx_Trino_darwin_amd64
    ├── gpx_Trino_darwin_arm64
    ├── gpx_Trino_linux_amd64
    ├── gpx_Trino_linux_arm
    ├── gpx_Trino_linux_arm64
    ├── gpx_Trino_windows_amd64.exe
    ├── img
    │   ├── explore.png
    │   └── logo.svg
    ├── module.js
    ├── module.js.map
    └── plugin.json

3 directories, 16 files
```

## Notes

### The artifact transform aspect of this was entirely optional

The artifact transform is not an integral part of downloading dependencies in this way. It so happened that the example I chose from my work used it. Setting up a custom repository is simpler if you don't use any transforms. Of course, this example was also achievable without transforms. I could have registered a task of type `Sync` and configured it to decompress the downloaded zip file, rather than doing it using a transform.

### You should put custom repository declarations into plugins, not into project build scripts

This code comes from a plugin I extracted which I called `grafana-trino-datasource-plugin`. All it does is create and populate this configuration. I didn't want this in the project build script because it's so noisy and verbose and distracts you as a reader from what matters: This project build knows how to download this directory from GitHub and use it.

## Appendix - The complete example

This script plugin downloads a release artifact (a zip file) from a public GitHub repository and then transforms it into a directory for later use. In the real example, the directory is later added to a Docker image. For this page, I have added the `printDownloadedDirectoryPath` task to demonstrate how you might go about using the downloaded file in a task.

```
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.internal.artifacts.transform.UnzipTransform
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.registerTransform
import org.gradle.kotlin.dsl.repositories

plugins {
    base
}

repositories {
    exclusiveContent {
        forRepository {
            ivy {
                url = uri("https://github.com/trinodb/grafana-trino")
                patternLayout { artifact("releases/download/v[revision]/[artifact]-[revision].[ext]") }
                metadataSources {
                    artifact()
                }
            }
        }
        filter {
            includeGroup("trinodb")
        }
    }
}

val trinoDatasourcePlugin by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
}

dependencies {
    registerTransform(UnzipTransform::class) {
        from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.ZIP_TYPE)
        to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.DIRECTORY_TYPE)
    }

    trinoDatasourcePlugin("trinodb:trino-datasource:1.0.6") {
        artifact {
            type = "zip"
            isTransitive = false
        }
    }
}

tasks.register("printDownloadedDirectoryPath") {
    val inputDir = configurations.named("trinoDatasourcePlugin").map { it.singleFile }
    doFirst {
        logger.lifecycle("Found file: " + inputDir.get().path)
    }
}
```

---
Created on 2024-05-24

Updated on 2025-02-23
