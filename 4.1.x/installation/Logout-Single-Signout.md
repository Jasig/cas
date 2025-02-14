---
layout: default
title: CAS - Logout & Single Logout
---



# Logout and Single Logout (SLO)

There are potentially many active application sessions during a CAS single sign-on session, and the distinction between
logout and single logout is based on the number of sessions that are ended upon a _logout_ operation. The scope of logout
is determined by where the action takes place:

1. Application logout - ends a single application session
2. CAS logout - ends the CAS SSO session

Note that the logout action in each case has no effect on the other in the simple case. Ending an application session
does not end the CAS session and ending the CAS session does not affect application sessions. This is a common cause of
confusion for new users and deployers of an SSO system.

The single logout support in CAS attempts to reconcile the dispartity between CAS logout and application logout. When
CAS is configured for SLO, it attempts to send logout messages to every application that requested authentication to
CAS during the SSO session. While this is a best-effort process, in many cases it works well and provides a consistent
user experience by creating symmetry between login and logout.


## CAS Logout

Per the [CAS Protocol](../protocol/CAS-Protocol.html), the `/logout` endpoint is responsible for destroying the current SSO session. Upon logout, it may also be desirable to redirect back to a service. This is controlled via specifying the redirect link via the `service` parameter.

The redirect behavior is turned off by default, and is activated via the following setting in `cas.properties`:

{% highlight bash %}
# Specify whether CAS should redirect to the specified service parameter on /logout requests
# cas.logout.followServiceRedirects=false
{% endhighlight %}

The specified url must be registered in the service registry of CAS and enabled.

## Single Logout (SLO)
CAS is designed to support single sign out: it means that it will be able to invalidate client application sessions in addition to its own SSO session.  
Whenever a ticket-granting ticket is explicitly expired, the logout protocol will be initiated. Clients that do not support the logout protocol may notice extra requests in their access logs that appear not to do anything.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Single Logout is turned on by default.</p></div>

When a CAS session ends, it notifies each of the services that the SSO session is no longer valid, and that relying parties need to invalidate their own session.

This can happen in two ways:

1. CAS sends an HTTP POST message directly to the service ( _back channel_ communication): this is the traditional way of performing notification to the service.
2. CAS redirects (HTTP 302) to the service with a message and a _RelayState_ parameter (_front channel_ communication): This feature is inspired by SAML SLO, and is needed if the client application is composed of several servers and use session affinity. The expected behaviour of the CAS client is to invalidate the application web session and redirect back to the CAS server with the _RelayState_ parameter.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Front-channel SLO at this point is still experimental.</p></div>

## SLO Requests
The way the notification is done (_back_ or _front_ channel) is configured at a service level through the `logoutType` property. This value is set to `LogoutType.BACK_CHANNEL` by default. The message is delivered or the redirection is sent to the URL presented in the _service_ parameter of the original CAS protocol ticket request.

A sample SLO message:

{% highlight xml %}
<samlp:LogoutRequest
    xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
    xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
    ID="[RANDOM ID]"
    Version="2.0"
    IssueInstant="[CURRENT DATE/TIME]">
    <saml:NameID>@NOT_USED@</saml:NameID>
    <samlp:SessionIndex>[SESSION IDENTIFIER]</samlp:SessionIndex>
</samlp:LogoutRequest>
{% endhighlight %}

The session identifier is the CAS service ticket ID that was provided to the service when it originally authenticated
to CAS. The session identifier is used to correlate a CAS session with an application session; for example, the SLO
session identifier maps to a servlet session that can subsequently be destroyed to terminate the application session.

Logout protocol is effectively managed by the `LogoutManagerImpl` component:

{% highlight xml %}
<bean id="logoutManager" class="org.jasig.cas.logout.LogoutManagerImpl"
          c:servicesManager-ref="servicesManager"
          c:httpClient-ref="noRedirectHttpClient"
          c:logoutMessageBuilder-ref="logoutBuilder"
          p:singleLogoutCallbacksDisabled="${slo.callbacks.disabled:false}"
          p:asynchronous="${slo.callbacks.asynchronous:true}"/>
{% endhighlight %}


### Turning Off Single Logout
To disable single logout, adjust the following setting in `cas.properties` file:

{% highlight bash %}
# To turn off all back channel SLO requests set slo.disabled to true
# slo.callbacks.disabled=false
{% endhighlight %}

### Single Logout Per Service
Registered applications with CAS have the option to control single logout behavior individually via the [Service Managament](Service-Management.html) component. Each registered service in the service registry will include configuration that describes how to the logout request should be submitted. This behavior is controlled via the `logoutType` property which allows to specify whether the logout request should be submitted via back/front channel or turned off for this application.

Sample configuration follows:

{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "logoutType" : "BACK_CHANNEL"
}
{% endhighlight %}

### Service Endpoint for Logout Requests
By default, logout requests are submitted to the original service id. CAS has the option to submit such requests to a specific service endpoint that is different
from the original service id, and of course can be configured on a per-service level. This is useful in cases where the application that is integrated with CAS
does not exactly use a CAS client that supports intercepting such requests and instead, exposes a different endpoint for its logout operations.

To configure a service specific endpoint, try the following example:


{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "logoutType" : "BACK_CHANNEL",
  "logoutUrl" : "https://web.application.net/logout",
}
{% endhighlight %}

### Aynchronous SLO Messages
By default, backchannel logout messages are sent to endpoint in an asynchronous fashion. To allow synchronous messages, modify the following setting in `cas.properties`:

{% highlight bash %}
# To send callbacks to endpoints synchronously, set this to false
# slo.callbacks.asynchronous=true
{% endhighlight %}


### Ticket Registry Cleaner Behavior
Furthermore, the default behavior is to issue single sign out callbacks in response to a logout request or when a TGT is expired via expiration policy when a `TicketRegistryCleaner` runs.  If you are using ticket registry cleaner and you want to enable the single sign out callback only when CAS receives a logout request, you can configure your `TicketRegistryCleaner` as such:

{% highlight xml %}
<bean id="ticketRegistryCleaner"
  class="org.jasig.cas.ticket.registry.support.DefaultTicketRegistryCleaner"
      c:centralAuthenticationService-ref="centralAuthenticationService"
      c:ticketRegistry-ref="ticketRegistry"
      p:lock-ref="cleanerLock"/>
{% endhighlight %}

Note that certain ticket registries don't use or need a registry cleaner. For such registries, the option of having a ticker registry cleaner is entirely done away with and is currently not implemented. With that being absent, you will no longer receive automatic SLO callbacks upon TGT expiration. As such, the only thing that would reach back to the should then be explicit logout requests per the CAS protocol.


#### With `TicketRegistryCleaner`
1. Single Logout is turned on
2. The cleaner runs to detect the ticket that are automatically expired. It will query the tickets in the ticket registry, and will accumulate those that are expired.
3. For the collection of expired tickets, the cleaner will again ask them to "expire" which triggers the SLO callback to be issued.
4. The cleaner subsequently removes the TGT from the registry. Note that simply removing a ticket by itself from the registry does not issue the SLO callback. A ticket needs to be explicitly told one way or another, to "expire" itself:
    - If the ticket is already expired, the mechanism will issue the SLO callback.
    - If the ticket is not already expired, it will be marked as expired and the SLO callback will be issued.


#### Without `TicketRegistryCleaner`
1. Single Logout is turned on
2. There is no cleaner, so nothing runs in the background or otherwise to "expire" and delete tickets from the registry and thus, no SLO callbacks will be issued automatically.
2. A logout request is received by CAS
3. CAS will locate the TGT and will attempt to destroy the SSO session.
4. In destroying the ticket, CAS will:
    - Ask the ticket to expire itself, which will issue SLO callbacks.
    - Delete the ticket from the registry

## SSO Session vs. Application Session
In order to better understand the SSO session management of CAS and how it regards application sessions, one important note is to be first and foremost considered:

<div class="alert alert-info"><strong>CAS is NOT a session manager</strong><p>Application session is the responsibility of the application.</p></div>

CAS wants to maintain and control the SSO session in the form of
the `TicketGrantingTicket` and a TGT id which is shared between the
user-agent and the CAS server in the form of a secure cookie.

CAS is not an application session manager in that it is the
responsibility of the applications to maintain and control their own
application sessions.  Once authentication is completed, CAS is
typically out of the picture in terms of the application sessions. Therefore, the expiration policy
of the application session itself is entirely independent of CAS and may be loosely coordinated
and adjusted depending on the ideal user experience in the event that the application session expires.

In the event that Single Logout is not activated, typically, application may expose a logout endpoint in order to destroy the session and next, redirect
the agent to the CAS `logout` endpoint in order to completely destroy the SSO session as well.

Here's a brief diagram that demonstrates various application session configuration and interactions with CAS:

![](http://i.imgur.com/0XyuLgz.png)
