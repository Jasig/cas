---
layout: default
title: CAS - Authy Authentication
category: Multifactor Authentication
---

# Authy Authentication

CAS provides support for Authy's [TOTP API](http://docs.authy.com/totp.html). This is done
via Authy's REST API that does all the heavy lifting.

Start by visiting the [Authy documentation](https://www.authy.com/developers/).

Support is enabled by including the following module in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-authy</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authy).

## Registration

By default, users are registered with authy based on their phone and email attributes retrieved by CAS.
