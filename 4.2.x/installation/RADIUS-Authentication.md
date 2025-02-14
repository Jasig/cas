---
layout: default
title: CAS - RADIUS Authentication
---

# RADIUS Authentication
RADIUS support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-radius</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Configuration

```xml
<alias name="radiusAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

The following settings may control the configuration RADIUS server and client:

```properties
# cas.radius.client.inetaddr=localhost
# cas.radius.client.port.acct=
# cas.radius.client.socket.timeout=60
# cas.radius.client.port.authn=
# cas.radius.client.sharedsecret=N0Sh@ar3d$ecReT
# cas.radius.server.protocol=EAP_MSCHAPv2
# cas.radius.server.retries=3
# cas.radius.server.nasIdentifier=
# cas.radius.server.nasPort=-1
# cas.radius.server.nasPortId=-1
# cas.radius.server.nasRealPort=-1
# cas.radius.server.nasPortType=-1
# cas.radius.server.nasIpAddress=
# cas.radius.server.nasIpv6Address=
# cas.radius.failover.authn=false
# cas.radius.failover.exception=false
```
