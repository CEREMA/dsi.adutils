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

import fr.cerema.dsi.ldap.activedirectory.client.ActiveDirectoryClient;
import fr.cerema.dsi.ldap.activedirectory.client.ActiveDirectoryClientImpl;
import fr.cerema.dsi.ldap.activedirectory.client.LdapConnectionConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * This class is loaded by the {@link EnableActiveDirectoryClient} annotation (it loads
 * {@link fr.cerema.dsi.ldap.activedirectory.client.DelegatingActiveDirectoryClientConfiguration} which extends
 * this class
 * <p>
 * it holds the {@link LdapConnectionConfigurer} and creates an instance
 * of {@link ActiveDirectoryClientImpl} which is put in the container with id activeDirectoryClient
 */
public class ActiveDirectoryClientConfigurationSupport implements ApplicationContextAware, ServletContextAware {

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private ServletContext servletContext;

    @Nullable
    private LdapConnectionConfigurer ldapConnectionConfigurer;

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    @Nullable
    public final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Nullable
    public final ServletContext getServletContext() {
        return this.servletContext;
    }


    /**
     * Callback for building the {@link LdapConnectionConfigurer}.
     * Delegates to {@link #configureLdapConnection}.
     */
    protected LdapConnectionConfigurer getLdapConnectionConfigurer() {
        if (this.ldapConnectionConfigurer == null) {
            this.ldapConnectionConfigurer = new LdapConnectionConfigurer();
            configureLdapConnection(this.ldapConnectionConfigurer);
        }
        return this.ldapConnectionConfigurer;
    }

    /**
     * Surcharger cette méthode pour configurer la connexion LDAP
     * @see LdapConnectionConfigurer
     */
    protected void configureLdapConnection(LdapConnectionConfigurer configurer) {
        configurer.configureLdapHost("localhost")
                .configureLdapPort(389)
                .configureDn(null)
                .configureCredentials(null);
    }



    @Bean(name = "activeDirectoryClient")
    public ActiveDirectoryClient createActiveDirectoryClient() {
        return new ActiveDirectoryClientImpl(this.getLdapConnectionConfigurer()
                                             .getLdapConnectionConfig());
    }
}
