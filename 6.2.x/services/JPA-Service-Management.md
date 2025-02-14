---
layout: default
title: CAS - JPA Service Registry
category: Services
---

# JPA Service Registry
Stores registered service data in a database.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jpa-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To learn how to configure database drivers, [please see this guide](../installation/JDBC-Drivers.html).
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#database-service-registry).

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.