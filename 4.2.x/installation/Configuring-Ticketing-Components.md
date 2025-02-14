---
layout: default
title: CAS - Configuring Ticketing Components
---

# Configuring Ticketing Components
There are two core configurable ticketing components:

* `TicketRegistry` - Provides for durable ticket storage.
* `ExpirationPolicy` - Provides a policy framework for ticket expiration semantics.

## Ticket Registry
The deployment environment and technology expertise generally determine the particular `TicketRegistry` component.
A cache-backed implementation is recommended for HA deployments, while the default
`DefaultTicketRegistry` in-memory component may be suitable for small deployments.

### Default (In-Memory) Ticket Registry
`DefaultTicketRegistry` uses a `ConcurrentHashMap` for memory-backed ticket storage and retrieval.
This component does not preserve ticket state across restarts.

### Cache-Based Ticket Registries
Cached-based ticket registries provide a high-performance solution for ticket storage in high availability
deployments. Components for the following caching technologies are provided:

* [Hazelcast](Hazelcast-Ticket-Registry.html)
* [Ehcache](Ehcache-Ticket-Registry.html)
* [Ignite](Ignite-Ticket-Registry.html)
* [Memcached](Memcached-Ticket-Registry.html)

#### Secure Cache Replication
A number of cache-based ticket registries support secure replication of ticket data across the wire,
so that tickets are encrypted and signed on replication attempts to prevent sniffing and eavesdrops.
[See this guide](Ticket-Registry-Replication-Encryption.html) for more info.

### RDBMS Ticket Registries
RDBMS-based ticket registries provide a distributed ticket store across multiple CAS nodes. Components for the following caching technologies are provided:

* [JPA](JPA-Ticket-Registry.html)

### Ticket Generators
CAS presents a pluggable architecture for generating unique ticket ids for each ticket type.

```properties
# lt.ticket.maxlength=20
# st.ticket.maxlength=20
# tgt.ticket.maxlength=50
# pgt.ticket.maxlength=50
```

## Ticket Expiration Policies
CAS supports a pluggable and extensible policy framework to control the expiration policy of ticket-granting tickets (TGT) and service tickets (ST). [See this guide](Configuring-Ticket-Expiration-Policy.html) for details on how to configure the expiration policies.
