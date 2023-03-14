package wooga.gradle.build.unity.ios.tasks

import com.wooga.gradle.test.TaskIntegrationSpec
import org.gradle.api.Task
import wooga.gradle.build.unity.ios.IOSBuildIntegrationSpec

abstract class IOSBuildTaskIntegrationSpec<T extends Task> extends IOSBuildIntegrationSpec implements TaskIntegrationSpec<T> {
    def setup() {
        buildFile << """
        task ${subjectUnderTestName}(type: ${subjectUnderTestTypeName})
        """.stripIndent()
    }
}
