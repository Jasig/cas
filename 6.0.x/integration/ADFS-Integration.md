---
layout: default
title: CAS - ADFS Integration
category: Authentication
---

# Overview

The integration between the CAS Server and ADFS delegates user authentication from CAS Server
to ADFS, making CAS Server a WS-Federation client. Claims released from ADFS are made available as attributes to CAS Server, and by extension CAS Clients.

<div class="alert alert-info"><strong>Remember</strong><p>The functionality described here allows CAS to use ADFS as an external identity provider. If you wish to do the opposite, allowing ADFS to become a CAS client and using CAS as an identity provider, you may take advantage of <a href="../installation/Configuring-SAML2-Authentication.html">SAML2 support in CAS</a> as one integration option.</p></div>

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-wsfederation-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

You may also need to declare the following repository in your
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

<div class="alert alert-info"><strong>JCE Requirement</strong><p>It's safe to make sure you have the proper JCE bundle 
installed in your Java environment that is used by CAS, specially if you need to consume encrypted payloads issued by ADFS. 
Be sure to pick the right version of the JCE for your Java version. Java versions can be detected via the <code>java -version</code> command.</p></div>

## WsFed Configuration

Adjust and provide settings for the ADFS instance, and make sure you have obtained the ADFS signing certificate
and made it available to CAS at a location that can be resolved at runtime.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#ws-fed-delegated-authentication).

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

The WsFed configuration optionally may allow you to manipulate claims coming from ADFS but before they are inserted into the CAS user principal.
The manipulation of the attributes is carried out using an *attribute mutator* where its logic may be implemented inside a Groovy script and whose
path is taught to CAS via settings.

The script may take on the following form:

```groovy
import org.apereo.cas.*
import java.util.*
import org.apereo.cas.authentication.*

def Map run(final Object... args) {
    def attributes = args[0]
    def logger = args[1]
    logger.warn("Mutating attributes {}", attributes)
    return [upn: ["CASUser"]]
}
```

The parameters passed to the script are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `attributes`          | A current `Map` of attributes provided from ADFS.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

Note that the execution result of the script *MUST* ensure that attributes are collected into a `Map`
where the attribute name, the key, is a simple `String` and the attribute value is transformed into a collection.

## Handling CAS Logout

An optional step, the `casLogoutView.html` can be modified to place a link to ADFS's logout page.

```html
<a href="https://adfs.example.org/adfs/ls/?wa=wsignout1.0">Logout</a>
```

Alternatively, you may simply instruct CAS to redirect to the above endpoint after logout operations have executed.
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#logout).

## Per-Service Relying Party Id

In order to specify a relying party identifier per service definition, adjust your service
registry to match the following:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "sample service",
  "id" : 100,
  "properties" : {
    "@class" : "java.util.HashMap",
    "wsfed.relyingPartyIdentifier" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "custom-identifier" ] ]
    }
  }
}
```

## Troubleshooting

Be aware of clock drift issues between CAS and the ADFS server. Validation failures of the response do show up in the logs, and the request is routed back to
 ADFS again, causing redirect loops.
