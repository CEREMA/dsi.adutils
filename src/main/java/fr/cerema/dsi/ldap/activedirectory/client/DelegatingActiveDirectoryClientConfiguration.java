package fr.cerema.dsi.ldap.activedirectory.client;

import fr.cerema.dsi.ldap.activedirectory.client.annotations.ActiveDirectoryClientConfigurationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegatingActiveDirectoryClientConfiguration extends ActiveDirectoryClientConfigurationSupport {

    @Autowired
    protected ActiveDirectoryClientConfigurer activeDirectoryClientConfigurer;

    @Override
    protected void configureLdapConnection(LdapConnectionConfigurer configurer) {
        activeDirectoryClientConfigurer.configureLdapConnection(configurer);
    }
}
