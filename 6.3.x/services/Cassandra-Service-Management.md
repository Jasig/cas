---
layout: default
title: CAS - Cassandra Service Registry
category: Services
---

# Cassandra Service Registry

Stores registered service data in [Apache Cassandra](http://cassandra.apache.org/) instances. Services are expected to be found/stored in a `casservices` table with a default write consistency of `LOCAL_QUORUM` and read consistency of `ONE`.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-cassandra-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>                                                            
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#cassandra-service-registry).

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<Logger name="com.datastax.driver" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
...
```

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.
