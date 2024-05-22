package wooga.gradle.wooga.cocoapods.tasks

import com.wooga.gradle.ArgumentsSpec
import com.wooga.gradle.io.ExecSpec
import com.wooga.gradle.io.LogFileSpec
import com.wooga.gradle.io.ProcessExecutor
import com.wooga.gradle.io.ProcessOutputSpec
import org.gradle.api.DefaultTask
import wooga.gradle.wooga.cocoapods.PodSpec
import wooga.gradle.wooga.cocoapods.internal.PodUtils

class PodTask extends DefaultTask implements ExecSpec, ArgumentsSpec, LogFileSpec, ProcessOutputSpec, PodSpec {

    PodTask() {}

    protected ProcessExecutor podProcessExecutor(List<String> args) {
        return PodUtils.podProcessExecutor(args, this)
                .withOutputLogFile(this, this)
    }

    String createArtRepoName(String artRepoURL) {
        return artRepoURL.replaceAll("https?://","").replaceFirst("/","-443-").replaceAll("[/]","-")
    }
}
