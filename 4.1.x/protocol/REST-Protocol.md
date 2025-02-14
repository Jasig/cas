---
layout: default
title: CAS - CAS REST Protocol
---

# REST Protocol
The REST protocol allows one to model applications as users, programmatically acquiring service tickets to authenticate to other applications. This means that other applications would be able to use a CAS client  to accept Service Tickets rather than to rely upon another technology such as client SSL certificates for application-to-application authentication of requests. This is achieved by exposing a way to RESTfully obtain a Ticket Granting Ticket and then use that to obtain a Service Ticket.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>The REST endpoint may become a tremendously convenient target for brute force dictionary attacks on CAS server. Enable support only soberly and with due consideration of security aspects.</p></div>

<div class="alert alert-info"><strong>Restlet Module</strong><p>If you are looking for the Restlet implementation of the CAS REST API, you will find the instructions <a href="REST-Protocol-Deprecated.html">here in this document</a>.</p></div>

# Components
By default the CAS REST API is configured to add routing for the tickets. It also defines the resources that will resolve the URLs. The `TicketResource` defined by default (which can be extended) accepts username/password.

Support is enabled by including the following in your `pom.xml` file:


{% highlight xml %}
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-rest</artifactId>
    <version>${cas.version}</version>
    <scope>runtime</scope>
</dependency>
{% endhighlight %}

REST support is currently provided internally by the [Spring framework](http://spring.io/guides/gs/rest-service/‎).


# Configuration
To turn on the protocol, add the following to the `web.xml`:

{% highlight xml %}
<servlet-mapping>
    <servlet-name>cas</servlet-name>
    <url-pattern>/v1/*</url-pattern>
</servlet-mapping>
{% endhighlight %}


...or delete the `web.xml` in the overlay altogether if there are no other customizations there as this mapping is provided by CAS' webapp module's `web.xml` out of the box.

Please note that if there are local customizations in overlay's `web.xml`, the following `contextConfigLocation` `<context-param>` must also be added in order to enable the new REST module: `classpath*:/META-INF/spring/*.xml`. So the entire context-param block would look like this:

{% highlight xml %}
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>
      /WEB-INF/spring-configuration/*.xml
      /WEB-INF/deployerConfigContext.xml
      classpath*:/META-INF/spring/*.xml
    </param-value>
</context-param>
{% endhighlight %}

# Protocol

## Request a Ticket Granting Ticket

### Sample Request
{% highlight bash %}
POST /cas/v1/tickets HTTP/1.0
 
username=battags&password=password&additionalParam1=paramvalue
{% endhighlight %}


### Sample Response


#### Successful Response
{% highlight bash %}
201 Created
Location: http://www.whatever.com/cas/v1/tickets/{TGT id}
{% endhighlight %}


#### Unsuccessful Response
If incorrect credentials are sent, CAS will respond with a 400 Bad Request error (will also respond for missing parameters, etc.). If you send a media type it does not understand, it will send the 415 Unsupported Media Type.


## Request a Service Ticket

### Sample Request
{% highlight bash %}
POST /cas/v1/tickets/{TGT id} HTTP/1.0
 
service={form encoded parameter for the service url}
{% endhighlight %}

### Sample Response

#### Successful Response
{% highlight bash %}
200 OK
ST-1-FFDFHDSJKHSDFJKSDHFJKRUEYREWUIFSD2132
{% endhighlight %}
#### Unsuccessful Response
CAS will send a 400 Bad Request. If an incorrect media type is sent, it will send the 415 Unsupported Media Type.


## Logout
{% highlight bash %}
DELETE /cas/v1/tickets/TGT-fdsjfsdfjkalfewrihfdhfaie HTTP/1.0
{% endhighlight %}
