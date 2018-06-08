# dsi.adutils
API for Active Directory manipulation without any knowledge in writing ldap request (Spring integration)

This project aims at manipulating an active directory without any precise knowledge of ldap requests.
We had to develop these utilities as part of a private Cerema project, but we thought is could be useful to publish them because other developers in the world may have to manipulate active directories in their organization

### Components used
The active directory is based upon the Apache Directory API (Apache 2 license)
https://directory.apache.org/api/

It is fully integrated with spring 5.
To instantiate the Active Directory Client, you only need to declare @EnableActiveDirectoryClient in a @Configuration spring component extending ActiveDirectoryClientConfigurer and to override the configureLdapConnection method of the interface.
```java
@Configuration
@EnableActiveDirectoryClient
public class ActiveDirectoryClientConfig implements ActiveDirectoryClientConfigurer {

    @Override
    public void configureLdapConnection(LdapConnectionConfigurer ldapConnectionConfigurer) {
        ldapConnectionConfigurer.configureLdapHost("ActiveDirectoryLdapServer")'
                .configureLdapPort(389)
                .configureDn("CN=yourCn,CN=Users,DC=youOrganization,DC=org")
                .configureCredentials("password");
    }
}
```
The ActiveDirectoryClient bean can then be used in any component like this :
```java
    @Autowired
    private ActiveDirectoryClient activeDirectoryClient;
```
### Features
The API offers to find users and groups recursively, can create and delete entities, add entities to others, remove entities from others, and manipulates object Sids
For a full description, clone the repository, launch the maven build and generate the javadoc.

The active directory client instantiates a pool of 8 connexions to the declared AD server
You cannot change the size of the connection pool, nor every parameter of the pool. Would be very easy to develop, but no time for that now.

### License
This code is Licensed under the CeCILL Version 2.0 License.
You may not use this code except in compliance with the License.
You may obtain a copy of the License at

http://www.cecill.info/licences/Licence_CeCILL_V2-fr.html
          
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License
