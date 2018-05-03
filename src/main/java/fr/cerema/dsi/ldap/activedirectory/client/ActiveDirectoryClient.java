package fr.cerema.dsi.ldap.activedirectory.client;

import org.apache.directory.api.ldap.model.entry.Entry;

import java.util.Set;

public interface ActiveDirectoryClient {

    static int AD_GLOBAL_SECURITYGROUP_FLAGS = 0x80000002;
    static String AD_GROUP_OBJECTCLASS = "group";
    static String AD_USER_OBJECTCLASS = "user";


    Set<String> getUsersForDN(String dn, boolean recursive);

    Set<String> getGroupsForDN(String dn, boolean recursive);

    Set<String> findBySAMAccountName(String sAMAccountName, String searchBase);

    String createSecurityGroup(String dn, String samAccountName) ;

    String addEntityToGroup(String entityDn, String groupDN);

    Entry getByObjectSid(String objectSid, String searchBase);

    //String getObjectSid(String objectSID, String searchBase);
}
