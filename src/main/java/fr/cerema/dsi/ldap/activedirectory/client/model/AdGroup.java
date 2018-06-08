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

package fr.cerema.dsi.ldap.activedirectory.client.model;

/**
 * Class whose instances are concrete representation of an Active Directory group
 * <p>
 * This class holds useful attributes of active directory groups
 * @author alain.charles
 */
public class AdGroup extends AbstractAdObject{

    private String description;

    /**
     * Returns the group's description
     * @return the description of the group
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the group's description
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
