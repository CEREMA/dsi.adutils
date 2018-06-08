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

package fr.cerema.dsi.ldap.activedirectory.client;

/**
 * This interface exposes methods for configuring the Active Directory Client bean.
 * Up to now, only connection's parameters can be configured. The connection pool cannot yet be configured (next version)
 */
public interface ActiveDirectoryClientConfigurer {

    /**
     * Backend method that is called by the framework for configuring the ldap connection
     * Must be overriden in the configuration class implementing the interface
     * @param ldapConnectionConfigurer
     */
    default void configureLdapConnection(LdapConnectionConfigurer ldapConnectionConfigurer) {    }

}
