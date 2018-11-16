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
 * Class whose instances are concrete representation of an Active Directory user
 * <p>
 * This class holds useful attributes of active directory users
 * @author alain.charles
 */
public class AdUser extends AbstractAdObject{


    private byte[] objectSid;

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

    private String userPrincipalName ;

    private String firstname;

    private String surname;

    private String mail;

    private String telephoneNumber;

    private String department;

    /**
     * Returns the active directory user's principal name
     * @return the principal name
     */
    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    /**
     * Sets the active directory user's principal name
     * @param userPrincipalName the principal name
     */
    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    /**
     * Returns the active directory user's firstName
     * @return the firts nalme
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the active directory user's firstname
     * @param firstname the firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Returns the active directory user's surname
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the active directory user's surname
     * @param surname the surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * returns the active directory user's mail address
     * @return the email address
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets the active directory user's mail address
     * @param mail the email address
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Returns the active directory user's phone number
     * @return the phone number
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * Sets the active directory user's phone number
     * @param telephoneNumber the phone number
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * Returns the active directory user's department
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the active directory user's department
     * @param department the department
     */
    public void setDepartment(String department) {
        this.department = department;
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


}
