---
layout: default
title: CAS - GeoTracking Authentication Requests
---

# GeoTracking Authentication Requests

Authentication requests can be mapped and translated to physical locations.

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#googlemaps-geotracking).

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#maxmind-geotracking).
