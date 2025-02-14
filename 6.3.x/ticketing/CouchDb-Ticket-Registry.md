---
layout: default
title: CAS - CouchDB Ticket Registry
category: Ticketing
---

# CouchDB Ticket Registry

CouchDB integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-couchdb-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```


[CouchDB](http://couchdb.apache.org) is a highly available, open source NoSQL database server based on
[Erlang/OTP](http://www.erlang.org) and its mnesia database. The intention of this
registry is to leverage the multi-master, multi-datacenter capabilities of CouchDB server to provide high availability to CAS.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#couchdb-ticket-registry).


The only truly mandatory setting is the URL. However, CouchDB should not be used in admin party mode in production, so username and password are needed as well.

## Caveat

The trade off for multi-master replication across multiple datacenters if CouchDB does not fully delete
records. Depending on deployment scale, usage, and available storage, the database may need regular cleaning
through normal CouchDB techniques.

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following
levels:

```xml
...
<Logger name="org.apache.couchdb" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
...
```
