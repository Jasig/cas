---
layout: default
title: CAS - Default Ticket Registry
category: Ticketing
---

# Default Ticket Registry

The default registry uses a memory-backed internal concurrent map for ticket storage and retrieval, though there is also the option to use an implementation that is backed by a caching engine to gain slightly better performance when it comes to evicting expired tickets.

This component does not preserve ticket state across restarts and is not a suitable solution
for clustered CAS environments that are deployed in active/active mode.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#inmemory-ticket-registry).

### Eviction Policy

This ticket registry relies on a background job that is automatically scheduled to clean up after the registry and remove expired tickets. The cleaner will periodically examine the state of the registry to identify expired tickets, remove them from the registry and then execute relevant logout operations.

In the event that the ticket registry is configured to use caching engine, CAS configured the cache store automatically such that each ticket put into the cache is given the ability to automatically expire based on the expiration policies defined for each ticket. The cache is constantly on its own monitoring for eviction events and once an item is deemed expired and evicted, CAS will take over to run logout operations. This means that running the default registry in this mode does not require CAS to schedule and maintain a background job to look after ticket state given the cache cleans up after itself.
