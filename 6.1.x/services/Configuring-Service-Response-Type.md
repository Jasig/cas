---
layout: default
title: CAS - Configuring Service Response Type
category: Services
---

# Configuring Service Response Type

By default, authentication requests are handled with a browser redirect (i.e. `302`) back to the calling application 
with the relevant parameters built into the url. This behavior can be optionally adjusted on a per-service basis
to dictate other options when responding to services.

A sample JSON file follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "sample service",
  "id" : 100,
  "responseType": "HEADER"
}
```

Accepted response types are as follows:

| Parameter             | Description
|------------------|--------------------------------------------------------------------------------------
| `REDIRECT` | This is the default option, where a browser `302` redirect navigates the user back to the calling application.
| `POST`     | Same as above, except that parameters are `POST`ed back to the calling application.
| `HEADER`   | Parameters constructed for this authentication request are inserted into the HTTP response as headers
