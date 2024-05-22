package wooga.gradle.wooga.cocoapods

import com.wooga.gradle.PropertyLookup

class CocoapodsPluginConventions {

    static final PropertyLookup homeDir = new PropertyLookup(
            "COCOAPODS_HOME_DIR",
            "cocoapods.homeDir",
            null
    )

    static final PropertyLookup version = new PropertyLookup(
            "COCOAPODS_VERSION",
            "cocoapods.version",
            "1.14.3"
    )
    static final PropertyLookup executable = new PropertyLookup(
            "COCOAPODS_EXECUTABLE",
            "cocoapods.executable",
            null
    )
}
