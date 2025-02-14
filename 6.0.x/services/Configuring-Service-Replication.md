---
layout: default
title: CAS - Configuring Service Replication
category: Services
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

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-service-registry-stream-hazelcast</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#service-registry-replication-hazelcast).

## Replication Modes

When CAS is configured to replicate service definitions in an active-active mode, you will need to make sure the service registry scheduler is carefully tuned in order to avoid surprises and overwrites. Likewise, the same sort of check needs to be done and verified for ad-hoc dynamic changes to the CAS service registry directory, if CAS is set to monitor for changes. Delays in replication and schedule may force one node to overwrite changes to the other. 

For instance, consider the following scenario: there are two nodes in a CAS cluster where CAS1 is set to monitor changes from `/etc/cas/services` on node N1 and CAS2 is monitoring `/etc/cas/services` directory on node N2. Both N1 and N2 on startup attempt to bootstrap each other's copies of service definitions to make sure all is synchronized correctly. 

Now let's consider that a file is `/etc/cas/services/App-100.json` is deleted from N2. In the time that it takes from N2 to broadcast the change to N1, it is likely that service registry scheduler for N2 also wakes up and attempts to restore the state of the world by synchronizing its copies of its service definition files from the distributed cache, which means that N2 will grab a copy of the deleted service from N1 and will restore the deleted file back. This situation typically manifests itself when the service registry scheduler is set to very aggressive timeouts and can mostly be avoided by relaxing the reload operation to run on a long scheduler such as every 2 hours. Alternatively, you may decide to simply run an active-passive setup to only have one master node produce and broadcast changes and other slave/passive nodes simply and only consume changes when needed.