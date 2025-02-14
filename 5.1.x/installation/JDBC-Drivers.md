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

## Database Support

Automatic support for drivers includes the following databases.
All other drivers need to be manually added to the build configuration.
To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#hibernate--jdbc).

### HSQLDB

Available drivers are:

1. `org.hsqldb.jdbcDriver`

| Dialects             
|-------------------------------------
| `org.hibernate.dialect.HSQLDialect`    

### Oracle

Note that the Oracle database driver needs to
be [manually installed](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html)
before the above configuration can take effect. Depending on the driver version, the actual name
of the driver class may vary.

| Dialects             
|-------------------------------------
| `org.hibernate.dialect.Oracle8iDialect`    
| `org.hibernate.dialect.Oracle9iDialect`    
| `org.hibernate.dialect.Oracle10gDialect`    
| `org.hibernate.dialect.Oracle12cDialect`    

### MYSQL

Available drivers are:

1. `com.mysql.jdbc.Driver`
2. `com.mysql.cj.jdbc.Driver`

| Dialects             
|-------------------------------------------------
| `org.hibernate.dialect.MySQLDialect`   
| `org.hibernate.dialect.MySQL5Dialect`   
| `org.hibernate.dialect.MySQLInnoDBDialect`   
| `org.hibernate.dialect.MySQLMyISAMDialect`   
| `org.hibernate.dialect.MySQL5InnoDBDialect`   
| `org.hibernate.dialect.MySQL57InnoDBDialect`  

### PostgreSQL

Available drivers are:

1. `org.postgresql.Driver`

| Dialects             
|------------------------------------------------
| `org.hibernate.dialect.PostgreSQL81Dialect`   
| `org.hibernate.dialect.PostgreSQL82Dialect`   
| `org.hibernate.dialect.PostgreSQL9Dialect`   
| `org.hibernate.dialect.PostgreSQL91Dialect`   
| `org.hibernate.dialect.PostgreSQL92Dialect`   
| `org.hibernate.dialect.PostgreSQL93Dialect`   
| `org.hibernate.dialect.PostgreSQL94Dialect`   
| `org.hibernate.dialect.PostgreSQL95Dialect`   

### MariaDB

Available drivers are:

1. `org.mariadb.jdbc.Driver`

| Dialects             
|------------------------------------------------
| `org.hibernate.dialect.MariaDBDialect`   
| `org.hibernate.dialect.MariaDBDialect`   

### Microsoft SQL Server (JTDS)

Available drivers are:

1. `net.sourceforge.jtds.jdbc.Driver`

| Dialects             
|------------------------------------------------
| `org.hibernate.dialect.SQLServerDialect`   
| `org.hibernate.dialect.SQLServer2005Dialect`   
| `org.hibernate.dialect.SQLServer2008Dialect`  
| `org.hibernate.dialect.SQLServer2012Dialect`  
