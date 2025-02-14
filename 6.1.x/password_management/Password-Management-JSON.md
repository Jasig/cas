---
layout: default
title: CAS - Password Management
category: Password Management
---

# Password Management - JSON

Accounts and password may be stored inside a static modest JSON resource, whose location is taught to CAS via settings.
This option is most useful during development, testing and demos and is not suitable for production.

The outline of the JSON file may match the following:

```json
{
  "casuser" : {
    "email" : "casuser@example.org",
    "password" : "p@ssw0rd",
    "phone" : "1234567890",
    "securityQuestions" : {
      "question1" : "answer1",
      "question2" : "answer2"
    }
  }
}
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#json-password-management).
