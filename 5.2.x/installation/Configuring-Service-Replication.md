---
layout: default
title: CAS - Configuring Service Replication
---

# Configure Service Replication

In the event that CAS service definitions are not managed globally via a [centralized store](Service-Management.html), 
definitions need to be kept in sync throughout all CAS nodes in a cluster when more than one node is deployed. 
When the management strategy of such definitions is to store them on disk local to each node (such as [JSON](JSON-Service-Management.html) or [YAML](YAML-Service-Management.html)) files, 
the following mechanisms may be used to copy files from one host to another.

## Native

A background task can be scheduled with the likes of `rsync` to copy files from from host to another. 
The job needs to of course run periodically to ensure configuration is kept in sync. 
This is the simplest option as CAS is completely ignorant of extra process in the background.

On Linux machines, `rsync` may be installed as:

```bash
# yum install rsync (On Red Hat based systems)
# apt-get install rsync (On Debian based systems)
```

As an example, this command will sync a directory `/etc/cas/services` from a local machine to a remote server:

```bash
rsync -avz /etc/cas/services root@192.168.0.101:/etc/cas/services
```

The exact opposite of the above command may be carried as such:

```bash
rsync -avzh root@192.168.0.100:/etc/cas/services /etc/cas/services
```

- To execute the transfer operation over ssh, use the `ssh --progress` flags.
- To test the command execution in mock mode, use the `--dry-run` flag.

## Hazelcast

If you'd rather not resort to outside tooling and processes or if the native options for your 
deployment are not that attractive, you can take advantage of CAS' own tooling that provides a 
distributed cache to broadcast service definition files across the cluster and add/remove/update 
each node as needed. As service definitions are loaded by CAS, events are broadcasted to all 
CAS nodes in the cluster to pick up the changes and keep definitions in sync. 

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>This feature is experimental.</p></div>

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-service-registry-stream-hazelcast</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, 
please [review this guide](Configuration-Properties.html#service-registry-replication-hazelcast).
