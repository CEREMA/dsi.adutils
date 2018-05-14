package fr.cerema.dsi.ldap.activedirectory.client;

import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AbstractAdObject;
import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AdGroup;
import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AdUser;
import org.apache.directory.api.ldap.model.entry.Entry;

import java.util.List;
import java.util.Set;

public interface ActiveDirectoryClient {

    static int AD_GLOBAL_SECURITYGROUP_FLAGS = 0x80000002;
    static String AD_GROUP_OBJECTCLASS = "group";
    static String AD_USER_OBJECTCLASS = "user";


    Set<AdUser> getUsersForDN(String dn, boolean recursive);

    Set<AdGroup> getGroupsForDN(String dn, boolean recursive);

    Set<String> findBySAMAccountName(String sAMAccountName, String searchBase);

    String createSecurityGroup(String dn, String samAccountName) ;

    String addEntityToGroup(String entityDn, String groupDN);

    AbstractAdObject getObjectBySid(String objectSid, String searchBase);

    Set<AbstractAdObject> getObjectsBySid(List<String> sids, String searchBase);

}
