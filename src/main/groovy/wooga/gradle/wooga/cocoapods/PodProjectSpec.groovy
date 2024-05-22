package wooga.gradle.wooga.cocoapods

import com.wooga.gradle.BaseSpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory

trait PodProjectSpec implements BaseSpec {

    private final DirectoryProperty projectDirectory = objects.directoryProperty()

    @InputDirectory
    DirectoryProperty getProjectDirectory() {
        projectDirectory
    }

    void setProjectDirectory(Provider<Directory> value) {
        projectDirectory.set(value)
    }

    void setProjectDirectory(File value) {
        projectDirectory.set(value)
    }


}
