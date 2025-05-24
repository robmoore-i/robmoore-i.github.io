package docbuild.shell

import javax.inject.Inject
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations

@UntrackedTask(because = "never cache")
abstract class UntrackedShell @Inject constructor(exec: ExecOperations) : Shell(exec)
