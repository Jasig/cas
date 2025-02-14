---
layout: default
title: CAS - CouchDB Service Registry
category: Services
---

# CouchDB Service Registry
CouchDB integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-couchdb-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

[CouchDB](http://couchdb.apache.org/) is a highly available, open source NoSQL database server based on
[Erlang/OTP](http://www.erlang.org) and its mnesia database. The intention of this registry is to leverage the capability of CouchDB
server to provide high availability to CAS across multiple data centers.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#couchdb-service-registry).

The only truly mandatory setting is the URL. However, CouchDB should not be used in admin party mode in production, so username and password are needed as well.

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.


## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following
levels:

```xml
...
<AsyncLogger name="org.apache.couchdb" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
