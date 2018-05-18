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
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.ldap.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This is the core class of ActiveDirectoryClient
 * It implements the API defined in {@link ActiveDirectoryClient} interface
 */
public class ActiveDirectoryClientImpl implements ActiveDirectoryClient {

    private static Logger LOG;
    private LdapConnectionConfig ldapConnectionConfig;
    private LdapConnectionPool ldapConnectionPool;

    public ActiveDirectoryClientImpl(LdapConnectionConfig config) {
        LOG = LoggerFactory.getLogger(ActiveDirectoryClientImpl.class);
        this.ldapConnectionConfig = config;
        LOG.info("Bean activeDirectoryClient instance configured with : " + this.getLdapConnectionParameters());
        DefaultLdapConnectionFactory factory= new DefaultLdapConnectionFactory(config);
        factory.setTimeOut(0);
        this.ldapConnectionPool = new LdapConnectionPool(new DefaultPoolableLdapConnectionFactory(factory));
        LOG.info("LDAP Connection pool ready with default configuration.");
    }

    private String getLdapConnectionParameters() {
        return "ldap://" + this.ldapConnectionConfig.getName()
                + ":PROTECTED"
                + "@" + this.ldapConnectionConfig.getLdapHost()
                +":"+this.ldapConnectionConfig.getLdapPort();
    }

    @Override
    public AbstractAdObject getByDn(String dn) {
        LOG.info("getByDn called with : " + dn);
        AbstractAdObject result = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                Entry resultEntry = ldapConnection.lookup(dn);
                if (resultEntry != null) {
                    Attribute classes = resultEntry.get("objectClass");
                    if (classes.contains(AD_USER_OBJECTCLASS)) result = this.createUserFromUserEntry(resultEntry);
                    if (classes.contains(AD_GROUP_OBJECTCLASS)) result = this.createGroupFromGroupEntry(resultEntry);
                }
            }
            catch(LdapException lde) {
                LOG.error("An error occured while requesting the ldap server.");
                LOG.error("Message from  Server is :" +lde.getLocalizedMessage());
            }
            finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return result;
    }

    @Override
    public AbstractAdObject getBySAMAccountName(String sAMAccountName, String searchBase) {
        LOG.info("getBySAMAccountName called with sAMAccountName: " + sAMAccountName + " and searchBase: " + searchBase);
        AbstractAdObject result = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                EntryCursor entryCursor = ldapConnection.search(searchBase.toString(),
                        "(sAMAccountName=" + sAMAccountName + ")", SearchScope.SUBTREE, "*");
                entryCursor.next();
                Entry resultEntry=entryCursor.get();
                Attribute classes = resultEntry.get("objectClass");
                if (classes.contains(AD_USER_OBJECTCLASS)) result = this.createUserFromUserEntry(resultEntry);
                if (classes.contains(AD_GROUP_OBJECTCLASS)) result = this.createGroupFromGroupEntry(resultEntry);
            }
            catch(LdapException lde) {
                LOG.error("An error occured while requesting the ldap server.");
                LOG.error("Message from  Server is :" +lde.getLocalizedMessage());
            }
            catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from  Server is :" +ce.getLocalizedMessage());
            }
            finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return result;
    }

    public Set<AdGroup> getGroupsForDN(String dn, boolean recursive) {
        LOG.info("getGroupsForDN called with Dn:" + dn);
        Set<AdGroup> groups = new HashSet<AdGroup>();
        Set<String> groupsExplored = new HashSet<>();
        groups.addAll(this.getGroupsForDN(dn, recursive, groupsExplored));
        return groups;
    }

    public Set<AbstractAdObject> getMembersForDN(String dn, boolean recursive) {
        LOG.info("getAllUsersForDN called with Dn:" + dn);
        Set<AbstractAdObject> members = new HashSet<AbstractAdObject>();
        Set<String> groups = new HashSet<String>();
        members.addAll(this.getMembersForDN(dn, recursive, groups));
        return members;
    }

    @Override
    public Set<AbstractAdObject> findByCommonName(String commonName, String searchBase) {
        LOG.info("findByCommonName called with : " + commonName + " and searchBase: " +searchBase);
        Set<AbstractAdObject> result = new HashSet<>();
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                EntryCursor entryCursor = ldapConnection.search(searchBase, "(cn=" + "*" + commonName + "*" + ")", SearchScope.SUBTREE, "*");
                while (entryCursor.next()) {
                    Entry resultEntry = entryCursor.get();
                    Attribute classes = resultEntry.get("objectClass");
                    if (classes.contains(AD_USER_OBJECTCLASS)) result.add(this.createUserFromUserEntry(resultEntry));
                    if (classes.contains(AD_GROUP_OBJECTCLASS)) result.add(this.createGroupFromGroupEntry(resultEntry));
                }
            }
            catch(LdapException lde) {
                LOG.error("An error occured while requesting the ldap server.");
                LOG.error("Message from  Server is :" +lde.getLocalizedMessage());
            }
            catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from  Server is :" +ce.getLocalizedMessage());
            }
            finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }
        return result;
    }

    @Override
    public AdGroup createSecurityGroup(String dn) {
        return this.createSecurityGroup(dn, null, null);
    }

    @Override
    public AdGroup createSecurityGroup(String dn, String description) {
        return this.createSecurityGroup(dn, description, null);
    }

    @Override
    public void deleteByDn(String dn) {
        LOG.info("deleteSecurityGroup called with Dn: " + dn);
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                ldapConnection.delete(dn);
                LOG.info("Active directory object with dn " + dn + " successfully deleted.");
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for security group creation :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }
    }

    @Override
    public AdGroup createSecurityGroup(String dn, String description, String sAMAccountName) {
        LOG.info("createSecurityGroup called with Dn: " + dn + " description: " + description + " samAccountName: " + sAMAccountName);
        AdGroup groupCreated = null;
        String accountName = null;
        StringBuffer searchBase = new StringBuffer();

        // Arguments check
        try {
            Dn dnToCreate = new Dn(dn);
            accountName = ((sAMAccountName == null || "".equals(sAMAccountName) ? dnToCreate.getRdn().getValue() : sAMAccountName)) ;
        } catch (LdapInvalidDnException e) {
            LOG.error(dn + " is not a valid dn.");
            return null;
        }

        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                Entry entry = new DefaultEntry(dn,
                        "sAMAccountName: "+accountName,
                        "ObjectClass: " + ActiveDirectoryClient.AD_GROUP_OBJECTCLASS,
                        "groupType: " + ActiveDirectoryClient.AD_GLOBAL_SECURITYGROUP_FLAGS);
                if (description != null && !"".equals(description)) {
                    entry.add("description", description);
                }
                ldapConnection.add(entry);
                groupCreated = (AdGroup) this.getByDn(dn);
                LOG.info("Security group with dn " + dn + " successfully created.");
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for security group creation :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return groupCreated;

    }

    @Override
    public void addEntityToGroup(String entityDn, String groupDn) {
        LOG.info("addEntityToGroup called with entityDn: + " + entityDn + " and groupDn" + groupDn);
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                 Modification addMemberModification = new DefaultModification(
                         ModificationOperation.ADD_ATTRIBUTE,
                         "member",
                          entityDn);

                ldapConnection.modify(groupDn,addMemberModification);
                LOG.info("Entity " + entityDn + " successfully added to group " + groupDn);
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for group modification :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

    }

    @Override
    public void removeEntityFromGroup(String entityDn, String groupDn) {
        LOG.info("removeEntityFromGroup called with entityDn: + " + entityDn + " and groupDn" + groupDn);
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                Modification memberModification = new DefaultModification(
                        ModificationOperation.REMOVE_ATTRIBUTE,
                        "member",
                        entityDn);

                ldapConnection.modify(groupDn,memberModification);
                LOG.info("Entity " + entityDn + " successfully removed from group " + groupDn);
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for group modification :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }
    }

    @Override
    public Set<String> findBySAMAccountName(String sAMAccountName, String searchBase) {
        LOG.info("findBySAMAccountName called with : " +sAMAccountName);
        Set<String> results = new HashSet<>();
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                SearchRequest req = new SearchRequestImpl();
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setBase(new Dn(searchBase));
                req.setFilter( "(sAMAccountName=" + sAMAccountName + "*)" );

            // Process the request
            SearchCursor searchCursor = ldapConnection.search(req);

            while (searchCursor.next()) {
                Response response = searchCursor.get();

                // process the SearchResultEntry
                if (response instanceof SearchResultEntry) {
                    Entry resultEntry = ((SearchResultEntry) response).getEntry();
                    results.add(resultEntry.get("distinguishedName").getString());
                }
            }
            }
            catch(LdapException lde) {
                LOG.error("An error occured while requesting the ldap server.");
                LOG.error("Message from  Server is :" +lde.getLocalizedMessage());
            }
            catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from  Server is :" +ce.getLocalizedMessage());
                }
            finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return results;
    }

    public Set<AbstractAdObject> getObjectsBySid(List<String> sids, String searchBase) {
        Set<AbstractAdObject> results = new HashSet<>();
        for (String sid : sids ) {
            results.add(this.getObjectBySid(sid, searchBase));
        }
        return results;
    }

    @Override
    public AbstractAdObject getObjectBySid(String objectSid, String searchBase) {
        LOG.info("getByObjectSid called with : " + objectSid + " and searchBase: " + searchBase);
        AbstractAdObject result = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                EntryCursor entryCursor = ldapConnection.search(searchBase, "(objectSid=" + objectSid + ")", SearchScope.SUBTREE, "*");
                entryCursor.next();
                Entry resultEntry=entryCursor.get();
                Attribute classes = resultEntry.get("objectClass");
                if (classes.contains(AD_USER_OBJECTCLASS)) result = this.createUserFromUserEntry(resultEntry);
                if (classes.contains(AD_GROUP_OBJECTCLASS)) result = this.createGroupFromGroupEntry(resultEntry);
            }
            catch(LdapException lde) {
                LOG.error("An error occured while requesting the ldap server.");
                LOG.error("Message from  Server is :" +lde.getLocalizedMessage());
            }
            catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from  Server is :" +ce.getLocalizedMessage());
            }
            finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return result;
    }

    private Set<AbstractAdObject> getMembersForDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {
        Set<AbstractAdObject> members = new HashSet<AbstractAdObject>();
        LOG.debug("***Exploring  " + dn);
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                SearchRequest req = new SearchRequestImpl();
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setBase(new Dn(dn));
                req.setFilter("(objectClass=*)");

                SearchCursor searchCursor = ldapConnection.search(req);
                Set<String> membersDn = new HashSet<String>();
                while (searchCursor.next()) {
                    Response response = searchCursor.get();
                    if (response instanceof SearchResultEntry) {
                        Entry resultEntry = ((SearchResultEntry) response).getEntry();
                        Attribute listMember = resultEntry.get("member");
                        if (listMember != null) {
                            for (Value value : listMember) {
                                membersDn.add(value.toString());
                            }
                        }
                    }
                }
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setFilter("(objectClass=*)");

                for (String memberDn : membersDn) {
                    req.setBase(new Dn(memberDn));
                    searchCursor = ldapConnection.search(req);
                    while (searchCursor.next()) {
                        Response response = searchCursor.get();
                        if (response instanceof SearchResultEntry) {
                            Entry resultEntry = ((SearchResultEntry) response).getEntry();
                            Attribute classe = resultEntry.get("objectClass");
                            if (classe != null) {
                                for (Value value : classe) {
                                    if (value.toString().equals(ActiveDirectoryClient.AD_USER_OBJECTCLASS)) {
                                        members.add(this.createUserFromUserEntry(resultEntry));
                                    }
                                    if (value.toString().equals(ActiveDirectoryClient.AD_GROUP_OBJECTCLASS)) {
                                        members.add(this.createGroupFromGroupEntry(resultEntry));
                                        if (recursive) {
                                            if (groupsAlreadyExplored.contains(memberDn)) {
                                                LOG.debug("****Skipping " + memberDn);
                                            } else {
                                                groupsAlreadyExplored.add(memberDn);
                                                members.addAll(getMembersForDN(memberDn, true, groupsAlreadyExplored));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from  Server is :" +ce.getLocalizedMessage());
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server.");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        } catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
        }
        return members;
    }

    private Set<AdGroup> getGroupsForDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {

        Set<AdGroup> groups = new HashSet<AdGroup>();
        Set<String> memberOfs = new HashSet<>();
        LOG.debug("***Exploring  " + dn);

        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            LOG.debug("Successfully got connection from pool");
            try {
                SearchRequest req = new SearchRequestImpl();
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setBase(new Dn(dn));
                req.setFilter("(objectClass=*)");

                // Process the request
                SearchCursor searchCursor = ldapConnection.search(req);
                while (searchCursor.next()) {
                    Response response = searchCursor.get();
                    if (response instanceof SearchResultEntry) {
                        Entry resultEntry = ((SearchResultEntry) response).getEntry();
                        Attribute listMemberOf = resultEntry.get("memberOf");
                        if (listMemberOf != null) {
                            for (Value value : listMemberOf) {
                                memberOfs.add(value.toString());
                            }
                        }
                    }
                }
                for (String memberOf : memberOfs) {
                    req.setBase(new Dn(memberOf));
                    searchCursor = ldapConnection.search(req);
                    while (searchCursor.next()) {
                        Response response = searchCursor.get();
                        if (response instanceof SearchResultEntry) {
                            Entry resultEntry = ((SearchResultEntry) response).getEntry();
                            Attribute classe = resultEntry.get("objectClass");
                            if (classe != null) {
                                for (Value value : classe) {
                                    if (value.toString().equals(ActiveDirectoryClient.AD_GROUP_OBJECTCLASS)) {
                                        groups.add(this.createGroupFromGroupEntry(resultEntry));
                                        if (recursive) {
                                            if (groupsAlreadyExplored.contains(memberOf)) {
                                                LOG.debug("****Skipping " + memberOf);
                                            } else {
                                                groupsAlreadyExplored.add(memberOf);
                                                groups.addAll(getGroupsForDN(memberOf, true, groupsAlreadyExplored));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server.");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } catch (CursorException ce) {
                LOG.error("An error occured while fetching next cursor of LDAP request results.");
                LOG.error("Message from Server is :" +ce.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
                LOG.debug("Successfully released connection to pool");
            }
        } catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
        }

        return groups;
    }

    private AdUser createUserFromUserEntry(Entry userEntry ) throws LdapInvalidAttributeValueException {
        Assert.notNull(userEntry, "Entry userEntry cannot be null");
        Attribute classes = userEntry.get("objectClass");
        Assert.isTrue(classes.contains(AD_USER_OBJECTCLASS),"Given Entry is not a user entry") ;
        AdUser result = new AdUser();
        result.setDistinguishedName(userEntry.get("distinguishedName").getString());
        if (Objects.nonNull(userEntry.get("userPrincipalName"))) result.setUserPrincipalName(userEntry.get("userPrincipalName").getString());
        result.setsAMAccountName(userEntry.get("sAMaccountName").getString());
        if (Objects.nonNull(userEntry.get("cn"))) result.setCommonName(userEntry.get("cn").getString());
        if (Objects.nonNull(userEntry.get("givenName"))) result.setFirstname(userEntry.get("givenName").getString());
        if (Objects.nonNull(userEntry.get("mail"))) result.setMail(userEntry.get("mail").getString());
        if (Objects.nonNull(userEntry.get("telephoneNumber"))) result.setTelephoneNumber(userEntry.get("telephoneNumber").getString());
        if (Objects.nonNull(userEntry.get("department"))) result.setDepartment(userEntry.get("department").getString());
        if (Objects.nonNull(userEntry.get("sn"))) result.setSurname(userEntry.get("sn").getString());
        result.setObjectSid(userEntry.get("objectSid").getBytes());
        return result;
    }

    private AdGroup createGroupFromGroupEntry(Entry groupEntry ) throws LdapInvalidAttributeValueException {
        Assert.notNull(groupEntry, "Entry groupEntry cannot be null");
        Attribute classes = groupEntry.get("objectClass");
        Assert.isTrue(classes.contains(AD_GROUP_OBJECTCLASS),"Given Entry is not a group entry") ;
        AdGroup result = new AdGroup();
        result.setDistinguishedName(groupEntry.get("distinguishedName").getString());
        result.setsAMAccountName(groupEntry.get("sAMaccountName").getString());
        if (Objects.nonNull(groupEntry.get("description"))) result.setDescription(groupEntry.get("description").getString());
        if (Objects.nonNull(groupEntry.get("cn"))) result.setCommonName(groupEntry.get("cn").getString());
        result.setObjectSid(groupEntry.get("objectSid").getBytes());
        return result;
    }


}
