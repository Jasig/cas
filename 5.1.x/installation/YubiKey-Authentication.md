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
following dependencies in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-yubikey</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#yubikey).

By default, all YubiKey accounts for users are allowed to authenticate. If you wish to plug in a custom registry implementation that would determine
which users are allowed to use their YubiKey accounts for authentication, you may plug in a custom implementation of the `YubiKeyAccountRegistry`
that allows you to provide a mapping between usernames and YubiKey public keys.


```java
package org.apereo.cas.support.yubikey;

@Configuration("myYubiKeyConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyYubiKeyConfiguration {

  @Bean
  public YubiKeyAccountRegistry yubiKeyAccountRegistry() {
      ...
  }
}
```
