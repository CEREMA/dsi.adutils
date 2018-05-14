package fr.cerema.dsi.ldap.activedirectory.client;

import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AbstractAdObject;
import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AdGroup;
import fr.cerema.dsi.ldap.activedirectory.client.fr.cerema.dsi.ldap.activedirectory.model.AdUser;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public Set<AdGroup> getGroupsForDN(String dn, boolean recursive) {
        LOG.info("getAllGroupsForDN called with Dn:" + dn);
        Set<AdGroup> groups = new HashSet<AdGroup>();
        Set<String> groupsExplored = new HashSet<>();
        groups.addAll(this.getAllGroupsForDN(dn, recursive, groupsExplored));
        return groups;
    }

    public Set<AdUser> getUsersForDN(String dn, boolean recursive) {
        LOG.info("getAllUsersForDN called with Dn:" + dn);
        Set<AdUser> users = new HashSet<AdUser>();
        Set<String> groups = new HashSet<String>();
        users.addAll(this.getAllUsersForGroupDN(dn, recursive, groups));
        return users;
    }

    @Override
    public String createSecurityGroup(String dn, String samAccountName) {
        LOG.info("createSecurityGroup called with Dn: " + dn + " and samAccountName: " + samAccountName);
        String dnCreated = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            try {
                ldapConnection.add(
                    new DefaultEntry(
                            dn,
                            "sAMAccountName: "+samAccountName,
                            "ObjectClass: " + ActiveDirectoryClient.AD_GROUP_OBJECTCLASS,
                            "groupType: " + ActiveDirectoryClient.AD_GLOBAL_SECURITYGROUP_FLAGS
                    ));
                dnCreated = dn;
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for security group creation :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return dnCreated;

    }

    @Override
    public String addEntityToGroup(String entityDn, String groupDn) {
        LOG.info("addEntityToGroup called with entityDn: + " + entityDn + " and groupDn" + groupDn);
        String entityAdded = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            try {
                 Modification addMemberModification = new DefaultModification(
                         ModificationOperation.ADD_ATTRIBUTE,
                         "member",
                          entityDn);

                ldapConnection.modify(groupDn,addMemberModification);
                entityAdded = entityDn;
            } catch (LdapException lde) {
                LOG.error("An error occured while requesting LDAP Server for group modification :");
                LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
            } finally {
                ldapConnectionPool.releaseConnection(ldapConnection);
            }
        }
        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return entityAdded;
    }

    @Override
    public Set<String> findBySAMAccountName(String sAMAccountName, String searchBase) {
        LOG.info("findBySAMAccountName called with : " +sAMAccountName);
        Set<String> results = new HashSet<>();
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
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
        LOG.info("getByObjectSid called with : " + objectSid);
        AbstractAdObject result = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
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
        }

        catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
            LOG.error("Message from LDAP Server is :" +lde.getLocalizedMessage());
        }

        return result;
    }

    private Set<AdUser> getAllUsersForGroupDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {
        Set<AdUser> users = new HashSet<AdUser>();
        LOG.debug("***Exploring  " + dn);
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            try {
                SearchRequest req = new SearchRequestImpl();
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setBase(new Dn(dn));
                req.setFilter("(objectClass=*)");

                SearchCursor searchCursor = ldapConnection.search(req);
                Set<String> members = new HashSet<String>();
                while (searchCursor.next()) {
                    Response response = searchCursor.get();
                    if (response instanceof SearchResultEntry) {
                        Entry resultEntry = ((SearchResultEntry) response).getEntry();
                        Attribute listMember = resultEntry.get("member");
                        if (listMember != null) {
                            for (Value value : listMember) {
                                members.add(value.toString());
                            }
                        }
                    }
                }
                req.setScope(SearchScope.SUBTREE);
                req.addAttributes("*");
                req.setTimeLimit(0);
                req.setFilter("(objectClass=*)");

                for (String member : members) {
                    req.setBase(new Dn(member));
                    searchCursor = ldapConnection.search(req);
                    while (searchCursor.next()) {
                        Response response = searchCursor.get();
                        if (response instanceof SearchResultEntry) {
                            Entry resultEntry = ((SearchResultEntry) response).getEntry();
                            Attribute classe = resultEntry.get("objectClass");
                            if (classe != null) {
                                for (Value value : classe) {
                                    if (value.toString().equals(ActiveDirectoryClient.AD_USER_OBJECTCLASS)) {
                                        users.add(this.createUserFromUserEntry(resultEntry));
                                    }
                                    if (recursive) {
                                        if (value.toString().equals(ActiveDirectoryClient.AD_GROUP_OBJECTCLASS)) {
                                            if (groupsAlreadyExplored.contains(member)) {
                                                LOG.debug("****Skipping " + member);
                                            } else {
                                                groupsAlreadyExplored.add(member);
                                                users.addAll(getAllUsersForGroupDN(member, true, groupsAlreadyExplored));
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
            }
        } catch (LdapException lde) {
            LOG.error("Cannot get/release LdapConnection from/to pool.");
        }
        return users;
    }

    private Set<AdGroup> getAllGroupsForDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {

        Set<AdGroup> groups = new HashSet<AdGroup>();
        Set<String> memberOfs = new HashSet<>();
        LOG.debug("***Exploring  " + dn);

        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
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
                                                groups.addAll(getAllGroupsForDN(memberOf, true, groupsAlreadyExplored));
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
        if (Objects.nonNull(groupEntry.get("cn"))) result.setCommonName(groupEntry.get("cn").getString());
        result.setObjectSid(groupEntry.get("objectSid").getBytes());
        return result;
    }


}
