---
layout: default
title: CAS - Memcached Ticket Registry
---

# Memcached Ticket Registry

Memcached integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

This registry stores tickets in one or more [memcached](http://memcached.org/) instances. 
Memcached stores data in exactly one node among many in a distributed cache, thus avoiding the requirement to replicate
or otherwise share data between nodes. A deterministic function is used to locate the node, _N'_, on which to store
key _K_:

    N' = f(h(K), N1, N2, N3, ... Nm)

where _h(K)_ is the hash of key _K_, _N1 ... Nm_ is the set of cache nodes, and _N'_ ∈ _N ... Nm_.

The function is deterministic in that it consistently produces the same result for a given key and set of cache nodes.
Note that a change in the set of available cache nodes may produce a different target node on which to store the key.

The actual memcached implementation may be supported via one of the following options, expected to be defined in the overlay.

##  Spymemcached

Enable support via the [spymemcached library](https://code.google.com/p/spymemcached/). This is a simple, asynchronous, 
single-threaded memcached client that should be the default choice for the majority of deployments.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-spy</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## AWS ElastiCache

You may also use [AWS ElastiCache](https://docs.aws.amazon.com/AmazonElastiCache/latest/UserGuide/AutoDiscovery.html) 
which is a web service that makes it easy to set up, manage, and scale a distributed in-memory 
data store or cache environment in the cloud. It provides a high-performance, scalable, and cost-effective caching 
solution, while removing the complexity associated with deploying and managing a distributed cache environment.

For clusters running the Memcached engine, ElastiCache supports Auto Discovery—the ability 
for client programs to automatically identify all of the nodes in a cache cluster, 
and to initiate and maintain connections to all of these nodes. With Auto Discovery, 
CAS does not need to manually connect to individual cache nodes; instead, CAS connects to one 
Memcached node and retrieves the list of nodes. From that list, CAS is aware of the rest 
of the nodes in the cluster and can connect to any of them. You do not need to hard 
code the individual cache node endpoints in the configuration

All of the cache nodes in the cluster maintain a list of metadata about all of the other nodes. 
This metadata is updated whenever nodes are added or removed from the cluster.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-memcached-aws-elasticache</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration Considerations

There are three core configuration concerns with memcached:

1. Hash Algorithm
2. Node locator strategy
3. Object serialization mechanism

### Hash Algorithm

The hash algorithm is used to transform a key value into a memcached storage key that uniquely identifies the
corresponding value. The choice of hashing algorithm has implications for failover behavior that is important
for HA deployments. The `FNV1_64_HASH` algorithm is recommended since it offers a nice balance of speed and low
collision rate; see the [javadocs](https://github.com/couchbase/spymemcached/blob/2.8.1/src/main/java/net/spy/memcached/DefaultHashAlgorithm.java)
for alternatives.

### Node Locator

The node locator serves as the deterministic node selection function for the memcached client provided by the
underlying spymemcached library. There are two choices:

1. [ARRAY_MOD](https://github.com/couchbase/spymemcached/blob/2.8.1/src/main/java/net/spy/memcached/ArrayModNodeLocator.java)
2. [CONSISTENT](https://github.com/couchbase/spymemcached/blob/2.9.0/src/main/java/net/spy/memcached/KetamaNodeLocator.java)

The array modulus mechanism is the default and suitable for cases when the number of nodes in the memcached pool is
expected to be consistent. The algorithm simply computes an index into the array of memcached nodes:

    hash(key) % length(nodes)

Obviously the selected index is a function of the number of memcached nodes, so variance in number of nodes produces
variance in the node selected to store the key, which is undesirable.

The consistent strategy generally provides a target node that does not vary with the number of nodes. This strategy
should be used in cases where the memcached pool may grow or shrink dynamically, including due to frequent node
failure.


### Object Serialization

Memcached stores bytes of data, so CAS tickets must be serialized to a byte array prior to storage. CAS ships with
a custom serialization component `KryoTranscoder` based on the [Kryo](https://code.google.com/p/kryo/) serialization
framework. This component is recommended over the default Java serialization mechanism since it produces much more
compact data, which benefits both storage requirements and throughput.

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#memcached-ticket-registry).

## High Availability Considerations

Memcached does not provide for replication by design, but the client is tolerant to node failures with
`failureMode="Redistribute"`. In this mode a write failure will simply cause the client to flag the node as failed
and remove it from the set of available nodes. It subsequently recomputes the node location function with the reduced
node set to find a new node on which to store the key. If the node location function selects the same node,
which is likely for the _CONSISTENT_ strategy, a backup node will be computed. The value is written to and read from
the failover node until the primary node recovers. The client will periodically check the failed node for liveliness
and restore it to the node pool as soon as it recovers. When the primary node is resurrected, if it contains a value
for a particular key, it would supersede the value known to the failover node. The most common effect on CAS behavior
in this circumstance would occur when ticket-granting tickets have duplicate values, which could affect single sign-out
and prevent access to services. In particular, services accessed and forced authentications that occur while the
failover service is active would be lost when the failed node recovers. In most cases this behavior is tolerable,
but it can be avoided by restarting the memcached service on the failed node prior to rejoining the cache pool.

A read failure in _Redistribute_ mode causes the node to be removed from the set of available nodes, a failover node
is computed, and a value is read from that node. In most cases this results in a key not found situation. The effect
on CAS behavior depends on the type of ticket requested:

* Service ticket - Service access would be denied for the requested ticket, but permitted for subsequent attempts since
a new ticket would be generated and validated.
* Ticket-granting ticket - The SSO session would be terminated and re-authentication would be required.

Read failures are thus entirely innocuous for environments where re-authentication is acceptable.
