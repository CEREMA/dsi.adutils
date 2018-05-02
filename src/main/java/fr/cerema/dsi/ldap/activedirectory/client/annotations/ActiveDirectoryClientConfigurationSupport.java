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
        return new ActiveDirectoryClientImpl(this.getLdapConnectionConfigurer().getLdapConnectionConfig());
    }
}
