---
layout: default
title: CAS - Configuring Ticket Expiration Policy Components
---


# Ticket Expiration Policies

CAS supports a pluggable and extensible policy framework to control the expiration policy of ticket-granting 
tickets (`TGT`), proxy-granting tickets (`PGT`), service tickets (`ST`) and proxy tickets (`PT`).

<div class="alert alert-info"><strong>Policies Are Not Ticket-Specific</strong><p>Ticket expiration policies are not specific to a 
particular kind of ticket, so it is possible to apply a policy intended for service tickets to ticket-granting tickets, although 
it may make little sense to do so.</p></div>

Unless noted otherwise, all ticket expiration policy values should be specified in seconds as the unit of measure.

## Ticket-Granting Ticket Policies

TGT expiration policy governs the time span during which an authenticated user may grant STs with a valid (non-expired) TGT without
having to re-authenticate. An attempt to grant a ST with an expired TGT would require the user to re-authenticate
to obtain a new (valid) TGT.


### Default

This is default option, which provides a hard-time out as well as a sliding window.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Timeout

The expiration policy applied to TGTs provides for most-recently-used expiration policy, similar to a Web server session timeout. 
For example, a 2-hour time span with this policy in effect would require a TGT to be used every 2 hours or less, otherwise 
it would be marked as expired.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Hard Timeout

The hard timeout policy provides for finite ticket lifetime as measured from the time of creation. For example, a 4-hour time span 
for this policy means that a ticket created at 1PM may be used up until 5PM; subsequent attempts to use it will mark it expired 
and the user will be forced to re-authenticate.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Throttled

The throttled timeout policy extends the TimeoutExpirationPolicy with the concept of throttling where a ticket may be used at 
most every N seconds. This policy was designed to thwart denial of service conditions where a rogue or misconfigured client 
attempts to consume CAS server resources by requesting high volumes of service tickets in a short time.


To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Never

The never expires policy allows tickets to exist indefinitely.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Use of this policy has significant consequences to overall 
security policy and should be enabled only after thorough review by a qualified security team. There are also implications to 
server resource usage for the ticket registries backed by filesystem storage. Since disk storage for tickets can never be reclaimed 
for those registries with this policy in effect, use of this policy with those ticket registry implementations 
is strongly discouraged.</p></div>

## Service Ticket Policies

ST expiration policy governs the time span during which an authenticated user may attempt to validate an ST.

### Default

This is the default policy applied to service tickets where a ticket is expired after a fixed number of uses or after a maximum 
period of inactivity elapses. This is the default and only option.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Proxy Ticket Policies

PT expiration policy governs the time span during which an authenticated user may attempt to validate an PT.

### Default

This is the default policy applied to proxy tickets where a ticket is expired after a fixed number of uses or after a maximum 
period of inactivity elapses. This is default and only option.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Proxy-Granting Ticket Policies

PGT expiration policy governs the time span during which CAS may grant PTs with a valid (non-expired) PGT.
At this time, the expiration policy assigned to proxy-granting tickets is controlled by the same policy
assigned to ticket-granting tickets.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

