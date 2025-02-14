---
layout: default
title: CAS - JDBC Drivers
---

# JDBC Drivers

While in most cases this is unnecessary and handled by CAS automatically,
you may need to also include the following module to account for various database drivers:

```xml
<dependency>
   <groupId>org.apereo.cas</groupId>
   <artifactId>cas-server-support-jdbc-drivers</artifactId>
   <version>${cas.version}</version>
</dependency>
```

Automatic support for database drivers includes:

1. HSQLDB
2. MySQL
3. PostgreSQL
4. MariaDB
5. Microsoft SQL Server (JTDS)
6. Sybase

All other drivers need to be manually added to the build configuration.
