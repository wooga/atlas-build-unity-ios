package wooga.gradle.wooga.cocoapods

import nebula.test.ProjectSpec
import org.gradle.api.Task
import wooga.gradle.build.unity.ios.IOSBuildPlugin
import wooga.gradle.build.unity.ios.IOSBuildPluginExtension
import wooga.gradle.wooga.cocoapods.tasks.PodTask

class CocoapodsPluginSpec extends ProjectSpec {

    def "#taskName depends on #dependencies"() {
        given:
        project.plugins.apply(CocoapodsPlugin)
        when:
        def task = project.tasks.getByName(taskName)
        then:
        def actualDependencies = task.taskDependencies.getDependencies(task)
        dependencies.forEach { dep ->
            assert actualDependencies.any { Task it -> it.name == dep }, "$dep not in dependency list of $task: $actualDependencies"
        }
        where:
        taskName     | dependencies
        "podInstall" | ["podAddArtRepositories", "podUpdateArtRepositories", "cocoapodsBundleBinstubs"]
    }

    def "PodTasks dont depend on #asdfBinstubsTask if cocoapods executable is set"() {
        given:
        project.plugins.apply(CocoapodsPlugin)
        project.extensions.getByType(CocoapodsPluginExtension).with {
            executable = File.createTempFile("pod", "fake")
        }
        when:
        def podTasks = project.tasks.withType(PodTask)
        then:
        podTasks.each { Task task ->
            def dependencies = task.taskDependencies.getDependencies(task)
            assert !dependencies.collect {it.name}.contains(asdfBinstubsTask), "$asdfBinstubsTask should not be a dependency of $task.name"
        }
        where:
        asdfBinstubsTask = "cocoapodsBundleBinstubs"
    }

    def "gets project directory from net.wooga.build-unity-ios if its applied"() {
        given:
        project.plugins.apply(CocoapodsPlugin)
        project.plugins.apply(IOSBuildPlugin)
        and:
        def xcodeProjectDir = new File(projectDir, "testXcodeDir")
        project.extensions.getByType(IOSBuildPluginExtension).with {
            it.xcodeProjectDirectory = xcodeProjectDir
        }

        when:
        def woogaCocoapods = project.extensions.getByType(CocoapodsPluginExtension)
        def projectDir = woogaCocoapods.projectDirectory

        then:
        projectDir.asFile.get() == xcodeProjectDir
    }

    def "xcodeArchive depends on podInstall when net.wooga.build-unity-ios is applied"() {
        given:
        project.plugins.apply(IOSBuildPlugin)
        project.plugins.apply(CocoapodsPlugin)

        when:
        def xCodeArchive = project.tasks.getByName("xcodeArchive")

        then:
        def dependencies = xCodeArchive.taskDependencies.getDependencies(xCodeArchive)
        dependencies.any { Task dependency -> dependency.name == "podInstall" }
    }

}
