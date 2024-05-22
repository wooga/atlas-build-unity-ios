package wooga.gradle.cocoapods

import com.wooga.gradle.test.IntegrationSpec
import com.wooga.gradle.test.PropertyLocation
import com.wooga.gradle.test.executable.FakeExecutables
import com.wooga.gradle.test.run.result.GradleRunResult
import com.wooga.gradle.test.writers.PropertyGetterTaskWriter
import com.wooga.gradle.test.writers.PropertySetterWriter
import spock.lang.Unroll
import wooga.gradle.wooga.cocoapods.CocoapodsPlugin
import wooga.gradle.wooga.cocoapods.internal.RepoArt

import static com.wooga.gradle.test.writers.PropertySetInvocation.*

class CocoapodsPluginIntegrationSpec extends IntegrationSpec {


    def setup() {
        buildFile << "${applyPlugin(CocoapodsPlugin)}\n"
    }

    def "adds cocoapods artifactory repositories to set home dir"() {
        given:
        def cocoapodsHome = new File(projectDir, podsHomeDir)
        buildFile << """
            woogaCocoapods {
                homeDir = ${wrapValue(cocoapodsHome, File)}
                artRepositories = ${wrapValue(expectedRepositories, List)}
            }
        """
        when:
        runTasksSuccessfully("podAddArtRepositories")
        then:
        def podExecutable = new File(projectDir, podExecPath).absolutePath
        def rawRepos = processOutput(["CP_HOME_DIR": cocoapodsHome.absolutePath],
                podExecutable, "repo-art", "list", "--no-ansi")
        def repos = RepoArt.fromListLog(rawRepos).collect { it.url }
        expectedRepositories.forEach { expectedRepo ->
            assert repos.contains(expectedRepo), "${expectedRepo} not found in 'repo-art list', found ${repos}"
        }
        cleanup:
        new File(projectDir, podsHomeDir).deleteDir()
        where:
        podsHomeDir = "podshome"
        podExecPath = "bin/pod"
        expectedRepositories = [
                "https://artifactory.playticorp.com/artifactory/api/pods/Octopus-cocoapods-STAGING",
                "https://artifactory.playticorp.com/artifactory/api/pods/Octopus-cocoapods",
        ]
    }


    def "uses cocoapods executable without installing anything from asdf"() {
        given:
        def fakePod = FakeExecutables.argsReflector("pod", 0)
        buildFile << "tasks.register('$taskName', wooga.gradle.wooga.cocoapods.tasks.PodTask);\n"
        buildFile << "woogaCocoapods.executable = ${wrapValueBasedOnType(fakePod.executable, File)}"

        when:
        def result = runTasksSuccessfully(taskName)

        then: "no asdf pod should be installed in bin directory"
        def run = new GradleRunResult(result)
        !run.tasks.containsKey(":cocoapodsBundleBinstubs")
        run.orderedTasks.each {
            assert !it.startsWith("asdf")
            assert !it.startsWith("ruby")
            assert !it.startsWith("bundle")
        }

        where:
        taskName = "samplePodTask"
        defaultRepoArts = []
        and: "'pod' executable should support linkage plugin"
    }

    def "installs asdf cocoapods with repo-art+linkage if no executable is set"() {
        given:
        buildFile << "tasks.register('$taskName', wooga.gradle.wooga.cocoapods.tasks.PodTask);\n"

        when:
        def result = new GradleRunResult(runTasksSuccessfully(taskName))

        then: "expected tasks ran"
        result.orderedTasks.contains(":cocoapodsBundleBinstubs")

        and: "pod should be installed in bin directory"
        def binary = new File(projectDir, "bin/pod")
        assert binary.exists()
        assert binary.canExecute()

        and: "'pod' executable should have 'repo-art' subcommand"
        assertProcess(binary.absolutePath, "repo-art")

        and: "pod executable should support Podfiles with linkage"
        new File(projectDir, "Podfile").with {
            text = "source 'https://github.com/CocoaPods/Specs.git'\nplugin 'cocoapods-pod-linkage'\n"
        }
        !processOutput(binary.absolutePath, "install", "--project-directory=${projectDir.absolutePath}")
                .contains("Your Podfile requires that the plugin `cocoapods-pod-linkage` be installed")

        where:
        taskName = "samplePodTask"
        defaultRepoArts = []
        and: "'pod' executable should support linkage plugin"
    }

    @Unroll("can set property woogaCocoapods.#property with #invocation and type #type with build.gradle")
    def "can set property on docker extension with build.gradle"() {
        when:
        set.location = invocation == none ? PropertyLocation.none : set.location

        def buildDir = new File(projectDir, "build")
        def propertyQuery = runPropertyQuery(get, set)
                .withSerializer("Directory", fileLikeSerializer(projectDir))
                .withSerializer("RegularFile", fileLikeSerializer(projectDir))
                .withSerializer("File", fileLikeSerializer(projectDir))
                .withSerializer("Provider<Directory>", fileLikeSerializer(buildDir))
                .withSerializer("Provider<RegularFile>", fileLikeSerializer(buildDir))

        then:
        propertyQuery.matches(rawValue)

        where:
        property           | invocation  | rawValue                      | type
        "homeDirectory"    | providerSet | "dir"                         | "Provider<Directory>"
        "homeDirectory"    | assignment  | "dir"                         | "Provider<Directory>"
        "homeDirectory"    | assignment  | "dir"                         | "Directory"
        "homeDirectory"    | assignment  | "dir"                         | "File"
        "homeDirectory"    | setter      | "dir"                         | "Provider<Directory>"
        "homeDirectory"    | setter      | "dir"                         | "Directory"
        "homeDirectory"    | setter      | "dir"                         | "File"
        "homeDirectory"    | none        | null                          | "File"

        "projectDirectory" | providerSet | "dir"                         | "Provider<Directory>"
        "projectDirectory" | assignment  | "dir"                         | "Provider<Directory>"
        "projectDirectory" | assignment  | "dir"                         | "Directory"
        "projectDirectory" | assignment  | "dir"                         | "File"
        "projectDirectory" | setter      | "dir"                         | "Provider<Directory>"
        "projectDirectory" | setter      | "dir"                         | "Directory"
        "projectDirectory" | setter      | "dir"                         | "File"
        "projectDirectory" | none        | "."                           | "Directory"

        "executable"       | providerSet | "dfile"                       | "Provider<RegularFile>"
        "executable"       | assignment  | "dfile"                       | "Provider<RegularFile>"
        "executable"       | assignment  | "dfile"                       | "RegularFile"
        "executable"       | assignment  | "dfile"                       | "File"
        "executable"       | setter      | "dfile"                       | "Provider<RegularFile>"
        "executable"       | setter      | "dfile"                       | "RegularFile"
        "executable"       | setter      | "dfile"                       | "File"
        // if not set, will be generated by asdf, throws if queried before tasks.
//        "executable"       | none        | null            | "File"

        "version"          | providerSet | "name"                        | "Provider<String>"
        "version"          | assignment  | "name"                        | "Provider<String>"
        "version"          | assignment  | "name"                        | "String"
        "version"          | setter      | "name"                        | "Provider<String>"
        "version"          | setter      | "name"                        | "String"
        "version"          | none        | "1.14.3"                      | "String"

        "gemExtensions"    | providerSet | ["a": ["b"]]                  | "Provider<Map<String, List<String>>>"
        "gemExtensions"    | assignment  | ["a": ["b"]]                  | "Provider<Map<String, List<String>>>"
        "gemExtensions"    | assignment  | ["a": ["b"]]                  | "Map<String, List<String>>"
        "gemExtensions"    | setter      | ["a": ["b"]]                  | "Provider<Map<String, List<String>>>"
        "gemExtensions"    | setter      | ["a": ["b"]]                  | "Map<String, List<String>>"
        "gemExtensions"    | none        | ["cocoapods-art"        : [],
                                            "cocoapods-pod-linkage": []] | "Map<String, List<String>>"

        "artRepositories"  | providerSet | ["a", "b"]                    | "Provider<List<String>>"
        "artRepositories"  | assignment  | ["a", "b"]                    | "Provider<List<String>>"
        "artRepositories"  | assignment  | ["a", "b"]                    | "List<String>"
        "artRepositories"  | setter      | ["a", "b"]                    | "Provider<List<String>>"
        "artRepositories"  | setter      | ["a", "b"]                    | "List<String>"
        "artRepositories"  | none        | []                            | "List<String>"

        set = new PropertySetterWriter("woogaCocoapods", property)
                .set(rawValue, type)
                .toScript(invocation)
        get = new PropertyGetterTaskWriter(set)
    }

    String processOutput(Map<String, String> env = [:], File workingDir = projectDir, String... command) {
        def builder = new ProcessBuilder(command).directory(workingDir)
        builder.environment().putAll(env)

        def process = builder.start()
        return process.text
    }

    //workingDir == projectDir is necessary for asdf to locate the correct .tool-versions file
    void assertProcess(File workingDir = projectDir, String... command) {
        def process = new ProcessBuilder(command).directory(workingDir).start()
        def output = process.text
        def exitValue = process.exitValue()

        assert exitValue == 0, "Failed executing process ${command}:\n ${output}"
    }

    static Closure<String> fileLikeSerializer(File baseDir) {
        return { String dir ->
            if (dir == ".") return baseDir.absolutePath
            dir ? new File(baseDir, dir).absolutePath : null
        }
    }

}
