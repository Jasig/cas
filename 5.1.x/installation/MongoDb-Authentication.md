---
layout: default
title: CAS - MongoDb Authentication
---

# MongoDb Authentication

Verify and authenticate credentials against a [MongoDb](https://www.mongodb.org/) instance.
Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-mongo</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties,
please [review this guide](Configuration-Properties.html#mongodb-authentication).

Accounts are expected to be found as such in collections:

```json
{
	"username": "casuser",
	"password": "34598dfkjdjk3487jfdkh874395",
	"first_name": "john",
	"last_name": "smith"
}
```
