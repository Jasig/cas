---
layout: default
title: CAS - U2F - FIDO Universal 2nd Factor Authentication
category: Multifactor Authentication
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

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f).

## Registration

U2F device registration flows are baked into CAS automatically. A background *cleaner* process is also automatically scheduled to scan the repository periodically and remove expired device registration records based on configured parameters.

<div class="alert alert-warning"><strong>Cleaner Usage</strong><p>In a clustered CAS deployment, it is best to keep the cleaner running on one designated 
CAS node only and turn it off on all others via CAS settings. Keeping the cleaner running on all nodes may likely lead to severe performance and locking issues.</p></div>

### Default

By default, a repository implementation is included that collects user device registrations and saves them into memory.
This option should only be used for demo and testing purposes.

### JSON

A simple device repository implementation that collects user device registrations and saves them into a JSON file whose path is taught to CAS via settings. This is a very modest option and should mostly be used for demo and testing purposes. Needless to say, this JSON resource acts as a database that must be available to all CAS server nodes in the cluster.

Devices stored into the JSON file take on the following format:

```json
{
  "@class" : "java.util.HashMap",
  "services" : [ "java.util.ArrayList", [ {
    "@class" : "org.apereo.cas.adaptors.u2f.storage.U2FDeviceRegistration",
    "id" : 1508515100762,
    "username" : "casuser",
    "record" : "{\"keyHandle\":\"keyhandle11\",\"publicKey\":\"publickey1\",\"counter\":1,\"compromised\":false}",
    "createdDate" : "2016-10-15"
  } ] ]
}
```

### Groovy

Device registrations may be managed via an external Groovy script. The script may be designed as follows:

```groovy
import java.util.*
import org.apereo.cas.adaptors.u2f.storage.*

Map<String, List<U2FDeviceRegistration>> read(final Object... args) {
    def logger = args[0]
    ...
    return null;
}

Boolean write(final Object... args) {
    List<U2FDeviceRegistration> list = args[0]
    def logger = args[1]
    ...
    return true;
}

void removeAll(final Object... args) {
    def logger = args[0]
}
```

### JPA

Device registrations may be kept inside a relational database by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-u2f-jpa</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f-jpa).

### MongoDb

Device registrations may be kept inside a MongoDb instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-u2f-mongo</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f-mongodb).

### Redis

Device registrations may be kept inside a Redis instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-u2f-redis</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f-redis).

### CouchDb

Device registrations may be kept inside a CouchDb instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-u2f-couchdb</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f-couchdb).

### REST

Device registrations may be managed via REST APIs. Endpoints must be designed to accept/process `application/json`. The syntax for he collection of devices passed back and forth is designed in JSON and is identical to the JSON structure defined above.

The following parameters are passed:

| Operation        | Parameters      | Description      | Result
|------------------|-----------------|-----------------------------------------------------------------------
| `GET`            | N/A             | Retrieve all registered devices.     | `200` status code Collection of registered devices as JSON in the body of the response.
| `POST`           | Collection of registered devices as JSON | Store registered devices. | `200`.
| `DELETE`           | N/A | Delete all device records | `200`.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido-u2f-rest).
