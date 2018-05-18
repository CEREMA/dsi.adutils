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

package fr.cerema.dsi.ldap.activedirectory.client.annotations;

import fr.cerema.dsi.ldap.activedirectory.client.DelegatingActiveDirectoryClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation used in a class marked with the  {@link org.springframework.context.annotation.Configuration}
 * instantiates the active directory and makes it available in the spring container with bean id activeDirectoryClient
 * <p>
 * The configuration class must implements {@link fr.cerema.dsi.ldap.activedirectory.client.ActiveDirectoryClientConfigurer}
 * so you can set the connection parameters by overriding the configureLdapConnection method
 *<p>
 *
 * <pre>
 *     {@code
 *      &#064Configuration
 *      &#064EnableActiveDirectoryClient
 *      public class ActiveDirectoryClientConfig implements ActiveDirectoryClientConfigurer {
 *
 *      &#064Override
 *      public void configureLdapConnection(LdapConnectionConfigurer ldapConnectionConfigurer) {
 *         ldapConnectionConfigurer.configureLdapHost("activeDirectoryHost")
 *                 .configureLdapPort(389)
 *                 .configureDn("userDN")
 *                 .configureCredentials("user Password);
 *     }
 * }
 *     }
 * </pre>
 *
 * The developper can also extends directly the {@link ActiveDirectoryClientConfigurationSupport} in the configuration class
 * and override the connection's configuration mathod(s).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DelegatingActiveDirectoryClientConfiguration.class)
public @interface EnableActiveDirectoryClient {
}
