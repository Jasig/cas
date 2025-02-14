---
layout: default
title: CAS - CAS REST Protocol
---

# REST Protocol

The REST protocol allows one to model applications as users, programmatically acquiring
service tickets to authenticate to other applications. This means that other applications would be able
to use a CAS client  to accept Service Tickets rather than to rely upon another technology such as
client SSL certificates for application-to-application authentication of requests. This is achieved
by exposing a way to RESTfully obtain a Ticket Granting Ticket and then use that to obtain a Service Ticket.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>The REST endpoint may
 become a tremendously convenient target for brute force dictionary attacks on CAS server. Consider
 enabling throttling support to ensure brute force attacks are prevented upon authentication failures.</p></div>

## Configuration

Support is enabled by including the following to the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest</artifactId>
    <version>${cas.version}</version>
    <scope>runtime</scope>
</dependency>
```

REST support is currently provided internally by 
the [Spring framework](http://spring.io/guides/gs/rest-service/).

## Request a Ticket Granting Ticket

```bash
POST /cas/v1/tickets HTTP/1.0

username=battags&password=password&additionalParam1=paramvalue
```

### Successful Response

```bash
201 Created
Location: http://www.whatever.com/cas/v1/tickets/{TGT id}
```

### Unsuccessful Response

If incorrect credentials are sent, CAS will respond with a 400 Bad Request error
(will also respond for missing parameters, etc.). If you send a media type
it does not understand, it will send the 415 Unsupported Media Type.

## Request a Service Ticket

```bash
POST /cas/v1/tickets/{TGT id} HTTP/1.0

service={form encoded parameter for the service url}
```


### Successful Response

```bash
200 OK
ST-1-FFDFHDSJKHSDFJKSDHFJKRUEYREWUIFSD2132
```

### Unsuccessful Response

CAS will send a 400 Bad Request. If an incorrect media type is
sent, it will send the 415 Unsupported Media Type.

## Logout

Destroy the SSO session by removing the issued ticket: 

```bash
DELETE /cas/v1/tickets/TGT-fdsjfsdfjkalfewrihfdhfaie HTTP/1.0
```

## Ticket Status

Verify the status of an obtained ticket to make sure it still is valid
and has not yet expired.

```bash
GET /cas/v1/tickets/TGT-fdsjfsdfjkalfewrihfdhfaie HTTP/1.0
```

### Successful Response

```bash
200 OK
```

### Unsuccessful Response

```bash
404 NOT FOUND
```

## Add Service

Support is enabled by including the following in your maven overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-services</artifactId>
    <version>${cas.version}</version>
    <scope>runtime</scope>
</dependency>
```

Invoke CAS to register applications into its own service registry. The REST
call must be authenticated as it requires a TGT from the CAS server, and furthermore,
the authenticated principal that submits the request must be authorized with a
pre-configured role name and value that is designated in the CAS configuration
via the CAS properties.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

```bash
POST /cas/v1/services/add/{TGT id} HTTP/1.0
serviceId=svcid&name=svcname&description=svcdesc&evaluationOrder=1234&enabled=true&ssoEnabled=true
```

### Successful Response

If the request is successful, the returned value in the response would be
the generated identifier of the new service.

```bash
200 OK
5463544213
```

## CAS REST Clients

In order to interact with the CAS REST API, a REST client must be used to submit credentials,
receive tickets and validate them. The following Java REST client is available
by [pac4j](https://github.com/pac4j/pac4j):

```java
String casUrlPrefix = "http://localhost:8080/cas";
CasRestAuthenticator authenticator = new CasRestAuthenticator(casUrlPrefix);
CasRestFormClient client = new CasRestFormClient(authenticator);

// The request object must contain the CAS credentials
final WebContext webContext = new J2EContext(request, response);
final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
final CasCredentials casCreds = client.requestServiceTicket("<SERVICE_URL>", profile);
final CasProfile casProfile = client.validateServiceTicket("<SERVICE_URL>", casCreds);
client.destroyTicketGrantingTicket(context, profile);
```

## Throttling

To understand how to throttling works in CAS, 
please review [the available options](../installation/Configuring-Authentication-Throttling.html).

By default, throttling REST requests is turned off.
