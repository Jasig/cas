---
layout: default
title: CAS - FIDO2 WebAuthn Multifactor Authentication
category: Multifactor Authentication
---

{% include variables.html %}

# FIDO2 WebAuthn Multifactor Authentication

[WebAuthn](https://webauthn.io/) is an API that makes it very easy 
for a relying party, such as a web service, to integrate strong 
authentication into applications using support built in to all leading browsers and platforms. This means 
that web services can now easily offer their users strong authentication with a choice of authenticators 
such as security keys or built-in platform authenticators such as biometric readers.

Support is enabled by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn" %}

{% include {{ version }}/webauthn-configuration.md %}

You may also need to declare the following repository in
your CAS overlay to be able to resolve dependencies:

```groovy       
repositories {
    maven { 
        mavenContent { releasesOnly() }
        url "https://dl.bintray.com/apereocas/webauthn-cas" 
    }
}
```

## Primary Authentication

It is possible to allow WebAuthN to act as a standalone authentication strategy for primary authentication. Using this approach,
user accounts and FIDO2-enabled devices that have already registered with 
CAS are given the option to login using their FIDO2-enabled
device by only providing the username linked to their registration record for a passwordless authentication experience.

Device registration can occur out of band using available CAS APIs, or by allowing users to pass through the registration flow
as part of the typical multifactor authentication. See below for details on device registration.


## Registration

Device registration flows are baked into CAS automatically. A background 
*cleaner* process is also automatically scheduled to scan the 
repository periodically and remove expired device registration records 
based on configured parameters. In the default setting, devices
expire after a fixed period since a user registered their device. If you deploy U2F
MFA for a setup where tokens are centrally distributed and revoked, 
you may want to extend the internal.

{% include {{ version }}/job-scheduling-configuration.md configKey="cas.authn.mfa.web-authn.cleaner" %}

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

{% include {{ version }}/json-webauthn-configuration.md %}

### MongoDb

Device registrations may be kept inside a MongoDb instance by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-mongo" %}

{% include {{ version }}/mongodb-configuration.md configKey="cas.authn.mfa.web-authn" %}

### LDAP

Device registrations may be kept inside LDAP directories by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-ldap" %}

Device registration records are kept inside a designated configurable multi-valued attribute as JSON blobs. The attribute values are parsed
to load, save, update or delete accounts. The content of each attribute value can be signed/encrypted if necessary. 

{% include {{ version }}/ldap-webauthn-configuration.md %}

### JPA

Device registrations may be kept inside a relational database instance by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-jpa" %}

{% include {{ version }}/rdbms-configuration.md configKey="cas.authn.mfa.web-authn.jpa" %}

### Redis

Device registrations may be kept inside a Redis database instance by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-redis" %}

{% include {{ version }}/redis-configuration.md configKey="cas.authn.mfa.web-authn" %}

### DynamoDb

Device registrations may be kept inside a DynamoDb instance by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-dynamodb" %}

{% include {{ version }}/dynamodb-configuration.md configKey="cas.authn.mfa.web-authn" %}

### REST

Device registrations may be managed using an external REST API by including the following module in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-webauthn-rest" %}

The following parameters are passed:

| Operation        | Parameters      | Description      | Result
|------------------|-----------------|------------------|-----------------------------------------------------
| `GET`            | N/A             | Retrieve all records.     | `200` status code; Collection of JSON records in the body.
| `GET`            | `username`      | Retrieve all records for user.  | `200` status code Collection of JSON records in the body.
| `POST`           | Collection of records as JSON body | Store/Update registered devices. | `200`.

{% include {{ version }}/rest-configuration.md configKey="cas.authn.mfa.web-authn.rest" %}
