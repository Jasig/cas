---
layout: default
title: CAS - Redis Authentication
---

# Redis Authentication

Verify and authenticate credentials using [Redis](https://redis.io/).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-redis-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

User accounts are mapped to a `username` field as the key. The user account record would contain the following fields:

| Field                     | Description    
|---------------------------|----------------------------------------------------------------------------
| `password`           | User password with applicable encoding, if any.
| `status`             | One of `OK`, `LOCKED`, `DISABLED`, `EXPIRED`, `MUST_CHANGE_PASSWORD`.
| `attributes`         | User attributes modeled as `Map<String, List<Object>>`.

To see the relevant list of CAS properties,
please [review this guide](../configuration/Configuration-Properties.html#redis-authentication).

## Redis Principal Attributes

The above dependency may also be used, in the event that principal attributes need to be fetched from a 
Redis database without necessarily authenticating credentials against Redis. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#redis).
