package wooga.gradle.cocoapods.tasks

import com.wooga.gradle.test.IntegrationSpec
import com.wooga.gradle.test.PropertyLocation
import com.wooga.gradle.test.writers.PropertyGetterTaskWriter
import com.wooga.gradle.test.writers.PropertySetterWriter
import spock.lang.Requires
import spock.lang.Unroll
import wooga.gradle.cocoapods.traits.CocoapodsSnippets
import wooga.gradle.wooga.cocoapods.CocoapodsPlugin
import wooga.gradle.wooga.cocoapods.tasks.PodInstall

import static com.wooga.gradle.test.PropertyUtils.wrapValue
import static com.wooga.gradle.test.writers.PropertySetInvocation.getAssignment
import static com.wooga.gradle.test.writers.PropertySetInvocation.getNone
import static com.wooga.gradle.test.writers.PropertySetInvocation.getProviderSet
import static com.wooga.gradle.test.writers.PropertySetInvocation.getSetter

/**
 * The test examples in this class are not 100% integration/functional tests.
 *
 * We can't run the real pod install as this would bring to much overhead at the moment.
 * We only test the invocation of pod and its parameters.
 */
@Requires({ os.macOs })
class PodInstallTaskSpec extends IntegrationSpec implements CocoapodsSnippets {


    static String subjectUnderTestName = "testInstallTask"
    def setup() {
        buildFile << applyPlugin(CocoapodsPlugin) + "\n"
        buildFile << setupCocoapodsXCodeProject(getXCodeProjDir())
        buildFile << setupPodMock()
        buildFile << "tasks.register(${wrapValue(subjectUnderTestName, String)}, ${PodInstall.name})"
    }

    def "task skips when no Pod file exist in project"() {
        given: "no pod file"
        def podfile = new File(getXCodeProjDir(), "Podfile")
        podfile.delete()
        assert !podfile.exists(), "Podfile shouldn't be present for this test"

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        outputContains(result, "Task :${subjectUnderTestName} NO-SOURCE")
    }

    def "task executes 'repo update' before 'install'"() {
        given: "a sample Pod file"
        createFile("Podfile", getXCodeProjDir())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        outputContains(result, "pod repo update")
        outputContains(result, "pod install")

        where:
        taskName = "podInstall"
    }

    def "buildKeychain caches task outputs"() {
        given: "a sample Pod file"
        def podFile = createFile("Podfile", getXCodeProjDir())

        and: "a Podfile.lock"
        def lockFile = createFile("Podfile.lock", getXCodeProjDir())

        and: "a gradle run with buildKeychain"
        runTasksSuccessfully(taskName)

        when:
        def result = runTasksSuccessfully(taskName)
        then:
        result.wasUpToDate(taskName)

        when:
        podFile << "a change"
        result = runTasksSuccessfully(taskName)

        then:
        !result.wasUpToDate(taskName)
        outputContains(result, "Input property 'inputFiles' file ${podFile.path} has changed.")

        when:
        lockFile << "a change"
        result = runTasksSuccessfully(taskName)

        then:
        !result.wasUpToDate(taskName)
        outputContains(result, "Input property 'inputFiles' file ${lockFile.path} has changed.")

        where:
        taskName = subjectUnderTestName
    }

//    @Unroll("can set property #property with #method and type #type")
//    def "can set property"() {
//        given: "a task to read back the value"
//        def query = new PropertyQueryTaskWriter("${subjectUnderTestName}.${property}")
//        query.write(buildFile)
//
//        and: "a set property"
//        appendToSubjectTask("${method}($value)")
//
//        when:
//        def result = runTasksSuccessfully(query.taskName)
//
//        then:
//        query.matches(result, expectedValue)
//
//        where:
//        property                 | method                  | rawValue            | returnValue | type
//        "projectDirectory"       | toProviderSet(property) | "/path/to/project"  | _           | "File"
//        "projectDirectory"       | toProviderSet(property) | "/path/to/project"  | _           | "Provider<Directory>"
//        "projectDirectory"       | toSetter(property)      | "/path/to/project"  | _           | "File"
//        "projectDirectory"       | toSetter(property)      | "/path/to/project"  | _           | "Provider<Directory>"
//
//        "xcodeProjectFileName"   | toProviderSet(property) | "test.xcodeproj"    | _           | "String"
//        "xcodeProjectFileName"   | toProviderSet(property) | "test.xcodeproj"    | _           | "Provider<String>"
//        "xcodeProjectFileName"   | toSetter(property)      | "test.xcodeproj"    | _           | "String"
//        "xcodeProjectFileName"   | toSetter(property)      | "test.xcodeproj"    | _           | "Provider<String>"
//
//        "xcodeWorkspaceFileName" | toProviderSet(property) | "test.xcworkspace"  | _           | "String"
//        "xcodeWorkspaceFileName" | toProviderSet(property) | "test.xcworkspace"  | _           | "Provider<String>"
//        "xcodeWorkspaceFileName" | toSetter(property)      | "test.xcworkspace"  | _           | "String"
//        "xcodeWorkspaceFileName" | toSetter(property)      | "test.xcworkspace"  | _           | "Provider<String>"
//
//        "artRepositories"        | toProviderSet(property) | ["https://foo.bar"] | _           | "List<String>"
//        "artRepositories"        | toProviderSet(property) | ["https://foo.bar"] | _           | "Provider<List<String>>"
//        "artRepositories"        | toSetter(property)      | ["https://foo.bar"] | _           | "List<String>"
//        "artRepositories"        | toSetter(property)      | ["https://foo.bar"] | _           | "Provider<List<String>>"
//        value = wrapValueBasedOnType(rawValue, type, IOSBuildIntegrationSpec.wrapValueFallback)
//        expectedValue = returnValue == _ ? rawValue : returnValue
//    }

//    def "adds repo-art repositories when configured"() {
//        given: "a sample Pod file"
//        def podFile = createFile("Podfile")
//
//        and: "a list of artifactory repositories configured"
//
//        """
//        woogaCocoapods {
//            artRepositories = ${wrapValue(repositories.values().toList(), "List<String>")}
//        }
//        """.stripIndent()
//
//        when:
//        def result = runTasks(taskName)
//
//        then:
//        result.success
//        repositories.each {repoName, repoUrl ->
//            assert outputContains(result, "repo-art add ${repoName} ${repoUrl} --silent")
//            assert outputContains(result, "repo-art update ${repoName}")
//        }
//
//        where:
//        repositories = [
//                "repo1-443-foo-bar": "https://repo1/foo/bar",
//                "repo2-443-"       : "https://repo2/",
//                "repo3"            : "https://repo3"
//        ]
//        taskName = subjectUnderTestName
//    }

    File getXCodeProjDir() {  new File(projectDir, "xCodeProject") }
}
