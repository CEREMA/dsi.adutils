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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizationalUnit extends AbstractAdObject{


    private String path;

    private String descriptionPath;

    private String description;

    private List<OrganizationalUnit> organizationalUnits = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationalUnit that = (OrganizationalUnit) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(path);
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }

    public String getDescription() {
        if (description != null) {
            return description;
        }
        else {
            return path.substring(path.lastIndexOf('/')+1, path.length());
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationalUnit> getOrganizationalUnits() {
        return organizationalUnits;
    }

    public void setOrganizationalUnits(List<OrganizationalUnit> organizationalUnits) {
        this.organizationalUnits = organizationalUnits;
    }
}
