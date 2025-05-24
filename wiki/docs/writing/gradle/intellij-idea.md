# Intellij IDEA

**Introduction: Using Gradle from Intellij IDEA**

Intellij IDEA is a big, complex, powerful tool, solving a problem that is harder than it looks from the outside. Gradle is similar in this way. Put them together, and you have a recipe for a horrible, overcomplicated experience. There are a few things you can do to have a better time.

The page describes a few pitfalls that you should avoid, and virtues that you should strive towards. I update it opportunistically whenever I see something that makes me think everyone should know this. In that regard, it is similar to my page on [Insider Tips](/writing/gradle/insider-guidelines).

### Intellij says "Unresolved reference"

In a Gradle project, this problem should be solved by a combination of adjusting the build, and synchronizing Intellij. The instructions you need to issue to Intellij may be different in different cases.

Here are some items in the knowledge toolbox I use to solve this problem:

### Avoid using "api" dependencies in Gradle.

These can be replaced with "implementation" dependencies, which lack the pitfalls and carry less risk of introducing wasteful, unused dependencies between projects, which slow down your build.

### Do not use the Intellij tooltip action "Add 'foo' to classpath"

This is nonsense, unless Intellij is going to edit your Gradle build files. When Intellij takes this option, what it is really doing is modifying its own module dependency representation and ignoring the one that it ordinarily recieves from Gradle. You should aim to update the Gradle build so that it supplies the desired dependency information to the IDE, rather than tricking the IDE into believing that the build is defining dependencies in the way you want, when in truth it isn't.

### Triggering an IDE sync is often insufficient, you must also run "Repair IDE"

Once you have made a change to the build definition that you think might fix your dependency situation, you need to then:

1. Trigger an IDE sync. I do this so often that I have it bound to a custom shortcut. I use Cmd-Shift-7.
2. Trigger "Repair IDE". Again, I have this on a custom shortcut. I use Cmd-Ctrl-I (I as in IKEA).

I like the "Repair IDE" flow because it offers you escalations after each attempt. If you run it initially and it doesn't fix your problem, when you think it should, then it will offer to "Rescan Project Indexes" as a next step. As the next step after that, I think it asks you to restart IDEA. I rarely have to go further than the first step, which I think just reloads all build files and project files from disk, although I don't actually know.
