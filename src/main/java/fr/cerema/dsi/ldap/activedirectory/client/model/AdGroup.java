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

/**
 * Class whose instances are concrete representation of an Active Directory group
 * <p>
 * This class holds useful attributes of active directory groups
 * @author alain.charles
 */
public class AdGroup extends AbstractAdObject{

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

    private String description;

    private byte[] objectSid;

    private String sAMAccountName;

    private String commonName;

    /**
     * Returns the group's description
     * @return the description of the group
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the group's description
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
