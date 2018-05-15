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

public class LdapConnectionConfigurer {

    private LdapConnectionConfig ldapConnectionConfig = new LdapConnectionConfig();

    public LdapConnectionConfig getLdapConnectionConfig() {
        return this.ldapConnectionConfig;
    }

    public LdapConnectionConfigurer configureLdapHost(String ldapHostName) {
        this.ldapConnectionConfig.setLdapHost(ldapHostName);
        return this;
    }

    public LdapConnectionConfigurer configureLdapPort(int ldapPort) {
        this.ldapConnectionConfig.setLdapPort(ldapPort);
        return this;
    }

    public LdapConnectionConfigurer configureDn(String dn) {
        this.ldapConnectionConfig.setName(dn);
        return this;
    }

    public LdapConnectionConfigurer configureCredentials(String password) {
        this.ldapConnectionConfig.setCredentials(password);
        return this;
    }


    public String toString() {
        return "LDAP Connection parameters :"  +
                " Host : " + this.ldapConnectionConfig.getLdapHost() +
                " - Port : " + this.ldapConnectionConfig.getLdapPort() +
                " - Dn Name : " + this.ldapConnectionConfig.getName() +
                " - Password : " + "PROTECTED";
    }

}
