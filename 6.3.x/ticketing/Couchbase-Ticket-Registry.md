---
layout: default
title: CAS - Couchbase Ticket Registry
category: Ticketing
---

# Couchbase Ticket Registry

Couchbase integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-couchbase-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```


[Couchbase](http://www.couchbase.com) is a highly available, open source NoSQL database server based on
[Erlang/OTP](http://www.erlang.org) and its mnesia database. The intention of this
registry is to leverage the capability of Couchbase server to provide high availability to CAS.

<div class="alert alert-info"><strong>Compatibility</strong><p>Couchbase support in CAS at the moment is limited to Couchbase v4.</p></div>

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#couchbase-ticket-registry).

The Couchbase integration currently assumes that the ticket registries are stored
in their own buckets. You may optionally set passwords for the buckets and optionally configure
redundancy and replication as per normal Couchbase configuration.

The only truly mandatory setting is the list of nodes.
The other settings are optional, but this is designed to store data in buckets
so in reality the bucket property must also be set.

## Expiration Policy

You will need to remember that every document in Couchbase contains the `expiry` property.
An expiration time-to-live value of `0` means that no expiration is set at all.
The expiration time starts when the document has been successfully stored on the server,
not when the document was created on the CAS server. In practice, the delta should be very very negligible.
Any expiration time larger than `30` days in seconds is considered absolute (as in a Unix time stamp)
and anything smaller is considered relative in seconds.

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following
levels:

```xml
...
<Logger name="com.couchbase" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
...
```
