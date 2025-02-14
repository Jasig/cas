---
layout: default
title: CAS - Swivel Secure Authentication
category: Multifactor Authentication
---

# Swivel Secure Authentication

Swivel Secure offers a wide range of authentication factors, allowing the use of 2FA and image based authentication. To learn more, please refer to [the official website](https://swivelsecure.com/).

CAS supports Swivel Secure's TURing-image based authentication. TURing uses the PINsafe protocol to provide a One Time Code for authentication. Each image is unique for that session. 

![image](https://user-images.githubusercontent.com/1205228/27012173-e8e32020-4e98-11e7-935f-c5166f228bd5.png)

## Configuration

Support is enabled by including the following module in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-swivel</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#swivel-secure).

## Logging

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="com.swiveltechnologies" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```

## Swivel SDK

Note that Swivel SDK artifacts are not published to a Maven repository. This means that you will need to download the necessary JAR files and include the in your build configuration. The SDK may be downloaded from [the CAS codebase](https://github.com/apereo/cas/blob/master/support/cas-server-support-swivel/lib/pinsafe.client.jar). Then, assuming the SDK is placed inside a `lib` directory of the [WAR overlay](../installation/WAR-Overlay-Installation.html) directory, it can be referenced in the build configuration as such:

```gradle
compile files("${projectDir}/lib/pinsafe.client.jar")
```
