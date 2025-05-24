# JVMs

This page contains tips regarding the use of Gradle and different JVM versions.

## Java Toolchains

Gradle is a Java program. When you invoke Gradle (e.g. using `./gradlew`), you are starting one or more Java processes (you can check your locally running Java processes using a command line tool suhc as `jps`).

Modern Gradle is compatible with Java 8 onwards. Running with the second most recent LTS Java release will give you the best experience, with the lowest likelihood of encountering bugs.

Gradle will run using whatever version of Java is specified in the `JAVA_HOME` environment variable passed to the process that invokes it.
- If you invoke Gradle from the command line using the wrapper (i.e. `./gradlew`), then your "default" `JAVA_HOME` value will be used. You can check this by running `echo $JAVA_HOME` from the same command line interface, or by running `env` and having a look through.
- If you invoke Gradle through your IDE (e.g. Intellij) then the JAVA_HOME environment variable passed to the Gradle process is controlled by the IDE. There should be a setting somewhere to configure this. There certainly is in Intellij IDEA.

The version of Java used to invoke Gradle is not necessarily the same as the version of Java used to compile your Java code or run your tests.

If you don't use the Java Toolchains feature to specify the version of the JVM to use when compiling code or running tests, then Gradle will just use whatever JVM is being used to run itself. Unless you have a good (and I imagine really quite interesting and unique) reason to preseve that behaviour, you should always be using Java Toolchains to explicitly specify the version of the JVM to use for compiling code and running tests. I find the [official documentation](https://docs.gradle.org/current/userguide/toolchains.html) for this feature to be pretty useful and descriptive.
