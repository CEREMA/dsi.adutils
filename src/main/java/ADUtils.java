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

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ADUtils {

    public Set<String> getAllGroupsForDN(String Dn) {
        Set<String> groups = new HashSet<String>();
        groups.addAll(this.getAllGroupsForDN(Dn, groups));
        return groups;
    }

    public Set<String> getAllUsersForDN(String Dn) {
        Set<String> users = new HashSet<String>();
        Set<String> groups = new HashSet<String>();
        users.addAll(this.getAllUsersForGroupDN(Dn, groups));
        return users;
    }

    private Set<String> getAllGroupsForDN(String DN, Set<String> groupsAlreadyExplored) {

        Set<String> groups = new HashSet<String>();
        System.out.println("***Exploring  " + DN);

        LdapConnection ldapConnection = new LdapNetworkConnection("172.20.201.241", 389);
        ldapConnection.setTimeOut(0);
        try {
            ldapConnection.bind("CN=SVC_claire,CN=Users,DC=lab,DC=cerema,DC=fr", "=jyq6d$wAi+GS6D");
            if (ldapConnection.isConnected()) {
                //System.out.println("Connecté avec le compte svc_claire");
            }
        } catch (LdapException e) {
            //System.out.println("Connexion avec le compte svc_claire impossible");
        }

        try {
            SearchRequest req = new SearchRequestImpl();
            req.setScope(SearchScope.SUBTREE);
            //req.addAttributes("(distinguishedName=CN=CHARLES Alain (alain.charles),OU=Utilisateurs,OU=Siège,DC=lab,DC=cerema,DC=fr)");
            req.addAttributes("*");
            req.setTimeLimit(0);
            req.setBase(new Dn(DN));
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
                            if (groupsAlreadyExplored.contains(value.toString())) {
                                System.out.println("****Skipping " + DN);
                            }
                            else{
                                groupsAlreadyExplored.add(value.toString());
                                groups.addAll(getAllGroupsForDN(value.toString(), groupsAlreadyExplored));
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche");
        }

        try {
            ldapConnection.unBind();
            if (!ldapConnection.isConnected()) {
                //System.out.println("Déconnexion du compte svc_claire OK");
            }
        } catch (LdapException e) {
            System.out.println("Déconnexion du compte svc_claire Impossible");

        }
        try {
            ldapConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private Set<String> getAllUsersForGroupDN(String DN, Set<String> groupsAlreadyExplored) {

        Set<String> users = new HashSet<String>();
        System.out.println("***Exploring  " + DN);

        LdapConnection ldapConnection = new LdapNetworkConnection("172.20.201.241", 389);
        ldapConnection.setTimeOut(0);
        try {
            ldapConnection.bind("CN=SVC_claire,CN=Users,DC=lab,DC=cerema,DC=fr", "=jyq6d$wAi+GS6D");
            if (ldapConnection.isConnected()) {
                //System.out.println("Connecté avec le compte svc_claire");
            }
        } catch (LdapException e) {
            //System.out.println("Connexion avec le compte svc_claire impossible");
        }

        try {// RECHERCHE DES USERS
            SearchRequest req = new SearchRequestImpl();
            req.setScope(SearchScope.SUBTREE);
            //req.addAttributes("(distinguishedName=CN=CHARLES Alain (alain.charles),OU=Utilisateurs,OU=Siège,DC=lab,DC=cerema,DC=fr)");
            req.addAttributes("*");
            req.setTimeLimit(0);
            req.setBase(new Dn(DN));
            req.setFilter("(objectClass=*)");

            // Process the request
            SearchCursor searchCursor = ldapConnection.search(req);
            Set<String> members = new HashSet<String>();
            while (searchCursor.next()) {
                Response response = searchCursor.get();

                // process the SearchResultEntry
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
            // Si les members sont des users, on les ajoute, sinon si des groupes on par en récursif

            req.setScope(SearchScope.SUBTREE);
            //req.addAttributes("(distinguishedName=CN=CHARLES Alain (alain.charles),OU=Utilisateurs,OU=Siège,DC=lab,DC=cerema,DC=fr)");
            req.addAttributes("*");
            req.setTimeLimit(0);
            req.setFilter("(objectClass=*)");

            for (String dn : members) {
                req.setBase(new Dn(dn));
                searchCursor = ldapConnection.search(req);
                while (searchCursor.next()) {
                    Response response = searchCursor.get();
                    if (response instanceof SearchResultEntry) {
                        Entry resultEntry = ((SearchResultEntry) response).getEntry();
                        Attribute classe = resultEntry.get("objectClass");
                        if (classe != null) {
                            for (Value value : classe) {
                                if (value.toString().equals("user")){
                                    users.add(dn);
                                }
                                if (value.toString().equals("group")) {
                                    if (groupsAlreadyExplored.contains(dn)) {
                                        System.out.println("****Skipping " + DN);
                                    }
                                    else{
                                        groupsAlreadyExplored.add(dn);
                                        users.addAll(getAllUsersForGroupDN(dn, groupsAlreadyExplored));
                                    }
                                }
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche");
        }

        try {
            ldapConnection.unBind();
            if (!ldapConnection.isConnected()) {
                //System.out.println("Déconnexion du compte svc_claire OK");
            }
        } catch (LdapException e) {
            System.out.println("Déconnexion du compte svc_claire Impossible");

        }
        try {
            ldapConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

}