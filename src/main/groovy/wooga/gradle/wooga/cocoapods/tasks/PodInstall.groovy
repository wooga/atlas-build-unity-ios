package wooga.gradle.wooga.cocoapods.tasks

import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import wooga.gradle.wooga.cocoapods.PodProjectSpec

class PodInstall extends PodTask implements PodProjectSpec {

    @InputFiles
    @SkipWhenEmpty
    protected FileCollection getInputFiles() {
        def podFileProvider = projectDirectory.file("Podfile")
        def inputFiles = podFileProvider.map { RegularFile podFile ->
            def files = [podFile]
            if(podFile.asFile.exists()) {
                def lockFile = projectDirectory.file("Podfile.lock")
                files.add(lockFile.get())
            }
            return files.findAll{ it.asFile.exists() }
        }
        project.files(inputFiles)
    }

    @OutputDirectory
    protected Provider<Directory> getPodsDir() {
        projectDirectory.dir("Pods")
    }

    PodInstall() { }

    @TaskAction
    protected void install() {
        super.podProcessExecutor(['repo', 'update'])
            .execute()
            .assertNormalExitValue()
        super.podProcessExecutor(['install', "--project-directory=${projectDirectory.get().asFile.absolutePath}"])
            .execute()
            .assertNormalExitValue()
    }
}
