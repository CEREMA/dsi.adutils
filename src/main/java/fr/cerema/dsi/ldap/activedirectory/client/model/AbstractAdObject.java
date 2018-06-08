/*
 * Copyright (c) 2018 - Alain CHARLES
 *
 *  Licensed under the CeCILL Version 2.0 License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.cecill.info/licences/Licence_CeCILL_V2-fr.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package fr.cerema.dsi.ldap.activedirectory.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cerema.dsi.ldap.activedirectory.client.utils.SidConverter;

import java.util.Objects;

/**
 * Generic abstract class holding base active directory object informations.
 * @author alain.charles
 */

public abstract class AbstractAdObject {

    private byte[] objectSid;

    private String distinguishedName;

    private String sAMAccountName;

    private String commonName;


    /**
     * Returns the object's sid as an array of bytes
     * @return the object's sid
     */
    @JsonIgnore
    public byte[] getObjectSid() {
        return objectSid;
    }

    /**
     * Sets the object's Sid
     * @param objectSid the object's Sid
     */
    public void setObjectSid(byte[] objectSid) {
        this.objectSid = objectSid;
    }

    /**
     * Returns the object's sid as a human readable String
     * <p>
     * The Sid syntax is used. It means the sid is returned as a String like S-1-5-21-1779429759-2771315062-123184451-12784
     * @return the object's sid as a String in sid syntax
     */
    @JsonProperty("objectSid")
    public String getObjectSidAsString() {
        return SidConverter.bytesToString(this.getObjectSid());
    }

    /**
     * Returns the object's distinguishedName
     * @return the distinguishedName
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the object's distinguishedName
     * @param distinguishedName the distinguishedName
     */
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    /**
     * Returns the object's sAMAccountName
     * @return the object's sAMAccountName
     */
    public String getsAMAccountName() {
        return sAMAccountName;
    }

    /**
     * Sets the object's sAMAccountName
     * @param sAMAccountName the sAMAccountName
     */
    public void setsAMAccountName(String sAMAccountName) {
        this.sAMAccountName = sAMAccountName;
    }

    /**
     * Returns the object's commonName (CN)
     * @return the object's CN
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets the object's commonName
     * @param commonName the object's commonName
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Equals method implementation
     * @param o the object to compare to
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAdObject that = (AbstractAdObject) o;
        return Objects.equals(distinguishedName, that.distinguishedName);
    }

    /**
     * Returns the hashCode : based on the distinguished name's hashcode
     * @return the object's hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(distinguishedName);
    }
}
