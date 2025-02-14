---
layout: default
title: CAS - Password Management
---

# Password Management - REST

Tasks such as locating user's email and security questions as well as management
and updating of the password are delegated to user-defined rest endpoints.

REST support is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-pm-rest</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#rest-password-management).

| Endpoint                  | Method    | Headers             | Expected Response
|---------------------------|-----------|------------------------------------------------------------------------
| Get Email Address         | `GET`     | `username`          | `200`. Email address in the body.
| Get Security Questions    | `GET`     | `username`          | `200`. Security questions map in the body.
| Update Password           | `POST`    | `username`, `password`, `oldPassword` | `200`. `true/false` in the body.
