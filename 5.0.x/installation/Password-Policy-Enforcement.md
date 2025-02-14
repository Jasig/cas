---
layout: default
title: CAS - Password Policy Enforcement
---

# Password Policy Enforcement

Password policy enforcement attempts to:

- Detect a number of scenarios that would otherwise prevent user authentication based on user account status.
- Warn users whose account status is near a configurable expiration date and redirect the flow to an external
identity management system.

## LDAP

The below scenarios are by default considered errors preventing authentication in a generic manner through
the normal CAS login flow. LPPE intercepts the authentication flow, detecting the above standard error codes.
Error codes are then translated into proper messages in the CAS login flow and would allow the user to take proper action,
fully explaining the nature of the problem.

- `ACCOUNT_LOCKED`
- `ACCOUNT_DISABLED`
- `INVALID_LOGON_HOURS`
- `INVALID_WORKSTATION`
- `PASSWORD_MUST_CHANGE`
- `PASSWORD_EXPIRED`

The translation of LDAP errors into CAS workflow is all
handled by [ldaptive](http://www.ldaptive.org/docs/guide/authentication/accountstate).

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Account Expiration Notification

LPPE is also able to warn the user when the account is about to expire. The expiration policy is
determined through pre-configured Ldap attributes with default values in place.

## Password Management

<div class="alert alert-warning"><strong>Remember, Remember</strong><p>This feature tends more towards the experimental.</p></div>

CAS presents humble password management features. If authentication fails due to a rejected password policy, CAS is able to intercept
that request and allow the user to update the account password in place. The password management features of CAS are rather modest, and
alternatively should the functionality provide inadequate for your policy, you may always redirect CAS to use a separate and standalone
application that is fully in charge of managing the account password and associated flows.

CAS may also allow users to reset their passwords voluntarily. Those who have forgotten their account password
may receive a secure link with a time-based expiration policy at their registered email address. The link
will allow the user to provide answers to his/her pre-defined security questions, which if successfully done,
will allow the user to next reset their password and login again.

This functionality needs to be explicitly enabled in CAS settings. You may also specify a pattern for accepted passwords. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### LDAP

The updated password may be stored inside an LDAP server.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
