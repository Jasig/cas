---
layout: default
title: CAS - Google Analytics
category: Integration
---

# Google Analytics

Google Analytics can be used to deliver useful statistics. create custom dimensions and metrics to gain
insight into CAS and user traffic.


Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-google-analytics</artifactId>
     <version>${cas.version}</version>
</dependency>
```

Furthermore, CAS presents the ability to drop in a special cookie upon successful authentication events to be later process
and consumed by Google Analytics. The value of this cookie is determined as a principal/authentication attribute.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#google-analytics).
