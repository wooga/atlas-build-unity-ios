package wooga.gradle.wooga.cocoapods

import com.wooga.gradle.BaseSpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

trait PodSpec implements BaseSpec {
    private final DirectoryProperty homeDirectory = objects.directoryProperty()

    @InputDirectory
    @Optional
    DirectoryProperty getHomeDirectory() {
        return homeDirectory
    }

    void setHomeDirectory(Provider<Directory> homeDir) {
        this.homeDirectory.set(homeDir)
    }

    void setHomeDirectory(Directory homeDir) {
        this.homeDirectory.set(homeDir)
    }

    void setHomeDirectory(File homeDir) {
        this.homeDirectory.set(homeDir)
    }

}