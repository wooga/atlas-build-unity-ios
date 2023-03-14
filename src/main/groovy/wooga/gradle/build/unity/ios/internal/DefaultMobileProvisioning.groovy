package wooga.gradle.build.unity.ios.internal

import com.dd.plist.NSDictionary
import com.dd.plist.XMLPropertyListParser
import wooga.gradle.build.unity.ios.MobileProvisioning

class DefaultMobileProvisioning implements MobileProvisioning, GroovyInterceptable {
    private static final String UUID_KEY = "UUID"
    private static final String APP_ID_NAME_KEY = "AppIDName"
    private static final String NAME_KEY = "Name"
    private static final String TEAM_IDENTIFIER_KEY = "TeamIdentifier"
    private static final String TEAM_NAME_KEY = "TeamName"
    private static final String TIME_TO_LIVE_KEY = "TimeToLive"
    private static final String VERSION_KEY = "Version"
    private static final String CREATION_DATE_KEY= "CreationDate"
    private static final String EXPIRATION_DATE_KEY= "ExpirationDate"
    private static final String IS_XCODE_MANAGED_KEY= "IsXcodeManaged"
    private static final String DEVELOPER_CERTIFICATES_KEY= "DeveloperCertificates"
    private static final String DER_ENCODED_PROFILE_KEY = "DER-Encoded-Profile"
    private static final String ENTITLEMENTS_KEY = "Entitlements"

    private Map plist = new HashMap<String, Object>()

    DefaultMobileProvisioning(Map data) {
        plist = data
    }

    private static int PLIST_START_OFFSET = 0x3E

    String getName() {
        plist[NAME_KEY]
    }

    String getAppIdName() {
        plist[APP_ID_NAME_KEY]
    }

    String getUuid() {
        plist[UUID_KEY]
    }

    String getTeamName() {
        plist[TEAM_NAME_KEY]
    }

    List<String> getTeamIdentifier() {
        plist[TEAM_IDENTIFIER_KEY] as List<String>
    }

    Date getExpirationDate() {
        (Date) plist[EXPIRATION_DATE_KEY]
    }

    Date getCreationDate() {
        (Date) plist[CREATION_DATE_KEY]
    }

    Boolean isExpired() {
        expirationDate < new Date()
    }

    Integer getTimeToLive() {
        Integer.parseInt(plist[TIME_TO_LIVE_KEY].toString())
    }

    Boolean isXcodeManaged() {
        Boolean.parseBoolean(plist[IS_XCODE_MANAGED_KEY].toString())
    }

    Integer getVersion() {
        Integer.parseInt(plist[VERSION_KEY].toString())
    }

    Map getEntitlements() {
        (Map) plist[ENTITLEMENTS_KEY]
    }

    static DefaultMobileProvisioning open(InputStream data) {
        if (data.skip(PLIST_START_OFFSET) == PLIST_START_OFFSET) {
            def lines = data.readLines()
            def plistEndIndex = lines.findIndexOf { it.startsWith("</plist>") }
            lines[plistEndIndex] = "</plist>"
            def bytes = lines.subList(0, plistEndIndex + 1).join("\n").bytes

            NSDictionary rootDict = (NSDictionary) XMLPropertyListParser.parse(bytes)
            return new DefaultMobileProvisioning((Map) rootDict.toJavaObject())
        } else {
            throw new IllegalArgumentException("Provided file is to short to be a provisioning profile")
        }
    }

    static DefaultMobileProvisioning open(File plistFile) {
        open(plistFile.newInputStream())
    }
}
