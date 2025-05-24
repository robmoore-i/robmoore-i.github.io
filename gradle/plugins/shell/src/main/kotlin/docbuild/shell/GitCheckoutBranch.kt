package docbuild.shell

import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault

@Suppress("LeakingThis")
@DisableCachingByDefault
abstract class GitCheckoutBranch @Inject constructor(exec: ExecOperations) : Shell(exec) {

    @get:Input
    abstract val branch: Property<String>

    @get:Input
    abstract val gitStatus: Property<String>

    init {
        cmd.set(listOf("git", "checkout"))
        cmd.add(branch)
    }

    @TaskAction
    fun gitCheckout() {
        if (gitStatus.isPresent && gitStatus.get().isNotBlank()) {
            throw RuntimeException(
                "Refusing to switch branch due to outstanding changes:\n${gitStatus.get()}---"
            )
        }
    }
}