---
layout: default
title: CAS - JavaMelody Monitoring
category: Monitoring & Statistics
---

# JavaMelody Monitoring

Use [JavaMelody](https://github.com/javamelody/javamelody) is to monitor CAS in QA and production environments.

Support is added by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-javamelody</artifactId>
    <version>${cas.version}</version>
</dependency>
```

JavaMelody monitoring is by default exposed at `${context-path}/monitoring` where `${context-path}` is typically set to `/cas`.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#javamelody).
