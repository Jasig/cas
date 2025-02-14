---
layout: default
title: CAS - Mongo Service Registry
---

# Mongo Service Registry

This registry uses a [MongoDb](https://www.mongodb.org/) instance to load and persist service definitions.
Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-mongo-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

This implementation auto-configures most of the internal details.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Auto Initialization

Upon startup and if the services registry database is blank, 
the registry is able to auto initialize itself from default 
JSON service definitions available to CAS. 

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

