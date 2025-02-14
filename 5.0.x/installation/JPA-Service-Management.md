---
layout: default
title: CAS - JPA Service Registry
---

# JPA Service Registry
Stores registered service data in a database.

Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jpa-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```
 
To learn how to configure database drivers, [please see this guide](JDBC-Drivers.html).
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Auto Initialization

Upon startup and if the services registry database is blank, 
the registry is able to auto initialize itself from default 
JSON service definitions available to CAS. 

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
