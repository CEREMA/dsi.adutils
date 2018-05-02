package fr.cerema.dsi.ldap.activedirectory.client;

public interface ActiveDirectoryClientConfigurer {

    default void configureLdapConnection(LdapConnectionConfigurer ldapConnectionConfigurer) {    }


}
