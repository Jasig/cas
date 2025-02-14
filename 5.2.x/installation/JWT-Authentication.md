---
layout: default
title: CAS - JWT Authentication
---

# JWT Authentication

[JSON Web Tokens](http://jwt.io/) are an open, industry standard RFC 7519 method for representing claims securely between two parties.
CAS provides support for token-based authentication on top of JWT, where an authentication request can be granted an SSO session based
on a form of credentials that are JWTs.

## JWT Service Tickets

CAS may also be allowed to fully create signed/encrypted JWTs and pass them back to the application in form of service tickets.
In this case, JWTs are entirely self-contained and contain the authenticated principal as well as all authorized attributes
in form of JWT claims. To learn more about this functionality, [please review this guide](Configure-ServiceTicket-JWT.html).

## Overview

CAS expects a `token` parameter (or request header) to be passed along to the `/login` endpoint. The parameter value must be a JWT.

<div class="alert alert-info"><strong>JCE Requirement</strong><p>It's safe to make sure you have the proper JCE bundle installed in your Java environment that is used by CAS, specially if you need to use specific signing/encryption algorithms and methods. Be sure to pick the right version of the JCE for your Java version. Java versions can be detected via the <code>java -version</code> command.</p></div>

Here is an example of how to generate a JWT via [Pac4j](https://github.com/pac4j/pac4j):

```java
final String signingSecret = RandomStringUtils.randomAlphanumeric(256);
final String encryptionSecret = RandomStringUtils.randomAlphanumeric(48);

System.out.println("signingSecret " + signingSecret);
System.out.println("encryptionSecret " + encryptionSecret);

final JwtGenerator<CommonProfile> g = new JwtGenerator<>();
g.setSignatureConfiguration(new SecretSignatureConfiguration(signingSecret, JWSAlgorithm.HS256));
g.setEncryptionConfiguration(new SecretEncryptionConfiguration(encryptionSecret,
        JWEAlgorithm.DIR, EncryptionMethod.A192CBC_HS384));

final CommonProfile profile = new CommonProfile();
profile.setId("casuser");
final String token = g.generate(profile);
System.out.println("token: " + token);
```

Once the token is generated, you may pass it to the `/login` endpoint of CAS as such:

```bash
/cas/login?service=https://...&token=<TOKEN_VALUE>
```

The `token` parameter may also be passed as a request header.

## Configuration

JWT authentication support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-token-webflow</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#jwttoken-authentication).

Configure the appropriate service in your service registry to hold the secrets:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "https://.+",
  "name" : "testId",
  "id" : 1,
  "properties" : {
    "@class" : "java.util.HashMap",
    "jwtSigningSecret" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "<SECRET>" ] ]
    },
    "jwtEncryptionSecret" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "<SECRET>" ] ]
    },
    "jwtSigningSecretAlg" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "HS256" ] ]
    },
    "jwtEncryptionSecretAlg" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "dir" ] ]
    },
    "jwtEncryptionSecretMethod" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "A192CBC-HS384" ] ]
    },
    "jwtSecretsAreBase64Encoded" : {
       "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
       "values" : [ "java.util.HashSet", [ "false" ] ]
    }
  }
}
```

Note that the only required property is `jwtSigningSecret`.
