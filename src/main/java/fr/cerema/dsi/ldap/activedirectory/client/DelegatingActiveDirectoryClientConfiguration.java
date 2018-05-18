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

import fr.cerema.dsi.ldap.activedirectory.client.annotations.ActiveDirectoryClientConfigurationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * This class holds the activeDirectoryClientConfigurer implementation that is defined by the developper
 * (a minima for defining connection parameters).
 * <p>
 * it is used by the framework who calls back the configureLdapConnection method implemented by
 * the {@link ActiveDirectoryClientConfigurer} defined by the developper
 * and not the default one defined in
 */
@Configuration
public class DelegatingActiveDirectoryClientConfiguration extends ActiveDirectoryClientConfigurationSupport {

    @Autowired
    protected ActiveDirectoryClientConfigurer activeDirectoryClientConfigurer;

    @Override
    protected void configureLdapConnection(LdapConnectionConfigurer configurer) {
        activeDirectoryClientConfigurer.configureLdapConnection(configurer);
    }
}
