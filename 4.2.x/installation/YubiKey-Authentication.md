---
layout: default
title: CAS - YubiKey Authentication
---

# YubiKey Authentication
Yubico is a cloud-based service that enables strong, easy-to-use and affordable two-factor authentication with one-time passwords 
through their flagship product, YubiKey. Once Yubico `clientId` and `secretKey` are obtained, then the configuration option 
is available to use YubiKey devices as a primary authentication source that CAS server could use to authenticate users. 
To configure YubiKey accounts and obtain API keys, [refer to the documentation](https://upgrade.yubico.com/getapikey/).

[YubiKey](https://www.yubico.com/products/yubikey-hardware) authentication components are enabled by including the 
following dependencies in the Maven WAR overlay:

```xml
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-support-yubikey</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

The authentication handler may be configured as such:

```xml
<bean class="org.jasig.cas.adaptors.yubikey.YubiKeyAuthenticationHandler"
   	c:clientId="${yubikey.apiKey.id}"
   	c:secretKey="${yubikey.apiKey.secret}"/>
```

By default, all YubiKey accounts for users are allowed to authenticate. If you wish to plug in a custom registry implementation that would determine 
which users are allowed to use their YubiKey accounts for authentication, you may plug in a custom implementation of the `YubiKeyAccountRegistry`
that allows you to provide a mapping between usernames and YubiKey public keys.

```xml
<bean class="org.jasig.cas.adaptors.yubikey.YubiKeyAuthenticationHandler"
    c:clientId="${yubikey.apiKey.id}"
    c:secretKey="${yubikey.apiKey.secret}"
    c:registry-ref="customYubiKeyAccountRegistry" />
```


