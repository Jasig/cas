---
layout: default
title: CAS - Acceptto Authentication
category: Multifactor Authentication
---

# Acceptto Authentication

Secure your workforce identity with [Acceptto](https://www.acceptto.com) 
end-to-end risk-based multiFactor authentication.

Start by visiting the [Acceptto documentation](https://www.acceptto.com/acceptto-mfa-rest-api/).

Support is enabled by including the following module in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-acceptto-mfa</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The integration adds support for both multifactor authentication and QR passwordless authentication.

## Integration with DBFP

The integration is able to handle the integration with DBFP and will set a cookie named `jwt` that is passed to the Acceptto API. This parameter contains a value that the server uses to assess the risk of authentication request including browser fingerprint, IP address of user and GPS location of the user’s browser. The server compares this data with the history of user behavior data to detect anomalies.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptto).
