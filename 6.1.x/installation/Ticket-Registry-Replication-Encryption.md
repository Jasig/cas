---
layout: default
title: CAS - Configuring Ticketing Components
category: Ticketing
---

# Ticket Registry Replication Encryption
The following ticket registries are able to support secure ticket replication
by encrypting and signing tickets:

* [Hazelcast](../ticketing/Hazelcast-Ticket-Registry.html)
* [Ehcache](../ticketing/Ehcache-Ticket-Registry.html)
* [Ignite](../ticketing/Ignite-Ticket-Registry.html)
* [CouchDb](../ticketing/CouchDb-Ticket-Registry.html)
* [Memcached](../ticketing/Memcached-Ticket-Registry.html)

<div class="alert alert-info"><strong>Default Behavior</strong><p>Encryption by default is turned off
when you use the above ticket registries. It requires explicit configuration before it can be used.</p></div>

## Configuration

Each ticket registry configuration supports a cipher component that needs to be configured by the deployer.
The settings, algorithms and secret keys used for the cipher may be controlled via CAS settings.
Refer to the settings allotted for each registry to learn more about ticket encryption.

Additionally, [Ignite](../ticketing/Ignite-Ticket-Registry.html) may be configured to use TLS for replication transport.
