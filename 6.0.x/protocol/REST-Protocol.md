---
layout: default
title: CAS - CAS REST Protocol
category: Protocols
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
'Content-type': 'Application/x-www-form-urlencoded'
username=battags&password=password&additionalParam1=paramvalue
```

You may also specify a `service` parameter to verify whether the authenticated user may be allowed to access the given service.

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

The `token` parameter may either be passed as a request parameter or a request header. The body of the response will include the ticket-granting ticket as a JWT. Note that JWTs created are typically signed and encrypted by default with pre-generated keys. To control settings or to see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#jwt-tickets).

## Authenticate Credentials

Similar to asking for ticket-granting tickets, this endpoint allows one to only verify the validity of provided credentials as they are extracted from the request body:

```bash
POST /cas/v1/users HTTP/1.0

username=battags&password=password
```

You may also specify a `service` parameter to verify whether the authenticated user may be allowed to access the given service. While the above example shows `username` and `password` as the provided credentials, you are practically allowed to provide multiple sets and different types of credentials provided CAS is equipped to extract and recognize those from the request body. See [this  for more info](#multiple-credentials).

A successful response will produce a `200 OK` status code along with a JSON representation of the authentication result, which may include the authentication object, authenticated principal along with any captured attributes and/or metadata fetched for the authenticated user.

## Request a Service Ticket

The below snippets show one might request a service ticket using the semantics of the CAS protocol:

```bash
POST /cas/v1/tickets/{TGT id} HTTP/1.0

service={form encoded parameter for the service url}
```

You may also specify a `renew` parameter to obtain a service ticket that can be accepted by a service that only wants tickets issued from the presentation of the user's primary credentials. In that case, user credentials have to be passed in the request, for example, as `username` and `password` parameters.

```bash
POST /cas/v1/tickets/{TGT id} HTTP/1.0

service={form encoded parameter for the service url}&renew=true&username=battags&password=password
```

You may also submit service ticket requests using the semantics [SAML1 protocol](SAML-Protocol.html).

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

Note that JWTs created are typically signed and encrypted by default with pre-generated keys. To control settings or to see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#jwt-tickets).

## Validate Service Ticket

Service ticket validation is handled through the [CAS Protocol](CAS-Protocol.html)
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

Invoke CAS to register applications into its own service registry. The REST call must be authenticated using basic authentication where credentials are authenticated and accepted by the existing CAS authentication strategy, and furthermore the authenticated principal must be authorized with a pre-configured role/attribute name and value that is designated in the CAS configuration via the CAS properties. The body of the request must be the service definition that shall be registered in JSON format and of course, CAS must be configured to accept the particular service type defined in the body. The accepted media type for this request is `application/json`.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#rest-api).

```bash
POST /cas/v1/services HTTP/1.0
```

...where body of the request may be:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "...",
  "name" : "...",
  "id" : 1,
  "description": "..."
}
```

A successful response will produce a `200` status code in return.

## X.509 Authentication

The feature extends the CAS REST API communication model to non-interactive X.509 authentication
where REST credentials may be retrieved from a certificate embedded in the request rather than
the usual and default username/password.

This pattern may be of interest in cases where the internal network architecture hides
the CAS server from external users behind firewall, reverse proxy, or a messaging bus and
allows only trusted applications to connect directly to the CAS server.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>The X.509 feature over REST
using a body parameter or a http header provides a tremendously convenient target for claiming
user identities or obtaining TGTs without proof of private key ownership.
To securely use this feature, network configuration <strong>MUST</strong> allow connections
to the CAS server only from trusted hosts which in turn have strict security limitations and
logging.
It is also recommended to make sure that the body parameter or the http header can only come
from trusted hosts and not from the original authenticating client.</p></div>

It is also possible to let the servlet container validate the TLS client key / X.509 certificate
during TLS handshake, and have CAS server retrieve the certificate from the container.

Support is enabled by including the following in your overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-rest-x509</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### Request a Ticket Granting Ticket (Proxy TLS Client Authentication using a body parameter)

```bash
POST /cas/v1/tickets HTTP/1.0
cert=<ascii certificate>
```

### Request a Ticket Granting Ticket (Proxy TLS Client Authentication using a http header)

The cas server should be configured for X509 authentication on the login page for
this to function properly.

### Request a Ticket Granting Ticket (TLS Client Authentication from the servlet container)

The cas server should be configured for X509 authentication on the login page for
this to function properly.

#### Successful Response

```bash
201 Created
Location: http://www.whatever.com/cas/v1/tickets/{TGT id}
```

## Multiple Credentials

The CAS REST API machinery has the ability to use multiple *credential extractors* that are tasked with analyzing the request body in order to fetch credentials and pass them along. While by default expected credentials that may be extracted are based on username/password, additional modules automatically lend themselves into this design and inject their opinionated credential extractor into the REST engine automatically so that the final collection of credentials may be used for issuing tickets, etc. This is, in a sense, how the [X.509 authentication](#x509-authentication) is integrated with the CAS REST Protocol. 

This indicates that you may pass along multiple credentials to the REST protocol in the request body and so long as CAS is configured to understand and extract those credentials and the authentication machinery is configured to also execute and validate those credentials. For instance, you may deliver a use case where two sets of credentials in form of username/password and OTP are provided to the REST protocol and CAS would then attempt to authenticate both credentials and produce a response on a successful validation, assuming that authentication strategies for username/password and OTP are properly configured in CAS.

## CAS REST Clients

In order to interact with the CAS REST API, a REST client must be used to submit credentials,
receive tickets and validate them. The following Java REST client is available
by [pac4j](https://github.com/pac4j/pac4j):

```java
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.HttpAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;
import java.util.Set;

public class RestTestClient {

    public static void main(String[] args ) throws HttpAction {
        final String casUrlPrefix = "http://localhost:8080/cas";
        String username = args[0];
        String password = args[1];
        String serviceUrl = args[2];
        CasConfiguration casConfiguration = new CasConfiguration(casUrlPrefix);
        final CasRestAuthenticator authenticator = new CasRestAuthenticator(casConfiguration);
        final CasRestFormClient client = new CasRestFormClient(casConfiguration,"username","password");
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        final WebContext webContext = new J2EContext(request, response);
        casConfiguration.init(webContext);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,password,"testclient");
        CasRestAuthenticator restAuthenticator = new CasRestAuthenticator(casConfiguration);
        // authenticate with credentials (validate credentials)
        restAuthenticator.validate(credentials, webContext);
        final CasRestProfile profile = (CasRestProfile) credentials.getUserProfile();
        // get service ticket
        final TokenCredentials casCredentials = client.requestServiceTicket(serviceUrl, profile, webContext);
        // validate service ticket
        final CasProfile casProfile = client.validateServiceTicket(serviceUrl, casCredentials, webContext);
        Map<String,Object> attributes = casProfile.getAttributes();
        Set<Map.Entry<String,Object>> mapEntries = attributes.entrySet();
        for (Map.Entry entry : mapEntries) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        client.destroyTicketGrantingTicket(profile,webContext);
    }
}


```

## Throttling

To understand how to throttling works in CAS, 
please review [the available options](../installation/Configuring-Authentication-Throttling.html). 
By default, throttling REST requests is turned off. 
To activate this functionality, you will need to choose an appropriate throttler and activate it by declaring the relevant module. 
The same throttling mechanism that handles the usual CAS server endpoints for authentication
and ticket validation, etc is then activated for the REST endpoints that are supported for throttling. 

To see the relevant options, [please review this guide](https://apereo.github.io/cas/development/configuration/Configuration-Properties.html#rest-api).

## Swagger API

CAS REST API may be automatically integrated with Swagger. [See this guide](../integration/Swagger-Integration.html) for more info.
