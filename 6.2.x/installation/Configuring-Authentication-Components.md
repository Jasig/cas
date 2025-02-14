---
layout: default
title: CAS - Configuring Authentication Components
category: Authentication
---

# Configuration

The CAS authentication process is primarily controlled by an authentication manager, which orchestrates a collection of authentication handlers.

## Authentication Manager

CAS ships with a single yet flexible authentication manager which performs authentication according to the following contract.

For any given credential the manager does the following:

1. Iterate over all configured authentication handlers.
2. Attempt to authenticate a credential if a handler supports it.
3. On success attempt to resolve a principal.
  1. Check whether a resolver is configured for the handler that authenticated the credential.
  2. If a suitable resolver is found, attempt to resolve the principal.
  3. If a suitable resolver is not found, use the principal resolved by the authentication handler.
4. Check whether the security policy (e.g. any, all) is satisfied.
  1. If security policy is met return immediately.
  2. Continue if security policy is not met.
5. After all credentials have been attempted check security policy again and throw `AuthenticationException` if not satisfied.

There is an implicit security policy that requires at least one handler to successfully authenticate a credential.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authentication-policy).

## Authentication Handlers

There are a variety of authentication handlers and methods supported by CAS. Use the menu to navigate around the site and choose.

<div class="alert alert-warning"><strong>Default Credentials</strong><p>To test the default authentication scheme in CAS,
use <strong>casuser</strong> and <strong>Mellon</strong> as the username and password respectively. These are automatically
configured via the static authencation handler, and <strong>MUST</strong> be removed from the configuration
prior to production rollouts.</p></div>

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#accept-users-authentication).

### Resolution Strategy

Authentication handlers are typically defined globally and then executed and tried by the authentication engine. 
The collection of handlers can be narrowed down using a selection criteria that defines an eligibility criteria
for the handler based on the current authentication transaction, provided credentials, service definition policy
and many other controls. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authentication-engine).

#### Per Registered Service

Each registered application in the registry may be assigned a set of identifiers/names for the required authentication 
handlers available and configured in CAS. These names can be used to enforce a service definition to only use the 
authentication strategy carrying that name when an authentication request is submitted to CAS.

Please [review this guide](../services/Configuring-Service-AuthN-Policy.html) to learn more.

#### Groovy Script

The global collection of authentication handlers can also pass through a Groovy script as a filter
to narrow down the list of candidates for the current transaction. The outline of the script may be designed as:

```groovy
def run(Object[] args) {
    def handlers = args[0]
    def transaction = args[1]
    def servicesManager = args[2]
    def logger = args[3]

    return handlers
}

def supports(Object[] args) {
    def handlers = args[0]
    def transaction = args[1]
    def servicesManager = args[2]
    def logger = args[3]
    true
}
```

The following parameters are passed to the script:

| Parameter        | Description
|------------------|--------------------------------------------------------------------------------------------
| `handlers`       | Collection of available and candidate `AuthenticationHandler`s.
| `transaction`    | The authentication transaction currently in progress.
| `servicesManager`  | The `ServicesManager` object responsible for locating service definitions attached to this transaction.
| `logger`           | The object responsible for issuing log messages such as `logger.info(...)`.

The outcome of the script should be the collection of selected handlers with the type `Set<AuthenticationHandler>`.

### Authentication Sequence

At runtime, CAS maintains a collection of authentication handlers/strategies that typically execute one after another. 
Each CAS module that presents a form of authentication strategy will simply insert itself into this collection at 
bootstrap time. At the end of this process, the result of all authentication transactions is collected and optionally processed by 
an authentication policy where success/failure of certain strategies/sources may be taken into account to fully satisfy the 
authentication requirements. The collection of authentication handlers tries to preserve order in a rather more deterministic way. 
The idea is that adopters can assign an `order` value to an authentication handler thereby explicitly positioning it in the 
collection and controlling its execution sequence.

### Naming Strategy

Each authentication handler in CAS can be named via CAS settings and if left undefined, the short name of 
the handler component itself is used (i.e. `LdapAuthenticationHandler`). The name itself can be any arbitrary string and typically is used
to identify and refer to the handler components in areas such as [required authentication for a service](../services/Configuring-Service-AuthN-Policy.html), etc.
In the event that multiple authentication handlers *of the same type* are defined, it is **RECOMMENDED** that each be given a unique name so as to avoid conflicts.
Authentication failures are typically collected in CAS by the name of each authentication handler. Leaving the name undefined will likely result in subsequent components
in the authentication chain overriding previous results.

## Authentication Policy

CAS presents a number of strategies for handling authentication security policies. Policies in general control the following:

1. Should the authentication chain be stopped after a certain kind of authentication failure?
2. Given multiple authentication handlers in a chain, what constitutes a successful authentication event?

Policies are typically activated after:

1. An authentication failure has occurred.
2. The authentication chain has finished execution.

Typical use cases of authentication policies may include:

1. Enforce a specific authentication's successful execution, for the entire authentication event to be considered successful.
2. Ensure a specific class of failure is not evident in the authentication chain's execution log.
3. Ensure that all authentication schemes in the chain are executed successfully, for the entire authentication event to be considered successful.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authentication-policy).

## Principal Resolution

Please [see this guide](Configuring-Principal-Resolution.html) more full details on principal resolution.

### Principal Transformation

Authentication handlers that generally deal with username-password credentials
can be configured to transform the user id prior to executing the authentication sequence.
Each authentication strategy in CAS provides settings to properly transform the principal.
Refer to the relevant settings for the authentication strategy at hand to learn more.

## Long Term Authentication

CAS has support for long term Ticket Granting Tickets, a feature that is also referred to as _"Remember Me"_
to extend the length of the SSO session beyond the typical configuration.
Please [see this guide](Configuring-LongTerm-Authentication.html) for more details.

## Proxy Authentication

Please [see this guide](Configuring-Proxy-Authentication.html) for more details.

## Multifactor Authentication (MFA)

Please [see this guide](../mfa/Configuring-Multifactor-Authentication.html) for more details.

## Login Throttling

CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
Please [see this guide](Configuring-Authentication-Throttling.html) for additional details on login throttling.

## SSO Session Cookie

A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session.
This cookie maintains login state for the client, and while it is valid, the client can present it to CAS in lieu of primary credentials.
Please [see this guide](Configuring-SSO-Session-Cookie.html) for additional details.
