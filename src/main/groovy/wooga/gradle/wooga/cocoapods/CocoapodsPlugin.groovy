package wooga.gradle.wooga.cocoapods

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.provider.Provider
import wooga.gradle.asdf.AsdfPlugin
import wooga.gradle.asdf.AsdfPluginExtension
import wooga.gradle.asdf.internal.ToolVersionInfo
import wooga.gradle.asdf.ruby.GemfileSpec
import wooga.gradle.asdf.ruby.RubyPlugin
import wooga.gradle.asdf.ruby.RubyPluginExtension
import wooga.gradle.build.unity.ios.IOSBuildPluginExtension
import wooga.gradle.wooga.cocoapods.tasks.AddArtRepositories
import wooga.gradle.wooga.cocoapods.tasks.PodInstall
import wooga.gradle.wooga.cocoapods.tasks.PodTask
import wooga.gradle.wooga.cocoapods.tasks.UpdateArtRepositories

class CocoapodsPlugin implements Plugin<Project> {

    protected Project project

    @Override
    void apply(Project project) {
        this.project = project
        def cocoapods = createWoogaCocoapodsExtension("woogaCocoapods")

        project.tasks.withType(PodTask).configureEach {
            homeDirectory.convention(cocoapods.homeDirectory.map {
                it.asFile.mkdir()
                return it
            })
            executableName.convention(cocoapods.executable.map { it.asFile.absolutePath })
        }

        project.tasks.withType(PodInstall).configureEach {
            projectDirectory.convention(cocoapods.projectDirectory)
        }

        def addArtRepos = project.tasks.register("podAddArtRepositories", AddArtRepositories) {
            artRepositories.convention(cocoapods.artRepositories)
        }

        def updateArtRepos = project.tasks.register("podUpdateArtRepositories", UpdateArtRepositories) {
            mustRunAfter(addArtRepos)
            artRepositories.convention(cocoapods.artRepositories)
        }

        def podInstall = project.tasks.register("podInstall", PodInstall) {
            dependsOn(addArtRepos, updateArtRepos)
        }

        //TODO: turn build-unity-ios into unified-build-system-ios
        //TODO: this plugin should be subordinate to build-unity-ios. Reverse interface changes there and make it configure this guy. 
        //Overrides for when net.wooga.build-unity-ios is applied
        project.pluginManager.withPlugin("net.wooga.build-unity-ios") { AppliedPlugin _ ->
            def buildIos = project.extensions.findByType(IOSBuildPluginExtension)
            cocoapods.projectDirectory.convention(buildIos.xcodeProjectDirectory)
            project.tasks.named("xcodeArchive") {
                dependsOn podInstall
            }
        }
    }

    def createWoogaCocoapodsExtension(String extensionName) {
        def cocoapods = project.extensions.create(extensionName, CocoapodsPluginExtension)
        cocoapods.projectDirectory.convention(project.layout.projectDirectory)
        cocoapods.executable.convention(defaultCocoapodsInstallation(cocoapods))
        cocoapods.version.convention(CocoapodsPluginConventions.version.getStringValueProvider(project))
        cocoapods.homeDirectory.convention(CocoapodsPluginConventions.homeDir.getDirectoryValueProvider(project))
        cocoapods.addGemExtension("cocoapods-art", [])
        cocoapods.addGemExtension("cocoapods-pod-linkage", [])

        return cocoapods
    }

    Provider<RegularFile> defaultCocoapodsInstallation(CocoapodsInstallSpec installSpec) {
        def maybeExecutable = CocoapodsPluginConventions.executable.getFileValueProvider(project)
        //present makes sense here because Conventions are static inputs (env vars/properties/constants).
        if(maybeExecutable.present) {
            return maybeExecutable
        } else {
            //We want to apply asdf/ruby plugin only if we actually need them
            return installAsdfRubyCocoapods(installSpec)
        }
    }

    Provider<RegularFile> installAsdfRubyCocoapods(CocoapodsInstallSpec cocoapods) {
        project.pluginManager.apply(AsdfPlugin.class)
        project.pluginManager.apply(RubyPlugin.class)
        def asdf = project.extensions.getByType(AsdfPluginExtension)
        def ruby = project.extensions.getByType(RubyPluginExtension)
        ruby.tool(new ToolVersionInfo("cocoapods", cocoapods.version)) { GemfileSpec it ->
            it.gems.putAll(cocoapods.gemExtensions.map {it.collectEntries { pair ->
                [pair.key, pair.value.toArray() as String[]]
            }})
        }
        def cocoapodsTool = asdf.getTool("cocoapods")
        return cocoapodsTool.getExecutable("pod").map {
            project.layout.projectDirectory.file(it)
        }
    }
}
