---
layout: default
title: CAS - Configuring Ticketing Components
---

# Ticketing

There are two core configurable ticketing components:

* `TicketRegistry` - Provides for durable ticket storage.
* `ExpirationPolicy` - Provides a policy framework for ticket expiration semantics.

## Ticket Registry

The deployment environment and technology expertise generally determine the particular `TicketRegistry` component.
A cache-backed implementation is recommended for HA deployments, while the default
`DefaultTicketRegistry` in-memory component may be suitable for small deployments.

### Default (In-Memory) Ticket Registry

The default registry uses a memory-backed internal map for ticket storage and retrieval.
This component does not preserve ticket state across restarts and is not a suitable solution
for clustered CAS environments that are deployed in active/active mode.

### Cache-Based Ticket Registries

Cached-based ticket registries provide a high-performance solution for ticket storage in high availability
deployments. Components for the following caching technologies are provided:

* [Hazelcast](Hazelcast-Ticket-Registry.html)
* [Ehcache](Ehcache-Ticket-Registry.html)
* [Ignite](Ignite-Ticket-Registry.html)
* [Memcached](Memcached-Ticket-Registry.html)
* [Infinispan](Infinispan-Ticket-Registry.html)

### RDBMS Ticket Registries

RDBMS-based ticket registries provide a distributed ticket store across multiple CAS nodes. 
Components for the following caching technologies are provided:

* [JPA](JPA-Ticket-Registry.html)

### NoSQL Ticket Registries

CAS also provides support for a variety of other databases, including Redis, MongoDb and Apache 
Cassandra, for ticket storage and persistence:

* [Infinispan](Infinispan-Ticket-Registry.html)
* [Couchbase](Couchbase-Ticket-Registry.html)

### Secure Cache Replication

A number of cache-based ticket registries support secure replication of ticket data across the wire,
so that tickets are encrypted and signed on replication attempts to prevent sniffing and eavesdrops.
[See this guide](Ticket-Registry-Replication-Encryption.html) for more info.


## Ticket Expiration Policies

CAS supports a pluggable and extensible policy framework to control the expiration policy of 
ticket-granting tickets (TGT) and service tickets (ST). 
[See this guide](Configuring-Ticket-Expiration-Policy.html) for details on how to configure the expiration policies.
