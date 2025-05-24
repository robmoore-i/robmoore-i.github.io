package docbuild.shell

import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations

@Suppress("LeakingThis")
@UntrackedTask(because = "never cache")
abstract class GitCheckoutBranch @Inject constructor(exec: ExecOperations) : UntrackedShell(exec) {

    @get:Input
    abstract val branch: Property<String>

    @get:Input
    abstract val gitStatus: Property<String>

    init {
        cmd.set(listOf("git", "checkout"))
        cmd.add(branch)
    }

    override fun before() {
        if (gitStatus.isPresent && gitStatus.get().isNotBlank()) {
            throw RuntimeException(
                "Refusing to switch branch due to outstanding changes:\n${gitStatus.get()}---"
            )
        }
    }
}
