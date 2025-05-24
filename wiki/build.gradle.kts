import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.GitCheckoutBranch
import docbuild.shell.Shell
import java.time.LocalDateTime.now

plugins {
    id("wiki.shell")
    id("wiki.mkdocs")
}

tasks {
    val mkdocsBuild by existing(MkdocsBuild::class)

    val gitStatusTask by registering(Shell::class) {
        cmd.set(listOf("git", "status", "--short"))
        outputFile.set(layout.buildDirectory.dir(name).map { it.file("git-status.txt") })
    }

    val gitCheckoutPublicationBranch by registering(GitCheckoutBranch::class) {
        mustRunAfter(mkdocsBuild)
        gitStatus.set(gitStatusTask.flatMap { it.outputFile }.map { it.asFile.readText() })
        branch.set("publication")
    }

    val syncMkdocsToPublishedDirectory by registering(Sync::class) {
        mustRunAfter(gitCheckoutPublicationBranch)
        from(mkdocsBuild)
        into(rootProject.layout.projectDirectory.dir("published"))
    }

    val gitCommitPublication by registering(Shell::class) {
        mustRunAfter(syncMkdocsToPublishedDirectory)
        cmd.set(listOf("git", "commit", "-am", "Published at ${now()}"))
    }

    val gitPush by registering(Shell::class) {
        mustRunAfter(gitCommitPublication)
        cmd.set(listOf("git", "push"))
    }

    val gitCheckoutMainBranch by registering(GitCheckoutBranch::class) {
        mustRunAfter(gitPush)
        gitStatus.set(gitStatusTask.flatMap { it.outputFile }.map { it.asFile.readText() })
        branch.set("main")
    }

    register("mkdocsPublish") {
        dependsOn(
            mkdocsBuild,
            gitCheckoutPublicationBranch,
            syncMkdocsToPublishedDirectory,
            gitCommitPublication,
            gitPush,
            gitCheckoutMainBranch
        )
    }
}
