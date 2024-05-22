package wooga.gradle.cocoapods.traits

import static com.wooga.gradle.test.PropertyUtils.wrapValue

trait CocoapodsSnippets {


    static String setupCocoapodsXCodeProject(File projectDir, String projectBaseName = "test") {
        projectDir.mkdirs()
        new File(projectDir, "${projectBaseName}.xcodeproj").createNewFile()
        new File(projectDir, "${projectBaseName}.xcworkspace").createNewFile()
        return """
        woogaCocoapods {
            projectDirectory = ${wrapValue(projectDir, File)}
        }
        """.stripIndent()
    }

    static String setupPodMock(File podMockDir = File.createTempDir("pod", "mock"), String projectBaseName = "test") {
        def podMock = new File(podMockDir, "pod")
        podMock.createNewFile()
        podMock.executable = true
        podMock << """
            #!/usr/bin/env bash
            echo \$@
            
        """.stripIndent()
        return """
        woogaCocoapods {
            executable = ${wrapValue(podMock, File)} 
        }
        """.stripIndent()
    }
}
