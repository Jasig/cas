---
layout: default
title: CAS - JPA Service Registry
---

# JPA Service Registry
Stores registered service data in a database.

Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jpa-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

The following settings are expected:

```properties
# svcreg.database.ddl.auto=create-drop
# svcreg.database.dialect=org.hibernate.dialect.OracleDialect|MySQLInnoDBDialect|HSQLDialect
# svcreg.database.batchSize=10
# svcreg.database.driverClass=org.hsqldb.jdbcDriver
# svcreg.database.url=jdbc:hsqldb:mem:cas-ticket-registry
# svcreg.database.user=sa
# svcreg.database.password=
# svcreg.database.pool.minSize=6
# svcreg.database.pool.maxSize=18
# svcreg.database.pool.maxWait=10000
# svcreg.database.pool.maxIdleTime=120
# svcreg.database.pool.acquireIncrement=6
# svcreg.database.pool.idleConnectionTestPeriod=30
# svcreg.database.pool.connectionHealthQuery=select 1
# svcreg.database.pool.acquireRetryAttempts=5
# svcreg.database.pool.acquireRetryDelay=2000
# svcreg.database.pool.connectionHealthQuery=select 1
```


## Configuration

The following configuration template may be applied to `deployerConfigContext.xml` to provide for persistent
registered service storage. The configuration assumes a `dataSource` bean is defined in the context.

```xml
<alias name="jpaServiceRegistryDao" alias="serviceRegistryDao" />
```
