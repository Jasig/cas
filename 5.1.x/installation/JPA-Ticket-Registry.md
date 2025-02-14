---
layout: default
title: CAS - JPA Ticket Registry
---


# JPA Ticket Registry
The JPA Ticket Registry allows CAS to store client authenticated state
data (tickets) in a database back-end such as MySQL.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Using a RDBMS as
the back-end persistence choice for Ticket Registry state management is a fairly unnecessary and complicated
process. Unless you are already outfitted with clustered RDBMS technology and the resources to manage it,
the complexity is likely not worth the trouble.</p></div>

Support is enabled by adding the following module into the Maven overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jpa-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#jpa-ticket-registry).

## TGT Locking

TGTs are almost always updated within the same transaction they are loaded from the database in, but
after some processing delays. Because of this, the JPA Ticket Registry utilizes write locks on all loads of
TGTs from the database to prevent deadlocks and ensure usage meta-data consistency when a single
TGT is used concurrently by multiple requests.

This reduces performance of the JPA Ticket Registry and may not be desirable or necessary for some deployments depending
the database in use, its configured transaction isolation level, and expected concurrency of a single
TGT.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#jpa-ticket-registry).
