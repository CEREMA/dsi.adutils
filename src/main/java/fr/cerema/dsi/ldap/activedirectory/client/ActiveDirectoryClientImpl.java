package fr.cerema.dsi.ldap.activedirectory.client;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
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

    public Set<String> getGroupsForDN(String dn, boolean recursive) {
        LOG.info("getAllGroupsForDN called with Dn:" + dn);
        Set<String> groups = new HashSet<String>();
        groups.addAll(this.getAllGroupsForDN(dn, recursive, groups));
        return groups;
    }

    public Set<String> getUsersForDN(String dn, boolean recursive) {
        LOG.info("getAllUsersForDN called with Dn:" + dn);
        Set<String> users = new HashSet<String>();
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

    @Override
    public Entry getByObjectSid(String objectSID, String searchBase) {
        LOG.info("getByObjectSid called with : " + objectSID);
        Entry resultEntry = null;
        try {
            LdapConnection ldapConnection = ldapConnectionPool.getConnection();
            try {

                // Process the request
                EntryCursor entryCursor = ldapConnection.search(searchBase, "(objectSID=" + objectSID + ")", SearchScope.SUBTREE, "*");
                entryCursor.next();
                resultEntry=entryCursor.get();
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

        return resultEntry;
    }

    private Set<String> getAllUsersForGroupDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {
        Set<String> users = new HashSet<String>();
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
                //req.addAttributes("(distinguishedName=CN=CHARLES Alain (alain.charles),OU=Utilisateurs,OU=Siège,DC=lab,DC=cerema,DC=fr)");
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
                                        users.add(member);
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

    private Set<String> getAllGroupsForDN(String dn, boolean recursive, Set<String> groupsAlreadyExplored) {

        Set<String> groups = new HashSet<String>();
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

                    // process the SearchResultEntry
                    if (response instanceof SearchResultEntry) {
                        Entry resultEntry = ((SearchResultEntry) response).getEntry();
                        Attribute listMemberOf = resultEntry.get("memberOf");
                        if (listMemberOf != null) {
                            for (Value value : listMemberOf) {
                                groups.add(value.toString()); // On s'en fout on le met même s'il y est déjà
                                if (recursive) {
                                    if (groupsAlreadyExplored.contains(value.toString())) {
                                        LOG.debug("****Skipping " + dn);
                                    } else {
                                        groupsAlreadyExplored.add(value.toString());
                                        groups.addAll(getAllGroupsForDN(value.toString(), true, groupsAlreadyExplored));
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

}
