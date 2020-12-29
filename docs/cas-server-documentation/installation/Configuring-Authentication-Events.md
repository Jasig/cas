---
layout: default
title: CAS - Configuring Authentication Events
category: Authentication
---
{% include variables.html %}

# Authentication Events

CAS provides a facility for consuming and recording authentication events into 
persistent storage. This functionality is similar to the records
kept by the [Audit log](Audits.html) except that the functionality and storage 
format is controlled via CAS itself rather than the audit engine.
Additionally, while audit data may be used for reporting and monitoring, events 
stored into storage via this functionality may later be assessed
in a historical fashion to assess authentication requests, evaluate risk 
associated with them and take further action upon them. Events are primarily
designed to be consumed by the developer and subsequent CAS modules, while 
audit data is targeted at deployers for end-user functionality and reporting.

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-core-events" %}

{% include {{ version }}/events-configuration.md %}

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

CAS attempts to record the geolocation properties of the authentication requests, by allowing 
the browser to ask for user's consent.  Should consent not be granted or geolocation 
not supported by the browser, CAS will ignore the geolocation data when it attempts to
record the event. To learn more, please [review this guide](GeoTracking-Authentication-Requests.html).

## Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                 | Description
|--------------------------|------------------------------------------------
| `events`                 | Provides a JSON representation of all CAS recorded events.

## Configuration

The following storage backends are available for consumption of events.

### MongoDb

Stores authentication events into a MongoDb NoSQL database.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-mongo" %}

{% include {{ version }}/mongodb-configuration.md configKey="cas.events" %}

### DynamoDb

Stores authentication events into a DynamoDb database.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-dynamodb" %}

{% include {{ version }}/dynamodb-configuration.md configKey="cas.events" %}

{% include {{ version }}/dynamodb-events-configuration.md %}

### CouchDb

Stores authentication events inside a CouchDb instance.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-couchdb" %}

{% include {{ version }}/couchdb-configuration.md configKey="cas.events.couch-db" %}

### JPA

Stores authentication events into a RDBMS.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-jpa" %}

{% include {{ version }}/rdbms-configuration.md configKey="cas.events.jpa" %}

### InfluxDb

Stores authentication events inside an InfluxDb instance.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-influxdb" %}

{% include {{ version }}/influxdb-configuration.md configKey="cas.events.influx-db" %}

### Memory

Stores authentication events into memory for a very limited time period.

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-events-memory" %}
