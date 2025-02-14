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
by weighing convenience against security risks. 

The use of long term authentication sessions dramatically increases the length of time ticket-granting tickets are
stored in the ticket registry. Loss of a ticket-granting ticket corresponding to a long-term SSO session would require
the user to re-authenticate to CAS. A security policy that requires that long term authentication sessions MUST NOT
be terminated prior to their natural expiration would mandate a ticket 
registry component that provides for durable storage, such as the [JPA Ticket Registry](JPA-Ticket-Registry.html).

## Configuration

Adjust your expiration policy so that remember-me authentication requests are
handled via a long-term timeout expiration policy, and other requests
are handled via the CAS default SSO session expiration policy.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ticket-granting-cookie).
