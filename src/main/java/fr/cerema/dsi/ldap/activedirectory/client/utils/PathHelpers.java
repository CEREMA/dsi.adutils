/*
 * Copyright (c) 2018 - Alain CHARLES
 *
 *  Licensed under the CeCILL Version 2.0 License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.cecill.info/licences/Licence_CeCILL_V2-fr.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package fr.cerema.dsi.ldap.activedirectory.client.utils;

import fr.cerema.dsi.ldap.activedirectory.client.exceptions.ActiveDirectoryClientException;
import fr.cerema.dsi.ldap.activedirectory.client.exceptions.ActiveDirectoryClientInvalidDnException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;

public class PathHelpers {

    public static String getUoPath(String dn) throws ActiveDirectoryClientException {
        String uoPATH = "";
        try {
            Dn name = new Dn(dn);
            for (Rdn rdn : name.getRdns()) {
                if ("OU".equals(rdn.getType())) {
                    uoPATH = rdn.getValue() + "/" + uoPATH;
                }
            }
            uoPATH = uoPATH.substring(0, uoPATH.length() - 1);
        } catch (LdapInvalidDnException e) {
            throw new ActiveDirectoryClientInvalidDnException("Cannot create Organizational unit from entry");
        }
        return uoPATH;
    }

    public static String createDnFromPath(String path) throws ActiveDirectoryClientException {
        String dn = "OU=Structure Amande, DC=lab, DC=Cerema, DC=fr";
        String[] ous = path.split("/");
        for (String ou : ous) {
            dn = "OU=" + ou + ", " + dn;
        }
        return dn;
    }
}