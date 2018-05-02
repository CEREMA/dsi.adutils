import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main {

    Set<String> groups = new HashSet<String>();
    public static void main(String [ ] args) {

        /*LdapConnection ldapConnection = new LdapNetworkConnection("172.20.201.241", 389);
        ldapConnection.setTimeOut(0);
        try {
            ldapConnection.bind( "CN=SVC_claire,CN=Users,DC=lab,DC=cerema,DC=fr", "=jyq6d$wAi+GS6D" );
            if (ldapConnection.isConnected()) {
                System.out.println("Connecté avec le compte svc_claire");
            }
        } catch (LdapException e) {
            System.out.println("Connexion avec le compte svc_claire impossible");
        }*/

        //---------------------------RECHERCHE
        // Create the SearchRequest object

        /*try {
            SearchRequest req = new SearchRequestImpl();
            req.setScope(SearchScope.SUBTREE);
            req.addAttributes("*");
            req.setTimeLimit(0);
            req.setBase(new Dn("OU=Claire,OU=Transversal,DC=lab,DC=cerema,DC=fr"));
            req.setFilter( "(objectSid=S-1-5-21-1779429759-2771315062-123184451-10858)" );

            // Process the request
            SearchCursor searchCursor = ldapConnection.search(req);

            while (searchCursor.next()) {
                Response response = searchCursor.get();

                // process the SearchResultEntry
                if (response instanceof SearchResultEntry) {
                    Entry resultEntry = ((SearchResultEntry) response).getEntry();
                    System.out.println(resultEntry.get("distinguishedName"));
                }
            }
        }
        catch(Exception e) {
            System.out.println("Erreur lors de la recherche");
        }*/



       /** try {
            ldapConnection.add(
                    new DefaultEntry(
                            "CN=ACL_TESTACH_Added_by_Java_RW,OU=Claire,OU=Transversal,DC=lab,DC=cerema,DC=fr", // The Dn
                            "sAMAccountName: ACL_TESTACH_Added_by_Java_RW",
                            "ObjectClass: group",
                            "groupType: -2147483646"
                    ));
        }
        catch ( Exception e) {
            System.out.println("Erreur");
        }**/

       /*try {
            Modification addedGroupForUser = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, "member",
                    "CN=CHARLES Alain (alain.charles),OU=Utilisateurs,OU=Siège,DC=lab,DC=cerema,DC=fr");

            ldapConnection.modify("CN=ACL_TESTACH_Added_by_Java_RW,OU=Claire,OU=Transversal,DC=lab,DC=cerema,DC=fr",addedGroupForUser);
        }
        catch ( Exception e) {
            System.out.println("Erreur");
        }*/

        /*try {
            Modification addedGroupForGroup = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, "member",
                    "CN=auto.Siège.DSI,OU=Auto,OU=Groupes,OU=Transversal,DC=lab,DC=cerema,DC=fr");

            ldapConnection.modify("CN=ACL_TESTACH_Added_by_Java_RW,OU=Claire,OU=Transversal,DC=lab,DC=cerema,DC=fr",addedGroupForGroup);
        }
        catch ( Exception e) {
            System.out.println("Erreur");
        }


        try {
            ldapConnection.unBind();
            if (!ldapConnection.isConnected()) {
                System.out.println("Déconnexion du compte svc_claire OK");
            }
        } catch (LdapException e) {
            System.out.println("Déconnexion du compte svc_claire Impossible");;
        }
        try {
            ldapConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    ADUtils utils = new ADUtils();
    /*
    System.out.println ("LAncement de la recherche de tous les groupes de denoyelle...");
    Long t1 = System.currentTimeMillis();
    Set<String> groups = utils.getAllGroupsForDN("CN=N1 DENOYELLE Arthur,OU=Administrateurs,DC=lab,DC=cerema,DC=fr");
    Long t2 = System.currentTimeMillis() - t1;
    for (String s: groups) {
        System.out.println(s);
    }
    System.out.println( "Requete réalisée en " + t2 + " ms");*/

        System.out.println ("Lancement de la recherche de tous agents du groupe CN=Administrateurs,CN=Builtin,DC=lab,DC=cerema,DC=fr");
        Long t1 = System.currentTimeMillis();
        Set<String> users = utils.getAllUsersForDN("CN=Administrateurs,CN=Builtin,DC=lab,DC=cerema,DC=fr");
        Long t2 = System.currentTimeMillis() - t1;
        System.out.println("Resultats : ");
        System.out.println("----------- ");
        for (String s: users) {
            System.out.println(s);
        }
        System.out.println("----------- ");
        System.out.println( "Requete réalisée en " + t2 + " ms");
    }





}
