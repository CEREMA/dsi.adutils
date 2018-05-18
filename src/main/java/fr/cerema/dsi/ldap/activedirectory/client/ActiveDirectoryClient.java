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
     * Returns the set of {@link AbstractAdObject} that are members of the given DN (which de facto must be a group), if any
     * @param dn the distinguished name of the group whose members will be returned
     * @param recursive
     * <p> if true, member groups will be recursively scanned and their users will be added to the returned set
     * <p> if false, only members of the given group dn will be returned
     * @return the set of objects members (recursively or not) of the given group dn
     */
    Set<AbstractAdObject> getMembersForDN(String dn, boolean recursive);

    /**
     * Returns the set of {@link AdGroup} containing the object whose dn is given as argument, if any
     * @param dn the distinguished name of the object we want to get the containing groups
     * @param recursive
     * <p> if true, member groups will be recursively scanned and their users will be added to the returned set
     * <p> if false, only members of the given group dn will be returned
     * @return the set of objects members (recursively or not) of the given group dn
     */
    Set<AdGroup> getGroupsForDN(String dn, boolean recursive);

    /**
     * Returns a Set of String containing the dn of objects whose sAMAccountNames <b>begin</b> with the sAMAccountName param.
     * @param sAMAccountName the sAMAccountName
     * @param searchBase the searchBase in the ldap directory
     * @return the set of String containing dn whose sAMAccountNames begin with the given parameter
     */
    Set<String> findBySAMAccountName(String sAMAccountName, String searchBase);

    /**
     * Returns the only object whose sAMAccountName is given as sAMAccountName parameter, or null if not found
     * @param sAMAccountName the sAMAccountName of the object to get
     * @param searchBase the searchBase to search from
     * @return the matching object or null if not found
     */
    AbstractAdObject getBySAMAccountName(String sAMAccountName, String searchBase);

    /**
     * Deletes the active directory object whose dn is given as dn parameter
     * @param dn the dn of object to remove
     */
    void deleteByDn(String dn);

    /**
     * Creates a security group with the given Dn
     * @param dn the dn of the group to create
     * @return the {@link AdGroup} created, or null if it fails for some reason (see logs for details)
     */
    AdGroup createSecurityGroup(String dn);

    /**
     * Creates a security group with the given Dn and description
     * @param dn the dn of the group to create
     * @param description the description of the group
     * @return the {@link AdGroup} created, or null if it fails for some reason (see logs for details)
     */
    AdGroup createSecurityGroup(String dn, String description) ;

    /**
     * Creates a security group with the given Dn, description and sAMAccountName
     * @param dn the dn of the group to create
     * @param description the description of the group
     * @param sAMAccountName the sAMAccountName of the group
     * @return the {@link AdGroup} created, or null if it fails for some reason (see logs for details)
     */
    AdGroup createSecurityGroup(String dn, String description, String sAMAccountName);

    /**
     * Adds an existing entity to an existing group
     * @param entityDn the dn of the entity to add to the group
     * @param groupDN the group's dn we must add the entity in
     */
    void addEntityToGroup(String entityDn, String groupDN);

    /**
     * Removes an object from a group
     * @param entityDn then dn of the entity to remove from the group
     * @param groupDn the dn of the group the entity must be removed from
     */
    void removeEntityFromGroup(String entityDn, String groupDn);

    /**
     * Return the object whose objectSid is given as parameter
     * @param objectSid the objectSid to search
     * @param searchBase the searchBase dn
     * @return the found object or null if not found
     */
    AbstractAdObject getObjectBySid(String objectSid, String searchBase);

    /**
     * Returns a set of objects whose objectSids are given in parameter
     * @param sids the {@link List} of objectSid to search
     * @param searchBase the searchBase
     * @return the set of objects whose objectSids are those given as parameters
     */
    Set<AbstractAdObject> getObjectsBySid(List<String> sids, String searchBase);

    /**
     * Returns a set of {@link AbstractAdObject} whose commonName (ie. cn)
     * <b>contains</b> the commonName given as parameter.
     * <p>
     * Search filter is based on searchBase DN.
     * @param commonName the string that must be contained in results commonName
     * @param searchBase the dn of the search base
     * @return {@link AbstractAdObject} whose common name <b>contains</b> commonName parameter
     */
    Set<AbstractAdObject> findByCommonName(String commonName, String searchBase);

    /**
     * Returns the only Object whose dn is given as parameter, or null if not found
     * @param dn the dn of the object to get
     * @return the object found or null if no matching dn found
     */
    AbstractAdObject getByDn(String dn);

}
