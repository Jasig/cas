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
</dependency>
```

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

### JWT Ticket Granting Tickets

Ticket-granting tickets created by the REST protocol may be issued as JWTs instead. Support is enabled by including the following in your overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-tokens</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To request a ticket-granting ticket as JWT next, ensure the `POST` request matches the following:

```bash
POST /cas/v1/tickets HTTP/1.0

username=battags&password=password&token=true&additionalParam1=paramvalue
```

The `token` parameter may either be passed as a request parameter or a request header. The body of the response will include the ticket-granting ticket as a JWT. Note that JWTs created are typically signed and encrypted by default with pre-generated keys. To control settings or to see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#jwt-tickets).


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

### JWT Service Tickets

Service tickets created by the REST protocol may be issued as JWTs instead. See [this guide](../installation/Configure-ServiceTicket-JWT.html) to learn more.

Support is enabled by including the following in your overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-tokens</artifactId>
    <version>${cas.version}</version>
</dependency>
```

Note that JWTs created are typically signed and encrypted by default with pre-generated keys. To control settings or to see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#jwt-tickets).

## Validate Service Ticket

Service ticket validation is handled through the [CAS Protocol](Cas-Protocol.html)
via any of the validation endpoints such as `/p3/serviceValidate`. 

```bash
GET /cas/p3/serviceValidate?service={service url}&ticket={service ticket}
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

Support is enabled by including the following in your overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-services</artifactId>
    <version>${cas.version}</version>
</dependency>
```

Invoke CAS to register applications into its own service registry. The REST
call must be authenticated as it requires a TGT from the CAS server, and furthermore,
the authenticated principal that submits the request must be authorized with a
pre-configured role name and value that is designated in the CAS configuration
via the CAS properties.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#rest-api).

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

## X.509 Authentication

The feature extends the CAS REST API communication model to non-interactive X.509 authentication
where REST credentials may be retrieved from a certificate embedded in the request rather than
the usual and default username/password.

This pattern may be of interest in cases where the internal network architecture hides
the CAS server from external users behind firewall or a messaging bus and
allows only trusted applications to connect to the CAS server.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>The X.509 feature over REST
provides a tremendously convenient target for claiming user identities. To securely use this feature, network
configuration <strong>MUST</strong> allow connections to the CAS server only from trusted hosts which in turn
have strict security limitations and logging.</p></div>

Support is enabled by including the following in your overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-x509</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Request a Ticket Granting Ticket

```bash
POST /cas/v1/tickets HTTP/1.0
cert=<ascii certificate>
```

### Successful Response

```bash
201 Created
Location: http://www.whatever.com/cas/v1/tickets/{TGT id}
```

## CAS REST Clients

In order to interact with the CAS REST API, a REST client must be used to submit credentials,
receive tickets and validate them. The following Java REST client is available
by [pac4j](https://github.com/pac4j/pac4j):

```java
final String casUrlPrefix = "http://localhost:8080/cas";
final CasRestAuthenticator authenticator = new CasRestAuthenticator(casUrlPrefix);
final CasRestFormClient client = new CasRestFormClient(authenticator);

// The request object must contain credentials used for CAS authentication
final WebContext webContext = new J2EContext(request, response);
final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
final CasCredentials casCreds = client.requestServiceTicket("<SERVICE_URL>", profile);
final CasProfile casProfile = client.validateServiceTicket("<SERVICE_URL>", casCreds);
client.destroyTicketGrantingTicket(context, profile);
```

## Throttling

To understand how to throttling works in CAS, please review [the available options](../installation/Configuring-Authentication-Throttling.html). By default, throttling REST requests is turned off. To activate this functionality, you will need to choose an appropriate throttler and instruct CAS to use it via CAS settings. To see the relevant options, [please review this guide](https://apereo.github.io/cas/development/installation/Configuration-Properties.html#rest-api).

## Swagger API

CAS REST API may be automatically integrated with Swagger. [See this guide](../integration/Swagger-Integration.html) for more info.
