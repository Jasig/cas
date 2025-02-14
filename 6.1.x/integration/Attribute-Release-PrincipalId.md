---
layout: default
title: CAS - Releasing Principal Id
category: Attributes
---

# Principal-Id Attribute

Registered CAS applications are given the ability to allow for configuration of a
username attribute provider, which controls what should be the designated user identifier
that is returned to the application. The user identifier by default is the authenticated CAS principal id, yet it optionally may be based off of an existing 
attribute that is available and resolved for the principal already. More practically, this component determines what should be placed inside the `<cas:user>`
 tag in the final CAS validation payload that is returned to the application.

<div class="alert alert-warning"><strong>Principal Id As Attribute</strong><p>You may also return the authenticated principal id as an extra attribute in the final CAS payload. See <a href="Attribute-Release-Policies.html">this guide</a> to learn more.</p></div>

A number of providers are able to perform canonicalization on the final user id returned to transform it
into uppercase/lowercase. This is noted by the `canonicalizationMode` whose allowed values are `UPPER`, `LOWER` or `NONE`.

## Default

The default configuration which need not explicitly be defined, simply returns the resolved
principal id as the username for this service.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceUsernameProvider",
    "canonicalizationMode" : "NONE"
  }
}
```

If you do not need to adjust the behavior of this provider (i.e. to modify the `canonicalization` mode),
then you can leave out this block entirely.

## Encrypted

Most if not all providers are able to encrypt the resolved username, assuming the service definition is given a public key.

The key can be generated via the following commands:

```bash
openssl genrsa -out private.key 1024
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
openssl pkcs8 -topk8 -inform PER -outform DER -nocrypt -in private.key -out private.p8
```

The public key is then configured for a regex service definition in CAS:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceUsernameProvider",
    "encryptUsername" : "true"
  },
  "publicKey" : {
    "@class" : "org.apereo.cas.services.RegisteredServicePublicKeyImpl",
    "location" : "classpath:public.key",
    "algorithm" : "RSA"
  }
}
```

The application can then proceed to decrypt the username using its own private key.
The following sample code demonstrates how that might be done in Java:

```java
final String casUsername = ...
final PrivateKey privateKey = ...
final Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
final byte[] cred64 = decodeBase64(encodedPsw);
cipher.init(Cipher.DECRYPT_MODE, privateKey);
final byte[] cipherData = cipher.doFinal(casUsername);
return new String(cipherData);
```

## Attribute

Returns an attribute that is already resolved for the principal as the username for this service. If the attribute
is not available, the default principal id will be used.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 600,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.PrincipalAttributeRegisteredServiceUsernameProvider",
    "usernameAttribute" : "cn",
    "canonicalizationMode" : "UPPER"
  }
}
```

## Javascript/Python/Ruby/Groovy Script

Let an external javascript, groovy or python script decide how the principal id attribute should be determined.
This approach takes advantage of scripting functionality built into the Java platform.
While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).

Scripts will receive and have access to the following variable bindings:

- `id`: The existing identifier for the authenticated principal.
- `attributes`: A map of attributes currently resolved for the principal.
- `logger`: A logger object, able to provide `logger.info()` operations, etc.


```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 500,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.ScriptedRegisteredServiceUsernameProvider",
    "script" : "file:/etc/cas/sampleService.[groovy|js|.py]",
    "canonicalizationMode" : "UPPER"
  }
}
```

Sample Groovy script follows:

```groovy
def run(Object[] args) {
    def attributes = args[0]
    def id = args[1]
    def logger = args[2]
    logger.info("Testing username attribute")
    return "test"
}
```

Sample javascript function follows:

```javascript
function run(uid, logger) {
   return "test"
}
```

## Groovy

Returns a username attribute value as the final result of a groovy script's execution.
Groovy scripts whether inlined or external will receive and have access to the following variable bindings:

- `id`: The existing identifier for the authenticated principal.
- `attributes`: A map of attributes currently resolved for the principal.
- `service`: The service object that is matched by the registered service definition.
- `logger`: A logger object, able to provide `logger.info(...)` operations, etc.


### Inline

Embed the groovy script directly inside the service configuration.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 600,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.GroovyRegisteredServiceUsernameProvider",
    "groovyScript" : "groovy { return attributes['uid'][0] + '123456789' }",
    "canonicalizationMode" : "UPPER"
  }
}
```

### External

Reference the groovy script as an external resource outside the service configuration.
The script must return a single `String` value.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 600,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.GroovyRegisteredServiceUsernameProvider",
    "groovyScript" : "file:/etc/cas/sampleService.groovy",
    "canonicalizationMode" : "UPPER"
  }
}
```

Sample Groovy script follows:

```groovy
logger.info("Choosing username attribute out of attributes $attributes")
return "newPrincipalId"
```

## Anonymous / Transient

Provides an opaque identifier for the username. 

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 500,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.AnonymousRegisteredServiceUsernameAttributeProvider"
  }
}
```

## Anonymous / Persistent

Provides an opaque identifier for the username. The opaque identifier by default conforms to the requirements
of the [eduPersonTargetedID](http://www.incommon.org/federation/attributesummary.html#eduPersonTargetedID) attribute.
The generated id may be based off of an existing principal attribute. If left unspecified or attribute not found, the authenticated principal id is used.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 500,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.AnonymousRegisteredServiceUsernameAttributeProvider",
    "persistentIdGenerator" : {
      "@class" : "org.apereo.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator",
      "salt" : "aGVsbG93b3JsZA==",
      "attribute": ""
    }
  }
}
```

To simulate the behavior, you may also try the following command:

```bash
perl -e 'use Digest::SHA qw(sha1_base64); \
    $digest = sha1_base64("$SERVICE!$USER!$SALT"); \
    $eqn = length($digest) % 4; print $digest; print "=" x (4-$eqn) . "\n"' 
```

Replace `$SERVICE` (the url of the application under test), `$USER` and `$SALT` with the appropriate values for the test.

