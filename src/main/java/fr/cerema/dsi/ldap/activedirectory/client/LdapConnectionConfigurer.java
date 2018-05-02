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
