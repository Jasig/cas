---
layout: default
title: CAS - Mongo Service Registry
---

# Mongo Service Registry
This DAO uses a [MongoDb](https://www.mongodb.org/) instance to load and persist service definitions.
Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration
This implementation auto-configures most of the internal details.

Enable the registry in `cas.properties` via:

```properties
#CAS components mappings
serviceRegistryDao=mongoServiceRegistryDao
```


The following configuration in `application.properties` is required.

```properties
mongodb.host=mongodb database url
mongodb.port=mongodb database port
mongodb.userId=mongodb userid to bind
mongodb.userPassword=mongodb password to bind
cas.service.registry.mongo.db=Collection name to store service definitions
```
