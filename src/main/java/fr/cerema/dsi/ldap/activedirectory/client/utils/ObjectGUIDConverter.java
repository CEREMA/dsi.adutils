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

public class ObjectGUIDConverter {

    private static String AddLeadingZero(int k) {
        return (k<0xF)?"0" + Integer.toHexString(k):Integer.toHexString(k);
    }

    public static String getObjectGUIDAsString(byte[] GUID) {
        String strGUID = "{";
        strGUID = "{";
        strGUID = strGUID + AddLeadingZero((int)GUID[3] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[2] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[1] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[0] & 0xFF);
        strGUID = strGUID + "-";
        strGUID = strGUID + AddLeadingZero((int)GUID[5] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[4] & 0xFF);
        strGUID = strGUID + "-";
        strGUID = strGUID + AddLeadingZero((int)GUID[7] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[6] & 0xFF);
        strGUID = strGUID + "-";
        strGUID = strGUID + AddLeadingZero((int)GUID[8] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[9] & 0xFF);
        strGUID = strGUID + "-";
        strGUID = strGUID + AddLeadingZero((int)GUID[10] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[11] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[12] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[13] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[14] & 0xFF);
        strGUID = strGUID + AddLeadingZero((int)GUID[15] & 0xFF);
        strGUID = strGUID + "}";
        return strGUID.toUpperCase();
    }

}
