---
layout: default
title: CAS - Amazon Cognito Authentication
category: Authentication
---

# Amazon Cognito Authentication

Verify and authenticate credentials using [Amazon Cognito](https://aws.amazon.com/cognito/).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aws-cognito-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#amazon-cognito-authentication).

When you create the *app client* entry in the Amazon Cognito management console, make sure the app is able to support the `ADMIN_NO_SRP_AUTH` authentication flow and it is *NOT* assigned a secret key.

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="com.amazonaws" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
