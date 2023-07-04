package wooga.gradle.build.unity.ios.tasks

import com.wooga.gradle.PlatformUtils
import com.wooga.gradle.test.PropertyQueryTaskWriter
import com.wooga.gradle.test.ios.MobileProvisionMock
import spock.lang.Requires
import spock.lang.Unroll

import static com.wooga.gradle.test.PropertyUtils.toProviderSet
import static com.wooga.gradle.test.PropertyUtils.toSetter

class InstallProvisioningProfilesIntegrationSpec extends IOSBuildTaskIntegrationSpec<InstallProvisionProfiles> {

    @Requires({ PlatformUtils.mac })
    @Unroll("can set property #property with #method and type #type")
    def "can set property"() {
        given: "a task to read back the value"
        def query = new PropertyQueryTaskWriter("${subjectUnderTestName}.${property}")
        query.write(buildFile)

        and: "a set property"
        appendToSubjectTask("${method}($value)")

        when:
        def result = runTasksSuccessfully(query.taskName)

        then:
        query.matches(result, expectedValue)

        where:
        property          | method                  | rawValue             | returnValue | type
        "outputDirectory" | property                | "/path/to/outputDir" | _           | "File"
        "outputDirectory" | property                | "/path/to/outputDir" | _           | "Provider<Directory>"
        "outputDirectory" | toProviderSet(property) | "/path/to/outputDir" | _           | "File"
        "outputDirectory" | toProviderSet(property) | "/path/to/outputDir" | _           | "Provider<Directory>"
        "outputDirectory" | toSetter(property)      | "/path/to/outputDir" | _           | "File"
        "outputDirectory" | toSetter(property)      | "/path/to/outputDir" | _           | "Provider<Directory>"

        "logFile"         | property                | "/path/to/log"       | _           | "File"
        "logFile"         | property                | "/path/to/log"       | _           | "Provider<RegularFile>"
        "logFile"         | toProviderSet(property) | "/path/to/log"       | _           | "File"
        "logFile"         | toProviderSet(property) | "/path/to/log"       | _           | "Provider<RegularFile>"
        "logFile"         | toSetter(property)      | "/path/to/log"       | _           | "File"
        "logFile"         | toSetter(property)      | "/path/to/log"       | _           | "Provider<RegularFile>"

        "logToStdout"     | toProviderSet(property) | true                 | _           | "Boolean"
        "logToStdout"     | toProviderSet(property) | true                 | _           | "Provider<Boolean>"
        "logToStdout"     | toSetter(property)      | true                 | _           | "Boolean"
        "logToStdout"     | toSetter(property)      | true                 | _           | "Provider<Boolean>"
        value = wrapValueBasedOnType(rawValue, type, wrapValueFallback)
        expectedValue = returnValue == _ ? rawValue : returnValue
    }

    def "installs provided provisioning profiles to the output directory"() {
        given: "a mock mobile provisioning file with a known uuid"
        def files = uuids.collect { UUID id ->
            def mock = MobileProvisionMock.createMock({
                it.uuid = id
            })
            def installedProfile = new File(projectDir, "${installDir}/${id}.mobileprovision")
            new Tuple2<File, File>(mock, installedProfile)
        }

        and: "a future provisioning profile location"
        assert !files.any { it.second.exists() }

        and:
        appendToSubjectTask("""
            provisioningProfiles.from(${wrapValueBasedOnType(files.collect { it.first }, "List<File>")})
            outputDirectory = ${wrapValueBasedOnType(new File(projectDir, installDir), "File")}
        """.stripIndent())

        when:
        runTasksSuccessfully(subjectUnderTestName)

        then:
        files.every { it.second.exists() }
        files.every {
            def mockBytes = it.first.bytes
            def profileBytes = it.second.bytes
            mockBytes == profileBytes
        }

        where:
        installDir = "build/custom profiles/location"
        uuids = [UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()]
    }

    def "writes log to logfile and stdout when configured"() {
        given: "a mock mobile provisioning file with a known uuid"
        def files = uuids.collect { UUID id ->
            def mock = MobileProvisionMock.createMock({
                it.uuid = id
            })
            def installedProfile = new File(projectDir, "build/profiles/${id}.mobileprovision")
            new Tuple2<File, File>(mock, installedProfile)
        }

        and: "a future logfile"
        def logFile = new File(projectDir, logFilePath)
        assert !logFile.exists()

        and:
        appendToSubjectTask("""
            provisioningProfiles.from(${wrapValueBasedOnType(files.collect { it.first }, "List<File>")})
            outputDirectory = ${wrapValueBasedOnType(new File(projectDir, "build/profiles"), "File")}
            logFile = ${wrapValueBasedOnType(logFile, "File")}
            logToStdout = ${wrapValueBasedOnType(logToStdout, "Boolean")} 
        """.stripIndent())

        when:
        def result = runTasksSuccessfully(subjectUnderTestName)

        then:
        logFile.exists()
        logFile.text.contains("Install Profiles: ${uuids.size()}")
        result.standardOutput.contains(logFile.text) == logToStdout

        where:
        uuids = [UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()]
        logFilePath = osPath("build/logs/custom.log")
        logToStdout << [true, false]
    }
}
