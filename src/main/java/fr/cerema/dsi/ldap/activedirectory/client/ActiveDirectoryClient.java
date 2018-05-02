package fr.cerema.dsi.ldap.activedirectory.client;

import java.util.Set;

public interface ActiveDirectoryClient {

    public static int AD_GLOBAL_SECURITYGROUP_FLAGS = 0x80000002;
    //public static int AD_GLOBAL_SECURITYGROUP_FLAGS = -2147483646;
    public static String AD_GROUP_OBJECTCLASS = "group";
    public static String AD_USER_OBJECTCLASS = "user";


    public Set<String> getUsersForDN(String dn, boolean recursive);

    public Set<String> getGroupsForDN(String dn, boolean recursive);

    public Set<String> findBySAMAccountName(String sAMAccountName, String searchBase);

    public String createSecurityGroup(String dn, String samAccountName) ;

    public String addEntityToGroup(String entityDn, String groupDN);
}
