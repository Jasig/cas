---
layout: default
title: CAS - Adaptive Authentication
---

# Adaptive Authentication

Adaptive authentication in CAS allows you to accept or reject authentication requests based on certain characteristics
of the client browser and/or device. When configured, you are provided with options to block authentication requests
from certain locations submitted by certain browser agents. For instance, you may consider authentication requests submitted
from `London, UK` to be considered suspicious, or you may want to block requests that are submitted from Internet Explorer, etc.

## Google Maps

Uses the [Google Maps Geocoding API](https://developers.google.com/maps/documentation/geocoding/start) to translate
authentication requests into a geo-location.

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-geolocation-googlemaps</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Maxmind

Uses [Maxmind](https://www.maxmind.com/en/home) to translate
authentication requests into a geo-location.

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-geolocation-maxmind</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
