import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.GitCheckoutBranch
import docbuild.shell.UntrackedShell
import java.time.LocalDateTime.now

plugins {
    id("wiki.shell")
    id("wiki.mkdocs")
}

tasks {
    val mkdocsBuild by existing(MkdocsBuild::class)

    val gitStatusTask by registering(UntrackedShell::class) {
        cmd.set(listOf("git", "status", "--short"))
        outputFile.set(layout.buildDirectory.dir(name).map { it.file("git-status.txt") })
    }

    val gitCheckoutPublicationBranch by registering(GitCheckoutBranch::class) {
        mustRunAfter(mkdocsBuild)

        gitStatus.set(gitStatusTask.flatMap { it.outputFile }.map { it.asFile.readText() })
        branch.set("publication")
    }

    val gitMergeMain by registering(UntrackedShell::class) {
        mustRunAfter(gitCheckoutPublicationBranch)
        cmd.set(listOf("git", "merge", "main"))
    }

    val syncMkdocsToPublishedDirectory by registering(Sync::class) {
        mustRunAfter(gitMergeMain)
        from(mkdocsBuild)
        into(rootProject.layout.projectDirectory.dir("published"))
    }

    val gitCommitPublication by registering(UntrackedShell::class) {
        mustRunAfter(syncMkdocsToPublishedDirectory)
        cmd.set(listOf("git", "commit", "-am", "Published at ${now()}"))
    }

    val gitPush by registering(UntrackedShell::class) {
        mustRunAfter(gitCommitPublication)
        cmd.set(listOf("git", "push"))
    }

    val gitCheckoutMainBranch by registering(GitCheckoutBranch::class) {
        mustRunAfter(gitPush)
        gitStatus.set(gitStatusTask.flatMap { it.outputFile }.map { it.asFile.readText() })
        branch.set("main")
    }

    val gitCurrentBranch by registering(UntrackedShell::class) {
        cmd.set(listOf("git", "branch", "--show-current"))
        outputFile.set(layout.buildDirectory.dir(name).map { it.file("git-branch.txt") })
    }

    val mkdocsPublishPreflightCheck by registering {
        val currentBranch = gitCurrentBranch.flatMap { it.outputFile }.map { it.asFile.readText() }
        inputs.property("currentBranch", currentBranch)
        doFirst {
            if (!currentBranch.isPresent || currentBranch.get().trim() != "main") {
                throw RuntimeException(
                    "Refusing to publish docs due to being on incorrect branch:\n${currentBranch.get()}---"
                )
            }
        }
    }
    mkdocsBuild.configure {
        mustRunAfter(mkdocsPublishPreflightCheck)
    }

    register("mkdocsPublish") {
        dependsOn(
            mkdocsPublishPreflightCheck,
            mkdocsBuild,
            gitCheckoutPublicationBranch,
            syncMkdocsToPublishedDirectory,
            gitCommitPublication,
            gitPush
        )
        finalizedBy(gitCheckoutMainBranch)
    }
}
