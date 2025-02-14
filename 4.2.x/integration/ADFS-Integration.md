---
layout: default
title: CAS - ADFS Integration
---

# Overview
The integration between the CAS Server and ADFS delegates user authentication from CAS Server to ADFS, making CAS Server a WS-Federation client. 
Claims released from ADFS are made available as attributes to CAS Server, and by extension CAS Clients.

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-wsfederation-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

You may also need to declare the following Maven repository in your 
CAS Overlay to be able to resolve dependencies:

```xml
<repositories>
    ...
    <repository>
        <id>shibboleth-releases</id>
        <url>https://build.shibboleth.net/nexus/content/repositories/releases</url>
    </repository>
    ...
</repositories>
```

<div class="alert alert-info"><strong>JCE Requirement</strong><p>It's safe to make sure you have the proper JCE bundle installed in your Java environment that is used by CAS, specially if you need to consume encrypted payloads issued by ADFS. Be sure to pick the right version of the JCE for your Java version. Java versions can be detected via the <code>java -version</code> command.</p></div>

## WsFed Configuration

Adjust and provide settings for the ADFS instance, and make sure you have obtained the ADFS signing certificate
and made it available to CAS at a location that can be resolved at runtime.

```properties
# The claim from ADFS that should be used as the user's identifier.
# cas.wsfed.idp.idattribute=upn
#
# Federation Service identifier
cas.wsfed.idp.id=
#
# The ADFS login url.
cas.wsfed.idp.url=
#
# Identifies resource(s) that point to ADFS's signing certificates.
# These are used verify the WS Federation token that is returned by ADFS.
# Multiple certificates may be separated by comma.
cas.wsfed.idp.signingcerts=classpath:adfs-signing.crt
#
# Unique identifier that will be set in the ADFS configuration.
# cas.wsfed.rp.id=urn:cas:localhost
#
# Slack dealing with time-drift between the ADFS Server and the CAS Server.
# cas.wsfed.idp.tolerance=10000

# cas.wsfed.idp.attribute.resolver.enabled=true
# cas.wsfed.idp.attribute.resolver.type=WSFED

# Private/Public keypair used to decrypt assertions, if any.
# cas.wsfed.idp.enc.privateKey=classpath:private.key
# cas.wsfed.idp.enc.cert=classpath:certificate.crt
# cas.wsfed.idp.enc.privateKeyPassword=NONE
```

## Encrypted Assertions

CAS is able to automatically decrypt SAML assertions that are issued by ADFS. To do this, 
you will first need to generate a private/public keypair:

```bash
openssl genrsa -out private.key 1024
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
openssl pkcs8 -topk8 -inform PER -outform DER -nocrypt -in private.key -out private.p8
openssl req -new -x509 -key private.key -out x509.pem -days 365

# convert the X509 certificate to DER format
openssl x509 -outform der -in x509.pem -out certificate.crt
```

Configure CAS to reference the keypair, and configure the relying party trust settings
in ADFS to use the `certificate.crt` file for encryption.

## Modifying ADFS Claims

The WsFed configuration optionally may allow you to manipulate claims coming from ADFS but before they are inserted into the CAS user principal. For this to happen, you need
to put together an implementation of `WsFederationAttributeMutator` that changes and manipulates ADFS claims:

```java
package org.jasig.cas.support.wsfederation;

public class WsFederationAttributeMutatorImpl implements WsFederationAttributeMutator {
    public void modifyAttributes(...) {
        ...
    }
}
```

The mutator then needs to be declared in your configuration:

```xml
<bean id="wsfedAttributeMutator"
    class="org.jasig.cas.support.wsfederation.WsFederationAttributeMutatorImpl" />
```


Finally, ensure that the attributes sent from ADFS are available and mapped in
your `attributeRepository` configuration.

## Handling CAS Logout

An optional step, it is recommended that the `casLogoutView.jsp` be replace to redirect to ADFS's logout page.

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:redirect url="https://adfs.example.org/adfs/ls/?wa=wsignout1.0"/>
```
