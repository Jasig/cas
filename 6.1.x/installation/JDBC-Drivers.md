---
layout: default
title: CAS - JDBC Drivers
category: Configuration
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
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties-Common.html#hibernate--jdbc).

### HSQLDB

Available drivers are:

1. `org.hsqldb.jdbcDriver`

| Dialects             
|-------------------------------------
| `org.hibernate.dialect.HSQLDialect`    

### Oracle


Available drivers are:

1. `oracle.jdbc.driver.OracleDriver`

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
| `org.hibernate.dialect.MySQL8Dialect`

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
| `org.hibernate.dialect.PostgresPlusDialect`

### MariaDB

Available drivers are:

1. `org.mariadb.jdbc.Driver`

| Dialects             
|------------------------------------------------
| `org.hibernate.dialect.MariaDBDialect`   
| `org.hibernate.dialect.MariaDB53Dialect`   
| `org.hibernate.dialect.MariaDB10Dialect`   
| `org.hibernate.dialect.MariaDB102Dialect`   
| `org.hibernate.dialect.MariaDB103Dialect`   

### Microsoft SQL Server 

Available drivers are:

1. `net.sourceforge.jtds.jdbc.Driver`
2. `com.microsoft.sqlserver.jdbc.SQLServerDriver`

| Dialects             
|------------------------------------------------
| `org.hibernate.dialect.SQLServerDialect`   
| `org.hibernate.dialect.SQLServer2005Dialect`   
| `org.hibernate.dialect.SQLServer2008Dialect`  
| `org.hibernate.dialect.SQLServer2012Dialect`  
