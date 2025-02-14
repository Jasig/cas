---
layout: default
title: CAS - U2F - FIDO Universal 2nd Factor Authentication
---

# U2F - FIDO Universal Authentication

U2F is an open authentication standard that enables internet users to securely access any number of online services, with one single device, instantly and with no drivers, or client software needed. The CAS U2F implementation is built on top of [Yubico](https://www.yubico.com/about/background/fido/) and the technical specifications are hosted by the open-authentication industry consortium known as the [FIDO Alliance](https://fidoalliance.org/).

Note that not all browsers today support U2F. While support in recent versions of Chrome and Opera seem to exist, you should [always verify](https://www.yubico.com/support/knowledge-base/categories/articles/browsers-support-u2f/) that U2F support is available for your target browser. 

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-u2f</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#fido-u2f).

## Registration

By default, an account registry implementation is included that collects user device registrations and saves them into memory.
This option should only be used for demo and testing purposes. Production deployments of this feature today will require a separate
implementation of the registry that is capable to register accounts into persistent storage. Additional options for storage may be added
later based on demand and availability.
