---
layout: default
title: CAS - REST Authentication
---

# REST Authentication

<div class="alert alert-warning"><strong>Be Careful</strong><p>This documentation describes
how to delegate and submit authentication requests to a remote REST endpoint. It has nothing
to do with the native CAS REST API, whose configuration and caveats are
<a href="../protocol/REST-Protocol.html">documented here</a>.</p></div>

REST authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-authentication</artifactId>
    <version>${cas.version}</version>
</dependency>
```

This allows the CAS server to reach to a remote REST endpoint via a `POST` for verification of credentials.
Credentials are passed via an `Authorization` header whose value is `Basic XYZ` where XYZ is a
Base64 encoded version of the credentials.

The response that is returned must be accompanied by a `200`
status code where the body should contain `id` and `attributes` fields, the latter being optional,
which represent the authenticated principal for CAS:

```json
{"@c":".SimplePrincipal","id":"casuser","attributes":{}}
```

Expected responses from the REST endpoint are mapped to CAS as such:

| Code                   | Result
|------------------------|---------------------------------------------
| `200`          | Successful authentication.
| `403`          | Produces a `AccountDisabledException`
| `404`          | Produces a `AccountNotFoundException`
| `423`          | Produces a `AccountLockedException`
| `412`          | Produces a `AccountExpiredException`
| `428`          | Produces a `AccountPasswordMustChangeException`
| Other          | Produces a `FailedLoginException`

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#rest-authentication).
