---
layout: default
title: CAS - Multifactor Authentication
---

# Multifactor Authentication (MFA)

CAS provides a framework for multi-factor authentication (MFA). The design philosophy for MFA support follows from
the observation that institutional security policies with respect to MFA vary dramatically. We provide first class
API support for authenticating multiple credentials and a policy framework around authentication. The components
could be extended in a straightforward fashion to provide higher-level behaviors such as Webflow logic to assist,
for example, a credential upgrade scenario where a SSO session is started by a weaker credential but a particular
service demands reauthentication with a stronger credential.

The authentication subsystem in CAS natively supports handling multiple credentials. While the default login form
and Webflow tier are designed for the simple case of accepting a single credential, all core API components that
interface with the authentication subsystem accept one or more credentials to authenticate.

Beyond support for multiple credentials, an extensible policy framework is available to apply policy arbitrarily.
CAS ships with support for applying policy in the following areas:

* Credential authentication success and failure.
* Service-specific authentication requirements.

## `PolicyBasedAuthenticationManager`
CAS ships with an authentication manager component that is fundamentally MFA-aware. It supports a number of
policies, discussed above, that could facilitate a simple MFA design; for example, where multiple credentials are
invariably required to start a CAS SSO session.

## `ContextualAuthenticationPolicy`
Strategy pattern component for applying security policy in an arbitrary context. These components are assumed to be
stateful once created.

## `ContextualAuthenticationPolicyFactory`
Factory class for creating stateful instances of `ContextualAuthenticationPolicy` that apply to a particular context.

## `AcceptAnyAuthenticationPolicyFactory`
Simple factory class that produces contextual security policies that always pass. This component is configured by
default in some cases to provide backward compatibility with CAS 3.x.

## `RequiredHandlerAuthenticationPolicyFactory`
Factory that produces policy objects based on the security context of the service requesting a ticket. In particular the security
context is based on the required authentication handlers that must have successfully validated credentials in order to access
the service. A clarifying example is helpful; assume the following authentication components are defined in `deployerConfigContext.xml`:

{% highlight xml %}
<bean id="ldapHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:name="ldapHandler">
      <!-- Details elided for simplicity -->
</bean>

<bean id="oneTimePasswordHandler"
      class="com.example.cas.authentication.CustomOTPAuthenticationHandler"
      p:name="oneTimePasswordHandler" />

<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
  <constructor-arg>
    <map>
      <entry key-ref="passwordHandler" value="#{ null }" />
      <entry key-ref="oneTimePasswordHandler" value="#{ null }" />
    </map>
  </constructor-arg>
</bean>
{% endhighlight %}

Assume also the following beans are defined in `applicationContext.xml`:
{% highlight xml %}
<bean id="centralAuthenticationService"
      class="org.jasig.cas.CentralAuthenticationServiceImpl"
      c:authenticationManager-ref="authenticationManager"
      c:logoutManager-ref="logoutManager"
      c:servicesManager-ref="servicesManager"
      c:serviceTicketExpirationPolicy-ref="neverExpiresExpirationPolicy"
      c:serviceTicketRegistry-ref="ticketRegistry"
      c:ticketGrantingTicketExpirationPolicy-ref="neverExpiresExpirationPolicy"
      c:ticketGrantingTicketUniqueTicketIdGenerator-ref="uniqueTicketIdGenerator"
      c:ticketRegistry-ref="ticketRegistry"
      c:uniqueTicketIdGeneratorsForService-ref="uniqueTicketIdGeneratorsForService"
      p:serviceContextAuthenticationPolicyFactory-ref="casAuthenticationPolicy" />

<bean id="casAuthenticationPolicy"
      class="org.jasig.cas.authentication.RequiredHandlerAuthenticationPolicyFactory" />
{% endhighlight %}

With the above configuration in mind, the [service management facility](Service-Management.html)
may now be leveraged to register services that require specific kinds of credentials be used to access the service.
The kinds of required credentials are specified by naming the authentication handlers that accept them, for example,
`ldapHandler` and `oneTimePasswordHandler`. Thus a service could be registered that imposes security constraints like
the following:

_Only permit users with SSO sessions created from both a username/password and OTP token to access this service._
