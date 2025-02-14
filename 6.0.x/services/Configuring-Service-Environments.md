---
layout: default
title: CAS - Configuring Service Environments
category: Services
---

# Configure Service Environments

Each registered application in the registry may be assigned a set of environment names. The environment names act as a filter, allowing
CAS to only load and honor the registered service definition if the runtime environment does in fact match the registered service environment. This allows one to register multiple versions of the same application many times with CAS where each version may only be relevant in a particular runtime profile. Environments can be activated in CAS using the `spring.profiles.active` property specified as an environment variable or command-line flag, etc.

For example, the below service definition is only recognized and loaded by CAS if the runtime environment profile is one of `production` or `pre-production`:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "https://app.example.org/.+",
  "name" : "ExampleApp",
  "id" : 1,
  "environments" : [ "java.util.HashSet", [ "production", "pre-production" ] ]
}
```

Note that a registered service definition without an assigned environment will be loaded regardless of the runtime profile. Similarly, all 
service definitions are considered eligible if CAS is set to run without an active profile.
