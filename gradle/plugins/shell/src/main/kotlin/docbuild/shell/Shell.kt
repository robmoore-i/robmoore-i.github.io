package docbuild.shell

import java.io.FileOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

@DisableCachingByDefault
abstract class Shell @Inject constructor(private val exec: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val cmd: ListProperty<String>

    @get:[OutputFile Optional]
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        if (outputFile.isPresent) {
            outputFile.get().asFile.delete()
        }
        exec.exec {
            commandLine(cmd.get())
            if (outputFile.isPresent) {
                outputFile.get().asFile.delete()
                standardOutput = FileOutputStream(outputFile.get().asFile, false)
            }
        }
    }
}