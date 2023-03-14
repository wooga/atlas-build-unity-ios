package com.wooga.gradle.test.ios

import com.dd.plist.NSDate
import com.dd.plist.NSDictionary
import com.dd.plist.XMLPropertyListParser
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import wooga.gradle.build.unity.ios.MobileProvisioning

class MobileProvisionMock implements MobileProvisioning {

    String uuid = UUID.randomUUID()
    String appIdName = "Mock AppId"
    String name = "Mock Profile"
    List<String> teamIdentifier = ["randomId"]
    String teamName = "Mock Team"
    Integer timeToLive = 265
    Integer version = 1

    private Date creationDate

    void setCreationDate(Date creationDate) {
        this.creationDate = normalizeDate(creationDate)
    }

    Date getCreationDate() {
        return creationDate
    }

    private Date expirationDate

    void setExpirationDate(Date expirationDate) {
        this.expirationDate = normalizeDate(expirationDate)
    }

    Date getExpirationDate() {
        return expirationDate
    }

    Boolean xcodeManaged = false
    Map entitlements = [:]

    MobileProvisionMock() {
        setCreationDate(new Date())
        setExpirationDate(new Date(creationDate.time + 100000))
    }

    MobileProvisionMock(String uuid, String name) {
        super()
        this.uuid = uuid
        this.name = name
    }

    @Override
    Boolean isExpired() {
        expirationDate < new Date()
    }

    @Override
    Boolean isXcodeManaged() {
        xcodeManaged
    }

    private Date normalizeDate(Date input) {
        def nsDateString = NSDate.fromJavaObject(input).toXMLPropertyList()
        ((NSDate) XMLPropertyListParser.parse(nsDateString.bytes)).toJavaObject(Date)
    }

    byte[] asBytes() {
        def s = new ByteArrayOutputStream()
        Random rd = new Random()
        byte[] intro = new byte[0x3E]
        rd.nextBytes(intro)
        //fill first bytes with random bytes
        s.write(intro, 0, intro.length)

        //write the provisioning profile plist content
        def plistRaw = [
                "UUID"                 : uuid,
                "AppIDName"            : appIdName,
                "Name"                 : name,
                "TeamIdentifier"       : teamIdentifier,
                "TeamName"             : teamName,
                "TimeToLive"           : timeToLive,
                "Version"              : version,
                "CreationDate"         : creationDate,
                "ExpirationDate"       : expirationDate,
                "IsXcodeManaged"       : xcodeManaged,
                "DeveloperCertificates": [],
                "DER-Encoded-Profile"  : "",
                "Entitlements"         : entitlements,
        ]
        s.write(NSDictionary.fromJavaObject(plistRaw).toXMLPropertyList().bytes)

        //write other random data as stand in for certificates
        byte[] outro = new byte[200]
        rd.nextBytes(intro)
        //fill first bytes with random bytes
        s.write(outro, 0, intro.length)
        s.toByteArray()
    }

    static File createMock(@ClosureParams(value = FromString.class, options = "com.wooga.gradle.test.ios.MobileProvisionMock") Closure configuration = null) {
        def mock = new MobileProvisionMock()
        if (configuration) {
            configuration.setDelegate(mock)
            configuration.call(mock)
        }
        def file = File.createTempFile("mock", ".mobileprovisioning")
        file.bytes = mock.asBytes()
        file
    }
}
