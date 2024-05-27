package wooga.gradle.cocoapods.tasks

import com.wooga.gradle.test.IntegrationSpec
import spock.lang.Requires
import wooga.gradle.cocoapods.traits.CocoapodsSnippets
import wooga.gradle.wooga.cocoapods.CocoapodsPlugin
import wooga.gradle.wooga.cocoapods.tasks.PodInstall

import static com.wooga.gradle.test.PropertyUtils.wrapValue

/**
 * The test examples in this class are not 100% integration/functional tests.
 *
 * We can't run the real pod install as this would bring to much overhead at the moment.
 * We only test the invocation of pod and its parameters.
 */
@Requires({ os.macOs })
class PodInstallSpec extends IntegrationSpec implements CocoapodsSnippets {


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

    File getXCodeProjDir() {  new File(projectDir, "xCodeProject") }
}
