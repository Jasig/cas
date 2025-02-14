---
layout: default
title: CAS - Cassandra Ticket Registry
category: Ticketing
---

# Cassandra Ticket Registry

Cassandra integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-cassandra-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

This registry stores tickets in [Apache Cassandra](http://cassandra.apache.org/) instances. Tickets are expected to be found/stored in a `castickets` table
with a default write consistency of `LOCAL_QUORUM` and read consistency of `ONE`.

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="com.datastax.driver" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#cassandra-ticket-registry).
