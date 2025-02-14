---
layout: default
title: CAS - Configuring Authentication Components
---

# Configuration

The CAS authentication process is primarily controlled by an authentication manager, which orchestrates a collection of authentication handlers.

## Authentication Manager
CAS ships with a single yet flexible authentication manager which performs authentication according to the following contract.

For each given credential do the following:

1. Iterate over all configured authentication handlers.
2. Attempt to authenticate a credential if a handler supports it.
3. On success attempt to resolve a principal.
  1. Check whether a resolver is configured for the handler that authenticated the credential.
  2. If a suitable resolver is found, attempt to resolve the principal.
  3. If a suitable resolver is not found, use the principal resolved by the authentication handler.
4. Check whether the security policy (e.g. any, all) is satisfied.
  1. If security policy is met return immediately.
  2. Continue if security policy is not met.
5. After all credentials have been attempted check security policy again and throw `AuthenticationException`
if not satisfied.

There is an implicit security policy that requires at least one handler to successfully authenticate a credential.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Authentication Handlers

There are a variety of authentication handlers and schemes supported by CAS. Use the menu to navigate around the site and choose. 

<div class="alert alert-warning"><strong>Default Credentials</strong><p>To test the default authentication scheme in CAS,
use <strong>casuser</strong> and <strong>Mellon</strong> as the username and password respectively. These are automatically
configured via the static authencation handler, and <strong>MUST</strong> be removed from the configuration 
prior to production rollouts.</p></div>

## Principal Resolution
Please [see this guide](Configuring-Principal-Resolution.html) more full details on principal resolution.

### Principal Transformation

Authentication handlers that generally deal with username-password credentials
can be configured to transform the user id prior to executing the authentication sequence.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Long Term Authentication

CAS has support for long term Ticket Granting Tickets, a feature that is also referred to as _"Remember Me"_
to extends the length of the SSO session beyond the typical configuration.
Please [see this guide](Configuring-LongTerm-Authentication.html) for more details.

## Proxy Authentication

Please [see this guide](Configuring-Proxy-Authentication.html) for more details.

## Multi-factor Authentication (MFA)

Please [see this guide](Configuring-Multifactor-Authentication.html) for more details.

## Login Throttling

CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
Please [see this guide](Configuring-Authentication-Throttling.html) for additional details on login throttling.

## SSO Session Cookie

A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session.
This cookie maintains login state for the client, and while it is valid, the client can present it to CAS in lieu of primary credentials.
Please [see this guide](Configuring-SSO-Session-Cookie.html) for additional details.
