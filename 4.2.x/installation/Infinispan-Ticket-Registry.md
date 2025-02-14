---
layout: default
title: CAS - Infinispan Ticket Registry
---

# Infinispan Ticket Registry
Infinispan integration is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-integration-infinispan</artifactId>
     <version>${cas.version}</version>
</dependency>
```


[Infinispan](http://infinispan.org/) is a distributed in-memory key/value data store with optional schema.
It can be used both as an embedded Java library and as a language-independent service accessed remotely over a variety of protocols. 
It offers advanced functionality such as transactions, events, querying and distributed processing.

Cache instance can be integrated with

- JCache (JSR-107)
- Hibernate second-level Cache
- WildFly modules
- Apache Lucene directory backed by Infinispan
- Directory Provider for Hibernate Search
- Spring Cache 3.x and 4.x
- CDI
- OSGi
- Apache Spark
- Apache Hadoop

There are a variety of cache stores available to choose from, some of which are:

- JPA/JDBC Store
- Single File & Soft-Index 
- REST 
- Cassandra
- Redis
- HBase
- MongoDB

See the [full list of implementations](http://infinispan.org/cache-store-implementations/).

## Distributed Cache
Enable the registry via:

```xml
<alias name="infinispanTicketRegistry" alias="ticketRegistry" />
```

The above ticket registry expects a `infinispanTicketsCache` element to be available in the Spring context.
This cache element may be chosen from one of the above implementations and configured.
Here is an example:

```xml
<bean id="infinispanTicketsCache" factory-bean="cacheManager" factory-method="getCache" />
<bean id="cacheManager" class="org.infinispan.manager.DefaultCacheManager"
          c:configurationFile="infinispan.xml"/>
```

...and a sample `infinispan.xml` configuration file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<infinispan xsi:schemaLocation="urn:infinispan:config:7.2 http://www.infinispan.org/schemas/infinispan-config-7.2.xsd"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:infinispan:config:7.2">

   <cache-container default-cache="cas">
       <jmx duplicate-domains="true" />
       <local-cache name="cas" />
   </cache-container>
</infinispan>
```

Refer to the [Infinispan](http://infinispan.org/) documentation to learn more about cache configuration, and how
to manage the eviction policy for various ticket types.
