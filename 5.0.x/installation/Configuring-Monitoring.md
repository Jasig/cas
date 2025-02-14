---
layout: default
title: CAS - Monitoring
---

# CAS Monitoring

CAS monitors may be defined to report back the health status of the ticket registry
and other underlying connections to systems that are in use by CAS.

## Default

The default monitors report back brief memory and ticket stats. There is nothing more for you to do. 

## Memcached

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```


## Ehcache

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ehcache-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).


## Hazelcast

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-hazelcast-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).


## JDBC

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jdbc-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).


## LDAP

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ldap-monitor</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
