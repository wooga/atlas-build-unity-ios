package wooga.gradle.wooga.cocoapods

import com.wooga.gradle.BaseSpec
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

trait CocoapodsInstallSpec implements BaseSpec {

    private final RegularFileProperty executable = objects.fileProperty()

    @InputFile
    RegularFileProperty getExecutable() {
        return executable
    }

    void setExecutable(Provider<RegularFile> executable) {
        this.executable.set(executable)
    }

    void setExecutable(RegularFile executable) {
        this.executable.set(executable)
    }

    void setExecutable(File executable) {
        this.executable.set(executable)
    }

    private final Property<String> version = objects.property(String)

    @Input
    Property<String> getVersion() {
        return version
    }

    void setVersion(String version) {
        this.version.set(version)
    }

    void setVersion(Provider<String> version) {
        this.version.set(version)
    }

    private final MapProperty<String, List<String>> gemExtensions = objects.mapProperty(String, List<String>)

    @Input
    @Optional
    MapProperty<String, List<String>> getGemExtensions() {
        return gemExtensions
    }

    void setGemExtensions(Map<String,List<String>> gems) {
        this.gemExtensions.set(gems)
    }

    void setGemExtensions(Provider<Map<String, String>> gems) {
        this.gemExtensions.set(gems)
    }

    void addGemExtension(String name, String version) {
        gemExtensions.put(name, [version])
    }

    void addGemExtension(String name, List<String> extraParts) {
        gemExtensions.put(name, extraParts)
    }
}
