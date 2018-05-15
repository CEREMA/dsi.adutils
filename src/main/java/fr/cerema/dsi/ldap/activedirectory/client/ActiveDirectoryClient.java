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

import fr.cerema.dsi.ldap.activedirectory.client.model.AbstractAdObject;
import fr.cerema.dsi.ldap.activedirectory.client.model.AdGroup;
import fr.cerema.dsi.ldap.activedirectory.client.model.AdUser;

import java.util.List;
import java.util.Set;

/**
 * This interface exposes the API of the Active Directory Client
 * @author alain.charles
 */
public interface ActiveDirectoryClient {

    /**
     * the flag for creating active directory security groups (source : Microsoft)
     */
    static int AD_GLOBAL_SECURITYGROUP_FLAGS = 0x80000002;
    /**
     * the objectClass identifying a group
     */
    static String AD_GROUP_OBJECTCLASS = "group";
    /**
     * the objectClass identifying a user
     */
    static String AD_USER_OBJECTCLASS = "user";


    /**
     * Returns the set of {@link AdUser} that are members of the given DN, if any
     * @param dn the distinguished name of the group whose members will be returned
     * @param recursive
     * <p> if true, groups will be recursively scanned and their users will be added to the returned set
     * <p> if false, only users member of the given group dn will be returned
     * @return the set of users member (recursively or not) of the given group dn
     */
    Set<AdUser> getUsersForDN(String dn, boolean recursive);

    Set<AdGroup> getGroupsForDN(String dn, boolean recursive);

    Set<String> findBySAMAccountName(String sAMAccountName, String searchBase);

    AdGroup createSecurityGroup(String dn);

    AdGroup createSecurityGroup(String dn, String description) ;

    AdGroup createSecurityGroup(String dn, String description, String sAMAccountName);

    void addEntityToGroup(String entityDn, String groupDN);

    void removeEntityFromGroup(String entityDn, String groupDn);

    AbstractAdObject getObjectBySid(String objectSid, String searchBase);

    Set<AbstractAdObject> getObjectsBySid(List<String> sids, String searchBase);

    AbstractAdObject getByDn(String dn);

}
