---
layout: default
title: CAS - Configuring Ticketing Components
---

# Ticket Registry Replication Encryption
The following ticket registries are able to support secure ticket replication
by encrypting and signing tickets:

* [Hazelcast](Hazelcast-Ticket-Registry.html)
* [Ehcache](Ehcache-Ticket-Registry.html)
* [Ignite](Ignite-Ticket-Registry.html)
* [Memcached](Memcached-Ticket-Registry.html)

<div class="alert alert-info"><strong>Default Behavior</strong><p>Encryption by default is turned off
when you use the above ticket registries. It requires explicit configuration before it can be used.</p></div>

## Configuration

Each ticket registry configuration supports a cipher component that needs to be configured by the deployer. A typical cipher configuration may be the following, placed into the `ticketRegistry.xml` file:

```xml
<alias name="shiroCipherExecutor" alias="ticketCipherExecutor" />
```

The settings, algorithms and secret keys used for the cipher may be controlled via `cas.properties`:

```properties
# Secret key to use when encrypting tickets in a distributed ticket registry.
# ticket.encryption.secretkey=C@$W3bSecretKey!

# Secret key to use when signing tickets in a distributed ticket registry.
# By default, must be a octet string of size 512.
# ticket.signing.secretkey=szxK-5_eJjs-aUj-64MpUZ-GPPzGLhYPLGl0wrYjYNVAGva2P0lLe6UGKGM7k8dWxsOVGutZWgvmY3l5oVPO3w
```

Additionally, [Ignite](Ignite-Ticket-Registry.html) may be configured to use TLS for replication transport.
