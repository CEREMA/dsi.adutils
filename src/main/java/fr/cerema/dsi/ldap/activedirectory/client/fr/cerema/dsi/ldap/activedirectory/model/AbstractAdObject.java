package fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.utils.SidConverter;

import java.util.Objects;

public abstract class AbstractAdObject {

    private byte[] objectSid;

    private String distinguishedName;

    private String sAMAccountName;

    private String commonName;


   @JsonIgnore
    public byte[] getObjectSid() {
        return objectSid;
    }

    public void setObjectSid(byte[] objectSid) {
        this.objectSid = objectSid;
    }

    @JsonProperty("objectSid")
    public String getObjectSidAsString() {
        return SidConverter.bytesToString(this.getObjectSid());
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getsAMAccountName() {
        return sAMAccountName;
    }

    public void setsAMAccountName(String sAMAccountName) {
        this.sAMAccountName = sAMAccountName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAdObject that = (AbstractAdObject) o;
        return Objects.equals(distinguishedName, that.distinguishedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distinguishedName);
    }
}
