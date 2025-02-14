---
layout: default
title: CAS - Long Term Authentication
---

# Long Term Authentication
This feature, also known as *Remember Me*, extends the length of the SSO session beyond the typical period of hours
such that users can go days or weeks without having to log in to CAS. See the
[security guide](../planning/Security-Guide.html)
for discussion of security concerns related to long term authentication.


## Policy and Deployment Considerations
While users can elect to establish a long term authentication session, the duration is established through
configuration as a matter of security policy. Deployers must determine the length of long term authentication sessions
by weighing convenience against security risks. The length of the long term authentication session is configured
(somewhat unhelpfully) in seconds, but the Google calculator provides a convenient converter:

[2 weeks in seconds](https://www.google.com/search?q=2+weeks+in+seconds&oq=2+weeks+in+seconds)

The use of long term authentication sessions dramatically increases the length of time ticket-granting tickets are
stored in the ticket registry. Loss of a ticket-granting ticket corresponding to a long-term SSO session would require
the user to re-authenticate to CAS. A security policy that requires that long term authentication sessions MUST NOT
be terminated prior to their natural expiration would mandate a ticket registry component that provides for durable storage, such as the `JpaTicketRegistry`.

## Configuration

Adjust your expiration policy so that remember-me authentication requests are
handled via a long-term timeout expiration policy, and other requests
are handled via the CAS default SSO session expiration policy.

```xml
<alias name="rememberMeDelegatingExpirationPolicy" alias="grantingTicketExpirationPolicy" />
<alias name="timeoutExpirationPolicy" alias="rememberMeExpirationPolicy" />
<alias name="ticketGrantingTicketExpirationPolicy" alias="sessionExpirationPolicy" />
```

The length of the long term authentication session is determined by:

```properties
# Inactivity Timeout Policy
# tgt.timeout.maxTimeToLiveInSeconds=1209600

# Default Expiration Policy
# tgt.maxTimeToLiveInSeconds=28800
# tgt.timeToKillInSeconds=7200

# tgc.remember.me.maxAge=1209600
```

This allows CAS to preserve a ticket expiration policy for 2 weeks for
long-term authentication requests, while using a maximum 8-hour expiration policy
with a sliding inactivity window of 2 hours for all other requests.

It also allows CAS to preserve the SSO session cookie for a maximum age of
2 weeks for long-term authentication requests.

### Webflow Configuration
Two sections of `login-webflow.xml` require changes:
1. Uncomment `RememberMeUsernamePasswordCredential` as the `credential` type.
2. Uncomment the binding property for `rememberMe`.

### User Interface Customization
Uncomment the `rememberMe` checkbox control in `casLoginView.jsp`.
