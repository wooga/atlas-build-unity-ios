/*
 * Copyright 2018-22 Wooga GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package wooga.gradle.build.unity.ios

import com.wooga.gradle.BaseSpec
import com.wooga.gradle.io.ExecSpec
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.gradle.api.Action
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.util.ConfigureUtil
import wooga.gradle.xcodebuild.config.ExportOptions

import static org.gradle.util.ConfigureUtil.configureUsing

trait IOSBuildPluginExtension extends BaseSpec {

    @Deprecated
    abstract PasswordCredentials getFastlaneCredentials()

    @Deprecated
    abstract void setFastlaneCredentials(PasswordCredentials cred)

    @Deprecated
    abstract IOSBuildPluginExtension fastlaneCredentials(Closure configuration)

    @Deprecated
    abstract IOSBuildPluginExtension fastlaneCredentials(Action<PasswordCredentials> action)

    @Deprecated
    abstract IOSBuildPluginExtension fastlaneCredentials(PasswordCredentials cred)

    private final Property<String> keychainPassword = objects.property(String)

    Property<String> getKeychainPassword() {
        keychainPassword
    }

    void setKeychainPassword(Provider<String> value) {
        keychainPassword.set(value)
    }

    void setKeychainPassword(String value) {
        keychainPassword.set(value)
    }

    private final ListProperty<String> signingIdentities = objects.listProperty(String)

    @Input
    ListProperty<String> getSigningIdentities() {
        signingIdentities
    }

    void setSigningIdentities(Provider<List<String>> value) {
        signingIdentities.set(value)
    }

    void setSigningIdentities(List<String> value) {
        signingIdentities.set(value)
    }

    void setSigningIdentity(String value) {
        signingIdentities.empty().add(value)
    }

    void setSigningIdentity(Provider<String> value) {
        signingIdentities.empty().add(value)
    }

    void signingIdentity(String value) {
        signingIdentities.add(value)
    }

    void signingIdentity(Provider<String> value) {
        signingIdentities.add(value)
    }

    private final RegularFileProperty codeSigningIdentityFile = objects.fileProperty()

    Property<RegularFile> getCodeSigningIdentityFile() {
        codeSigningIdentityFile
    }

    void setCodeSigningIdentityFile(Provider<RegularFile> value) {
        codeSigningIdentityFile.set(value)
    }

    void setCodeSigningIdentityFile(File value) {
        codeSigningIdentityFile.set(value)
    }

    private final Property<String> codeSigningIdentityFilePassphrase = objects.property(String)

    Property<String> getCodeSigningIdentityFilePassphrase() {
        codeSigningIdentityFilePassphrase
    }

    void setCodeSigningIdentityFilePassphrase(Provider<String> value) {
        codeSigningIdentityFilePassphrase.set(value)
    }

    void setCodeSigningIdentityFilePassphrase(String value) {
        codeSigningIdentityFilePassphrase.set(value)
    }

    private final Property<String> appIdentifier = objects.property(String)

    Property<String> getAppIdentifier() {
        appIdentifier
    }

    void setAppIdentifier(Provider<String> value) {
        appIdentifier.set(value)
    }

    void setAppIdentifier(String value) {
        appIdentifier.set(value)
    }

    private final Property<String> provisioningProfileAppId = objects.property(String)

    Property<String> getProvisioningProfileAppId() {
        provisioningProfileAppId
    }

    void setProvisioningProfileAppId(Provider<String> value) {
        provisioningProfileAppId.set(value)
    }

    void setProvisioningProfileAppId(String value) {
        provisioningProfileAppId.set(value)
    }

    private final Property<String> teamId = objects.property(String)

    Property<String> getTeamId() {
        teamId
    }

    void setTeamId(Provider<String> value) {
        teamId.set(value)
    }

    void setTeamId(String value) {
        teamId.set(value)
    }

    private final Property<String> scheme = objects.property(String)

    Property<String> getScheme() {
        scheme
    }

    void setScheme(Provider<String> value) {
        scheme.set(value)
    }

    void setScheme(String value) {
        scheme.set(value)
    }

    private final Property<String> configuration = objects.property(String)

    Property<String> getConfiguration() {
        configuration
    }

    void setConfiguration(Provider<String> value) {
        configuration.set(value)
    }

    void setConfiguration(String value) {
        configuration.set(value)
    }

    private final Property<String> provisioningName = objects.property(String)

    Property<String> getProvisioningName() {
        provisioningName
    }

    void setProvisioningName(Provider<String> value) {
        provisioningName.set(value)
    }

    void setProvisioningName(String value) {
        provisioningName.set(value)
    }

    private final Property<Boolean> adhoc = objects.property(Boolean)

    Property<Boolean> getAdhoc() {
        adhoc
    }

    void setAdhoc(Provider<Boolean> value) {
        adhoc.set(value)
    }

    void setAdhoc(Boolean value) {
        adhoc.set(value)
    }

    private final Property<Boolean> publishToTestFlight = objects.property(Boolean)

    Property<Boolean> getPublishToTestFlight() {
        publishToTestFlight
    }

    void setPublishToTestFlight(Provider<Boolean> value) {
        publishToTestFlight.set(value)
    }

    void setPublishToTestFlight(Boolean value) {
        publishToTestFlight.set(value)
    }

    private final RegularFileProperty baseExportOptionsPlist = objects.fileProperty()
    private final RegularFileProperty finalExportOptionsPlist = objects.fileProperty()

    RegularFileProperty getExportOptionsPlist() {
        baseExportOptionsPlist
    }

    Provider<RegularFile> getFinalExportOptionsPlist() {
        finalExportOptionsPlist.present ? finalExportOptionsPlist : baseExportOptionsPlist
    }

    void setExportOptionsPlist(Provider<RegularFile> value) {
        baseExportOptionsPlist.set(value)
    }

    void setExportOptionsPlist(File value) {
        baseExportOptionsPlist.set(value)
    }

    Provider<ExportOptions> getExportOptions() {
        baseExportOptionsPlist.map({
            def options = it.asFile.exists() ? ExportOptions.open(it.asFile) : new ExportOptions()
            exportOptionsActions.each {
                it.execute(options)
            }
            options
        })
    }

    private final MapProperty<String, String> provisioningProfiles = objects.mapProperty(String, String)

    @Input
    MapProperty<String, String> getProvisioningProfiles() {
        provisioningProfiles
    }

    void setProvisioningProfiles(Map<String, String> value) {
        provisioningProfiles.set(value)
    }

    void setProvisioningProfiles(Provider<Map<String, String>> value) {
        provisioningProfiles.set(value)
    }

    private final List<Action<ExportOptions>> exportOptionsActions = []

    void exportOptions(Action<ExportOptions> action) {
        exportOptionsActions << action

        finalExportOptionsPlist.set(layout.buildDirectory.file("export/exportOptions.plist").map({
            def f = it.asFile
            f.parentFile.mkdirs()
            it.asFile.text = exportOptions.get().toXMLPropertyList()
            it
        }))
    }

    void exportOptions(Closure closure) {
        exportOptions(configureUsing(closure))
    }

    private final DirectoryProperty xcodeProjectPath = objects.directoryProperty()

    DirectoryProperty getXcodeProjectPath() {
        xcodeProjectPath
    }

    void setXcodeProjectPath(Provider<Directory> value) {
        xcodeProjectPath.set(value)
    }

    void setXcodeProjectPath(File value) {
        xcodeProjectPath.set(value)
    }

    private final DirectoryProperty xcodeWorkspacePath = objects.directoryProperty()

    DirectoryProperty getXcodeWorkspacePath() {
        xcodeWorkspacePath
    }

    void setXcodeWorkspacePath(Provider<Directory> value) {
        xcodeWorkspacePath.set(value)
    }

    void setXcodeWorkspacePath(File value) {
        xcodeWorkspacePath.set(value)
    }

    private final DirectoryProperty xcodeProjectDirectory = objects.directoryProperty()

    DirectoryProperty getXcodeProjectDirectory() {
        xcodeProjectDirectory
    }

    void setXcodeProjectDirectory(Provider<Directory> value) {
        xcodeProjectDirectory.set(value)
    }

    void setXcodeProjectDirectory(File value) {
        xcodeProjectDirectory.set(value)
    }

    private final Property<Boolean> preferWorkspace = objects.property(Boolean)

    Property<Boolean> getPreferWorkspace() {
        preferWorkspace
    }

    void setPreferWorkspace(Provider<Boolean> value) {
        preferWorkspace.set(value)
    }

    void setPreferWorkspace(Boolean value) {
        preferWorkspace.set(value)
    }

    private final Property<String> projectBaseName = objects.property(String)

    Property<String> getProjectBaseName() {
        projectBaseName
    }

    void setProjectBaseName(Provider<String> value) {
        projectBaseName.set(value)
    }

    void setProjectBaseName(String value) {
        projectBaseName.set(value)
    }

    Provider<String> xcodeProjectFileName = projectBaseName.map({ it + ".xcodeproj" })
    Provider<String> xcodeWorkspaceFileName = projectBaseName.map({ it + ".xcworkspace" })
    Provider<String> preferredProjectFileName = preferWorkspace.flatMap({
        it ? xcodeWorkspaceFileName : xcodeProjectFileName
    })

    Provider<Directory> projectPath = xcodeProjectDirectory.flatMap({
        def firstCheck = it.dir(preferredProjectFileName)
        if (firstCheck.forUseAtConfigurationTime().get().asFile.exists()) {
            return firstCheck
        }
        it.dir(xcodeProjectFileName)
    })

    protected static class CocoaPods implements ExecSpec {
        private final Property<String> version = objects.property(String)

        Property<String> getVersion() {
            return version
        }

        void setVersion(String version) {
            this.version.set(version)
        }

        void setVersion(Provider<String> version) {
            this.version.set(version)
        }
    }

    CocoaPods cocoapods = objects.newInstance(CocoaPods)

    void cocoapods(Action<ExecSpec> action) {
        action.execute(cocoapods)
    }

    void cocoapods(@ClosureParams(value = FromString.class,
            options = ["wooga.gradle.build.unity.ios.IOSBuildPluginExtension.CocoaPods"]) Closure configure) {
        cocoapods(configureUsing(configure))
    }
}
