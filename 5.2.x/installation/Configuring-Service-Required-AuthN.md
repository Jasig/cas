---
layout: default
title: CAS - Configuring Service Required Authentication
---

# Configure Service Required Authentication

Each registered application in the registry may be assigned a set of identifiers/names for the required authentication handlers available and configured in CAS.  These names can be used to enforce a service definition to only use the authentication strategy carrying that name when  an authentication request is submitted to CAS. While authentication methods in CAS all are given a default name, most if not all  methods can be assigned a name via CAS settings.

As an example, if there are two authentication strategies defined in CAS where one is noted by the name `LdapAuthenticationHandler` and `DatabaseAuthenticationHandler`, the following service definition should ensure that only the `DatabaseAuthenticationHandler` is used to verify credentials for authentication requests that appropriately match the defined pattern:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "https://app.example.org/.+",
  "name" : "ExampleApp",
  "id" : 1,
  "requiredHandlers" : [ "java.util.HashSet", [ "DatabaseAuthenticationHandler" ] ]
}
```
