package wooga.gradle.build.unity.ios.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.Task
import wooga.gradle.build.unity.ios.IOSBuildIntegrationSpec

import java.lang.reflect.ParameterizedType

abstract class CocoaPodSpec<T extends Task> extends IOSBuildIntegrationSpec {
    File podMock
    File podMockPath
    File xcodeProject
    File xcodeWorkspace
    String projectBaseName

    Class<T> getSubjectUnderTestClass() {
        if (!_sutClass) {
            try {
                this._sutClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
            }
            catch (Exception e) {
                this._sutClass = (Class<T>) DefaultTask
            }
        }
        _sutClass
    }
    private Class<T> _sutClass

    String getSubjectUnderTestName() {
        "${subjectUnderTestClass.simpleName.uncapitalize()}Test"
    }

    String getSubjectUnderTestTypeName() {
        subjectUnderTestClass.getTypeName()
    }

    def setup() {
        buildFile << """
        task $subjectUnderTestName(type: ${subjectUnderTestTypeName})
        """.stripIndent()
        setupPodMock()
    }

    def setupPodMock() {
        podMockPath = File.createTempDir("pod", "mock")

        buildFile << """
        $subjectUnderTestName {
            executableDirectory.set(${wrapValueBasedOnType(podMockPath, "File")})
        } 
        """.stripIndent()

        podMock = createFile("pod", podMockPath)
        podMock.executable = true
        podMock << """
            #!/usr/bin/env bash
            echo \$@
            env
            if [ "\$1" == "install" ]; then
                mkdir -p "\$3/${projectBaseName}.xcworkspace" || true
            fi
        """.stripIndent()
    }
}
