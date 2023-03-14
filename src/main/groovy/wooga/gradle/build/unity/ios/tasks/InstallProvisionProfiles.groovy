package wooga.gradle.build.unity.ios.tasks

import com.wooga.gradle.BaseSpec
import com.wooga.gradle.io.LogFileSpec
import com.wooga.gradle.io.ProcessOutputSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import wooga.gradle.build.unity.ios.MobileProvisioning
import wooga.gradle.build.unity.ios.internal.DefaultMobileProvisioning
import java.io.PrintWriter
import java.text.SimpleDateFormat

class InstallProvisionProfiles extends DefaultTask implements BaseSpec, LogFileSpec, ProcessOutputSpec {

    private final ConfigurableFileCollection provisioningProfiles = objects.fileCollection()

    @InputFiles
    ConfigurableFileCollection getProvisioningProfiles() {
        provisioningProfiles
    }

    private final DirectoryProperty outputDirectory = objects.directoryProperty()

    @Internal
    DirectoryProperty getOutputDirectory() {
        outputDirectory
    }

    void setOutputDirectory(Provider<Directory> value) {
        outputDirectory.set(value)
    }

    void setOutputDirectory(File value) {
        outputDirectory.set(value)
    }

    @Internal
    Provider<Set<MobileProvisioning>> getProfiles() {
        provisioningProfiles.getElements().map {
            it.collect {
                DefaultMobileProvisioning.open(it.asFile)
            }
        } as Provider<Set<MobileProvisioning>>
    }

    @OutputFiles
    protected FileCollection getOutputFiles() {
        def files = objects.fileCollection()
        files.setFrom(provisioningProfiles.collect {
            outputDirectory.file("${readUUIDFromMobileProvision(it)}.mobileprovision")
        })
        files
    }

    static String readUUIDFromMobileProvision(File input) {
        DefaultMobileProvisioning.open(input).uuid
    }

    @TaskAction
    protected void install() {
        def logFile = logFile.asFile.getOrNull()
        if(logFile) {
            logFile.parentFile.mkdirs()
        }
        def output = new PrintWriter(getOutputStream(logFile), true)
        output.println("Install Profiles: ${provisioningProfiles.size()}")
        provisioningProfiles.each {
            def profile = DefaultMobileProvisioning.open(it)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            output.println("""\
                Install mobile provisioning profile:
                ----------------------------------------------------------------
                Name: ${profile.name}
                Uuid: ${profile.uuid}
                Team: ${profile.teamName}(${profile.teamIdentifier})
                Creation date: ${outputFormat.format(profile.creationDate)}
                Expiration date: ${outputFormat.format(profile.expirationDate)}
                Is xcode managed: ${profile.isXcodeManaged()}
                Entitlements: ${profile.entitlements}
                ----------------------------------------------------------------
                """.stripIndent().trim())

            if (profile.expired) {
                logger.warn("profile ${profile.name}(${profile.uuid}) is expired")
                output.println("profile ${profile.name}(${profile.uuid}) is expired")
            }

            def out = outputDirectory.file("${readUUIDFromMobileProvision(it)}.mobileprovision").get().asFile
            output.println("to: '${out.absolutePath}'\n")
            out.bytes = it.bytes
        }
        output.close()
    }
}
