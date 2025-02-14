---
layout: default
title: CAS - JPA Ticket Registry
---


# JPA Ticket Registry
The JPA Ticket Registry allows CAS to store client authenticated state
data (tickets) in a database back-end such as MySQL.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Using a RDBMS as
the back-end persistence choice for Ticket Registry state management is a fairly unnecessary and complicated
process. Ticket registries generally do not need the durability that comes with RDBMS and unless
you are already outfitted with clustered RDBMS technology and the resources to manage it,
the complexity is likely not worth the trouble. Given the proliferation of hardware virtualization
and the redundancy and vertical scaling they often provide, more suitable recommendation would be
the default in-memory ticket registry for a single node CAS deployment and distributed cache-based
registries for higher availability.</p></div>

Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jpa-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```


## Configuration

```xml
<alias name="jpaTicketRegistry" alias="ticketRegistry" />
```

The following settings are expected:

```properties
# ticketreg.database.ddl.auto=create-drop
# ticketreg.database.dialect=org.hibernate.dialect.OracleDialect|MySQLInnoDBDialect|HSQLDialect
# ticketreg.database.batchSize=10
# ticketreg.database.driverClass=org.hsqldb.jdbcDriver
# ticketreg.database.url=jdbc:hsqldb:mem:cas-ticket-registry
# ticketreg.database.user=sa
# ticketreg.database.password=
# ticketreg.database.pool.minSize=6
# ticketreg.database.pool.maxSize=18
# ticketreg.database.pool.maxWait=10000
# ticketreg.database.pool.maxIdleTime=120
# ticketreg.database.pool.acquireIncrement=6
# ticketreg.database.pool.idleConnectionTestPeriod=30
# ticketreg.database.pool.connectionHealthQuery=select 1
# ticketreg.database.pool.acquireRetryAttempts=5
# ticketreg.database.pool.acquireRetryDelay=2000
# ticketreg.database.pool.connectionHealthQuery=select 1
```

