---
layout: default
title: CAS - Configuring Authentication Events
---

# Authentication Events
CAS provides a facility for consuming and recording authentication events into persistent storage. This functionality is similar to the records
kept by the [Audit log](Audits.html) except that the functionality and storage format is controlled via CAS itself rather than the audit engine.
Additionally, while audit data may be used for reporting and monitoring, events stored into storage via this functionality may later be assessed
in a historical fashion to assess authentication requests, evaluate risk associated with them and take further action upon them. Events are primarily
designed to be consumed by the developer and subsequent CAS modules, while audit data is targeted at deployers for end-user functionality and reporting.

By default, no events are recorded by this functionality.

## Recorded Data
The following metadata is captured and recorded by the event machinery when enabled:

| Field                             | Description
|-----------------------------------|-----------------------------------------------------------------
| `principalId`                              | The principal id of the authenticated subject
| `timestamp`                                | Timestamp of this event
| `creationTime`                             | Timestamp of this authentication event
| `clientIpAddress`                          | Client IP address
| `serverIpAddress`                          | Server IP address
| `agent`                                    | User-Agent of the browser
| `geoLatitude`                              | Geo Latitude of authentication request's origin 
| `geoLongitude`                             | Geo Longitude of authentication request's origin
| `geoAccuracy`                              | Accuracy measure of the location
| `geoTimestamp`                             | Timestamp of the geo location request

## GeoLocation
CAS attempts to record the geolocation properties of the authentication requests, by allowing the browser to ask for user's consent. 
Should consent not be granted or geolocation not supported by the browser, CAS will ignore the geolocation data when it attempts to 
record the event. 

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Configuration
The following storage backends are available for consumption of events:

### MongoDb
Stores authentication events into a MongoDb NoSQL database.

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-events-mongo</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### JPA
Stores authentication events into a RDBMS.

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-events-jpa</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
