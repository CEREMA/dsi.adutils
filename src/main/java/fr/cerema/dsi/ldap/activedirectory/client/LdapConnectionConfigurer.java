/*
 * Copyright (c) 2018 - Alain CHARLES
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package fr.cerema.dsi.ldap.activedirectory.client;

import org.apache.directory.ldap.client.api.LdapConnectionConfig;

/**
 * This is an helper class for configuring the ldap connection to AD
 * <p>
 * It holds an {@link LdapConnectionConfig} and exposes methods to configure it.
 */
public class LdapConnectionConfigurer {

    private LdapConnectionConfig ldapConnectionConfig = new LdapConnectionConfig();

    /**
     * Returns the {@link LdapConnectionConfig} used for configuring the ldap connection
     * @return the ldapConnectionConfig
     */
    public LdapConnectionConfig getLdapConnectionConfig() {
            return this.ldapConnectionConfig;
    }

    /**
     * Configures the ldap host
     * @param ldapHostName the hostname of the active directory ldap server
     * @return the instance itself for coding facilities
     */
    public LdapConnectionConfigurer configureLdapHost(String ldapHostName) {
        this.ldapConnectionConfig.setLdapHost(ldapHostName);
        return this;
    }

    /**
     * Configures the ldap port
     * @param ldapPort the port number of the active directory ldap server
     * @return the instance itself for coding facilities
     */
    public LdapConnectionConfigurer configureLdapPort(int ldapPort) {
        this.ldapConnectionConfig.setLdapPort(ldapPort);
        return this;
    }

    /**
     * Configures the ldap user (if necessary)
     * @param dn user's dn to be used for connecting to the AD ldap server
     * @return the instance itself for coding facilities
     */
    public LdapConnectionConfigurer configureDn(String dn) {
        this.ldapConnectionConfig.setName(dn);
        return this;
    }


    /**
     * Configures the ldap user password
     * @param password the password of the user used by ldap connection for requesting the ldap server
     * @return the instance itself for coding facilities
     */
    public LdapConnectionConfigurer configureCredentials(String password) {
        this.ldapConnectionConfig.setCredentials(password);
        return this;
    }


    /**
     * For debugging purpose
     * @return the ldap connection parameters
     */
    public String toString() {
        return "LDAP Connection parameters :"  +
                " Host : " + this.ldapConnectionConfig.getLdapHost() +
                " - Port : " + this.ldapConnectionConfig.getLdapPort() +
                " - Dn Name : " + this.ldapConnectionConfig.getName() +
                " - Password : " + "PROTECTED";
    }

}
