---
layout: default
title: CAS - FIDO2 WebAuthn Multifactor Authentication
category: Multifactor Authentication
---

# FIDO2 WebAuthn Multifactor Authentication

[WebAuthn](https://webauthn.io/) is an API that makes it very easy for a relying party, such as a web service, to integrate strong 
authentication into applications using support built in to all leading browsers and platforms. This means 
that web services can now easily offer their users strong authentication with a choice of authenticators 
such as security keys or built-in platform authenticators such as biometric readers.

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn).

## Primary Authentication

It is possible to allow WebAuthN to act as a standalone authentication strategy for primary authentication. Using this approach,
user accounts and FIDO2-enabled devices that have already registered with CAS are given the option to login using their FIDO2-enabled
device by only providing the username linked to their registration record for a passwordless authentication experience.

Device registration can occur out of band using available CAS APIs, or by allowing users to pass through the registration flow
as part of the typical multifactor authentication. See below for details on device registration. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn).

## Registration

Device registration flows are baked into CAS automatically. A background *cleaner* process is also automatically scheduled to scan the 
repository periodically and remove expired device registration records based on configured parameters. In the default setting, devices
expire after a fixed period since a user registered their device. If you deploy U2F
MFA for a setup where tokens are centrally distributed and revoked, 
you may want to [extend the interval](../configuration/Configuration-Properties.html#fido2-webauthn).

<div class="alert alert-warning"><strong>Cleaner Usage</strong><p>In a clustered CAS deployment, it is best to keep 
the cleaner running on one designated CAS node only and turn it off on all others via CAS settings. Keeping the cleaner running 
on all nodes may likely lead to severe performance and locking issues.</p></div>

### Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                                      | Description
|-----------------------------------------------|----------------------------------------------------------------------------
| `webAuthnDevices/{username}`                  | `GET` request to fetch device registration records for the user.
| `webAuthnDevices/{username}`                  | `DELETE` request to delete all device registration records for the user.
| `webAuthnDevices/{username}/{credential}`     | `DELETE` request to delete a device registration record for the user.
| `webAuthnDevices/{username}` | `POST` request to add a device registration record for the user with request body parameter `record`.

### Default

By default, a repository implementation is included that collects user device registrations and saves them into memory.
This option should only be used for demo and testing purposes.

### JSON

A device repository implementation that collects user device registrations and saves them into a JSON file whose 
path is taught to CAS via settings. This is a very modest option and should mostly be used for demo and testing 
purposes. Needless to say, this JSON resource acts as a database that must be available to all CAS server nodes in the cluster.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-json).

### MongoDb

Device registrations may be kept inside a MongoDb instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn-mongo</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-mongodb).

### LDAP

Device registrations may be kept inside LDAP directories by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn-ldap</artifactId>
     <version>${cas.version}</version>
</dependency>
```

Device registration records are kept inside a designated configurable multi-valued attribute as JSON blobs. The attribute values are parsed
to load, save, update or delete accounts. The content of each attribute value can be signed/encrypted if necessary. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-ldap).

### JPA

Device registrations may be kept inside a relational database instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn-jpa</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-jpa).

### Redis

Device registrations may be kept inside a Redis database instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn-redis</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-redis).

### DynamoDb

Device registrations may be kept inside a DynamoDb instance by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-webauthn-dynamodb</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#fido2-webauthn-dynamodb).
