---
layout: default
title: CAS - Configuring Service Contacts
---

# Configure Service Contacts

CAS has ability to assign contact information to a service definition. These are individual and/or entities that can be classified as owners of the application that may be notified in case changes are applied to the service definition.

A sample JSON file follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "sample service",
  "id" : 100,
  "contacts": [
    "java.util.ArrayList", [{
        "@class": "org.apereo.cas.services.DefaultRegisteredServiceContact",
        "name": "John Smith",
        "email": "jsmith@example.org",
        "phone": "123-456-7890",
        "department": "IT"
      }
    ]
  ]
}
```
