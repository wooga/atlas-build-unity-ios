package wooga.gradle.build.unity.ios.internal

import com.wooga.gradle.test.ios.MobileProvisionMock
import spock.lang.Shared
import spock.lang.Specification

class MobileProvisioningSpec extends Specification {

    @Shared
    MobileProvisionMock profile

    def setup() {
        profile = new MobileProvisionMock()
    }

    def "basic mock which can act as a standin for a mobile provision file"() {
        given: "some known profile values"
        profile.uuid = UUID.randomUUID()
        profile.name = "some name"


        and: "a profile file"
        def f = File.createTempFile("test", ".mobileprovisioning")
        f.bytes = profile.asBytes()

        when:
        def subject = DefaultMobileProvisioning.open(f)

        then:
        subject.uuid == profile.uuid
        subject.name == profile.name
        subject.teamName == profile.teamName
        subject.teamIdentifier == profile.teamIdentifier
        subject.expirationDate.time == profile.expirationDate.time
        subject.creationDate.time == profile.creationDate.time
        subject.timeToLive == profile.timeToLive
        subject.xcodeManaged == profile.xcodeManaged
    }
}
