# Unit Testing for Jenkins Pipeline Libraries

This is my guide to writing unit testable Jenkins shared pipeline libraries, which allows you to support greater complexity and agility in your CI system. I first developed this practice in 2020 with the help of my friend [Adrian Bärtschi](https://github.com/adrianbaertschi).

## Motivation

Almost everyone now uses automated CI pipelines. Originally they just ran the build, but in contemporary development ecosystems, pipelines have a diverse range of responsibilities, such as:

- Producing derivative build artifacts, like Docker images
- Publishing build artifacts to an artifact repository
- Deploying components into various environments
- Integrating other verification tools, like static code analysis, performance
  testing and mutation testing
- Audit compliance

The behaviour of these complex custom pipelines is determined by some kind of pipeline configuration. In this article, I'll use a Jenkins pipeline, with the pipeline definition written using Groovy. There are similar "pipeline-as-code" solutions for other CI servers, such as [Gomatic](https://github.com/gocd-contrib/gomatic) for [GoCD](http://www.gocd.org/). TeamCity allows you to define your pipelines using [its Kotlin DSL](https://www.jetbrains.com/help/teamcity/kotlin-dsl.html).

### The challenge of writing automated tests for pipeline libraries

One of the challenges with automated testing for pipeline libraries is that many of the behaviours that you expect from a pipeline don't lend themselves so obviously to unit testing. For example - publishing an artifact in a remote artifact repository.

Additionally, your code may depend heavily on behaviour that is only available when run on a real CI agent. In Jenkins, an obvious example might be invoking shell commands. Invoking shell commands within a replicated environment for a unit test would require some fairly heavyweight setup, although it could be technically feasible.

### The costs of untested code still apply to pipeline libraries

Based on the above challenges faced by pipeline library maintainers, almost all the pipeline library code I've seen exists without having any kind of automated testing. The code is only exercised during manual testing, done on an actual CI agent. The different branches are tested individually, and in general, the cost of change is quite high. If you choose not to accept a high cost of change, then consumers will pay. Bugs can be missed, and pipelines may break even due to minor issues like typos. Code review will not save you from these things.

For changes that have the potential to affect many behaviours of the pipeline library, the manual testing phase will have to cover everything. This will provide a huge barrier to broadly scoped refactorings which would otherwise have the potential to greatly improve your experience of making changes.

A lack of tests will also discourage other developers from making changes to the pipeline library. This will make existing maintainers into bottlenecks, which is an unfortunate position to be in. This compounds with the cost of change, because not only are maintainers in a position where they are in-demand bottlenecks, but also they can't safely make refactorings which could make it possible for non-maintainers to make changes.

This problem of bottleneck maintainers is especially bad for teams where the pipeline library maintainers are "part-time", that is, they are actually meant to be doing feature delivery, but they are also the team's de-facto "CI person". A lack of tests will cement this person's role as full-time pipeline library maintainer by making the code inaccessible to other people, who cannot safely make changes.

## Put a build tool around your pipeline library code

My friend Adrian Bärtschi introduced me to this while we worked together. We used Gradle to define a project layout that enabled us to develop the pipeline library like a normal JVM codebase, including remote dependencies and test sources.

We organise our pipeline library code like this:

* build.gradle
* settings.gradle
* src
    * some_package
        * MyPipeline.groovy
* test
    * some_package
        * MyJunitTest.java
* vars
    * entryPoint.groovy

Our `build.gradle` looks like this:

```
plugins {
  id "groovy"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation "org.codehaus.groovy:groovy-all:2.4.15"

  testImplementation "org.junit.jupiter:junit-jupiter:5.4.2"
  testImplementation "org.mockito:mockito-core:3.5.13"
  testImplementation "org.hamcrest:hamcrest:2.2"
  testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine"
}

test {
  useJUnitPlatform()
}

sourceSets {
  main {
    groovy {
      //noinspection GroovyAssignabilityCheck
      srcDirs = ["vars", "src"]
    }
  }

  test {
    java {
      //noinspection GroovyAssignabilityCheck
      srcDirs = ["test"]
    }
  }
}
```

With this setup, you can write you pipeline library as-normal.

Something to note is that we're using Java for the test sources, but Groovy for the main sources. This is because we wanted to Junit for the tests, which is a tool we're really familiar with. In general, Java is also a language we are more familiar with, so we thought we'd rather use that. In practice, it probably doesn't matter much what you go with. Gradle provides an extensible build language, such as the `sourceSets` construction, which enables you to pick-and-choose based on whatever works best for your team and your context.

## Wrap 3rd party interfaces using an Adaptor class

For the interfaces that can only be realistically implemented on a CI agent, define the interface clearly and position it in your code such that you can inject test doubles for that behaviour.

Below is an example of a Jenkins pipeline library class which wraps the CI agent runtime environment in order to provide a testable interface.

The `Object jenkins` which is injected as a dependency in the constructor is the object which access to all the default methods available within the CI agent runtime environment. In Jenkins, this includes any methods or fields provided by Jenkins plugins and by Jenkins itself.

```groovy
// Jenkins.groovy

/**
 * An example of a simple wrapper object which delegates to the object 
 * representing the CI agent runtime environment. This object can be mocked in
 * tests so that all the code in the pipeline library can be exercised locally
 * in unit tests.
 */
class Jenkins {
    private final Object jenkins

    Jenkins(Object jenkins) {
        this.jenkins = jenkins
    }

    void node(Closure closure) {
        jenkins.node(closure)
    }

    void stage(String name, Closure closure) {
        println("--- Stage: ${name} ---")
        jenkins.stage(name, closure)
    }

    void println(String text) {
        jenkins.echo(text)
    }

    void sh(String script) {
        jenkins.sh(script)
    }
}
```

## Only write tests that give you confidence

In a context where almost all the behaviours you want to test are dependent on an external interface, it can be tempting to write tests which use mocks to bake in the current implementation, rather than write tests which verify behaviour that really gives you confidence.

An example of a really simple test that gives me a very basic level of confidence is a test that just exercises all the code and makes sure that there are no typos or dynamic typing mismatches.

A more complicated test might stub Jenkins' `findFiles` to provide a list of files in the same format as is returned by Jenkins agents' runtime, and then assert that some kind of transformations and filters were happened correctly.

## Invest in really understanding the CI agent runtime

One of my favourite things about TDD is that you can't do it well without understanding the problem domain. It will be impossible to write many useful tests for your pipeline library if you don't understand the behaviours that you depend on, because you need to be able to stub the behaviour correctly where appropriate, and you need to know which interactions to write mock object verifications for as assertions.

For this reason, I recommend reading up on the behaviour of the out-of-the-box Jenkins behaviours, and the behaviour of the add-on plugins which your pipeline library depends on. Understanding them well will help you to write a pipeline which works nicely, and where the tests you have for it give you confidence in your changes.

## Example of a test

Here's an example of a test from a pipeline library I wrote not too long ago as a personal project, while I was doing more exploration practically in the area of unit testing for pipeline libraries.

```
package stage.clone;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import pipeline.JobParameters;
import pipeline.MockJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

public class CloneStageTest {

    private final JobParameters jobParameters = new JobParameters("https://github.com/robmoore-i/AccountingCalisthenics");

    @Test
    void returnsTheCodeDirectoryRelativePath() {
        MockJenkins jenkins = new MockJenkins();
        CloneStage stage = new CloneStage(jenkins);

        assertEquals("code", stage.run(jobParameters));
    }

    @Test
    void ensuresTheCodeDirectoryIsDeletedBeforeCloning() {
        MockJenkins jenkins = new MockJenkins();
        CloneStage stage = new CloneStage(jenkins);

        stage.run(jobParameters);

        InOrder inOrder = Mockito.inOrder(jenkins.mock);
        inOrder.verify(jenkins.mock).sh("rm -rf code");
        inOrder.verify(jenkins.mock).sh(startsWith("git clone"));
    }

    @Test
    void clonesTheGivenRepository() {
        MockJenkins jenkins = new MockJenkins();
        CloneStage stage = new CloneStage(jenkins);

        stage.run(jobParameters);

        verify(jenkins.mock).sh("git clone " + jobParameters.githubUrl + " code");
    }

    @Test
    void startsTheStage() {
        MockJenkins jenkins = new MockJenkins();
        CloneStage stage = new CloneStage(jenkins);

        stage.run(jobParameters);

        verify(jenkins.mock).stage(eq("Clone"), any());
    }
}
```

## Summary

- Writing automated tests for pipeline libraries can be challenging

- Growing your pipeline library code without tests will result in the same
  problems as you would see from any other codebase that is growing without
  tests.

- There are a few techniques you can apply which will make writing tests for
  your pipeline library easier, such as:
    - Using a build tool, like Gradle
    - Wrap 3rd party interfaces using an Adaptor class
    - Only write tests that give you confidence
    - Invest in really understanding the CI runtime

---
Created on 2022-05-28

Updated on 2023-03-09
