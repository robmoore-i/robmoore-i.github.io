import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.Shell

tasks.register<Shell>("mkdocsServe") {
    cmd.set(listOf("mkdocs", "serve"))
}

tasks.register<MkdocsBuild>("mkdocsBuild")
