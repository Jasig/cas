---
layout: default
title: CAS - RADIUS Authentication
category: Multifactor Authentication
---

# RADIUS Authentication

RADIUS support is enabled by only including the following dependency in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-radius</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#radius-authentication).

# RSA RADIUS MFA

RSA RADIUS OTP support for MFA is enabled by only including the following dependency in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-radius-mfa</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#radius-otp).

## Repository

You may also need to declare the following repository in
your CAS overlay to be able to resolve dependencies:

```xml
<repositories>
    ...
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
    ...
</repositories>
```
