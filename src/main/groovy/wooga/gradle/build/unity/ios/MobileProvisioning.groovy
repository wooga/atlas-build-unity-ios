package wooga.gradle.build.unity.ios

trait MobileProvisioning {
    abstract String getName()

    abstract String getAppIdName()

    abstract String getUuid()

    abstract String getTeamName()

    abstract List<String> getTeamIdentifier()

    abstract Date getExpirationDate()

    abstract Date getCreationDate()

    abstract Boolean isExpired()

    abstract Integer getTimeToLive()

    abstract Boolean isXcodeManaged()

    abstract Integer getVersion()

    abstract Map getEntitlements()

    boolean equals(MobileProvisioning o) {
        if (this.is(o)) return true

        if (!(o instanceof MobileProvisioning)) {
            return false
        }

        MobileProvisioning that = (MobileProvisioning) o

        if (appIdName != that.appIdName) return false
        if (creationDate != that.creationDate) return false
        if (entitlements != that.entitlements) return false
        if (expirationDate != that.expirationDate) return false
        if (name != that.name) return false
        if (teamIdentifier != that.teamIdentifier) return false
        if (teamName != that.teamName) return false
        if (timeToLive != that.timeToLive) return false
        if (uuid != that.uuid) return false
        if (version != that.version) return false
        if (xcodeManaged != that.xcodeManaged) return false

        return true
    }

    int hashCode() {
        int result
        result = (uuid != null ? uuid.hashCode() : 0)
        result = 31 * result + (appIdName != null ? appIdName.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (teamIdentifier != null ? teamIdentifier.hashCode() : 0)
        result = 31 * result + (teamName != null ? teamName.hashCode() : 0)
        result = 31 * result + (timeToLive != null ? timeToLive.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0)
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0)
        result = 31 * result + (xcodeManaged != null ? xcodeManaged.hashCode() : 0)
        result = 31 * result + (entitlements != null ? entitlements.hashCode() : 0)
        return result
    }
}
