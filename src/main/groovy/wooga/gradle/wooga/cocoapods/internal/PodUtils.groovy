package wooga.gradle.wooga.cocoapods.internal

import com.wooga.gradle.io.ExecSpec
import com.wooga.gradle.io.ProcessExecutor
import wooga.gradle.wooga.cocoapods.tasks.PodTask

class PodUtils {

    static ProcessExecutor podProcessExecutor(List<String> args, PodTask task) {
        def finalArgs = args + task.arguments.get()
        def executor = ProcessExecutor.from(task as ExecSpec)
                .withArguments(finalArgs)

        if(task.homeDirectory.present) {
            executor.withEnvironment(["CP_HOME_DIR": task.homeDirectory.get().asFile.canonicalPath])
        }
        return executor
    }

    static Map<String, RepoArt> listRepoArts(PodTask task) {
        def capturedStdout = new ByteArrayOutputStream()
        podProcessExecutor(["repo-art", "list", "--no-ansi"], task)
                .withStandardOutput(capturedStdout)
                .execute().assertNormalExitValue()

        def stdout= capturedStdout.toString()
        def repositories = RepoArt.fromListLog(stdout)
        return repositories.collectEntries { it -> [it.name, it] }
    }

    static List<String> listRepoArtsURLs(PodTask task) {
        listRepoArts(task).collect { pair -> pair.value.url }
    }


}
