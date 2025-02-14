---
layout: default
title: CAS - Configuring Ticket Expiration Policy Components
---


## Ticket Expiration Policies
CAS supports a pluggable and extensible policy framework to control the expiration policy of ticket-granting 
tickets (TGT) and service tickets (ST).

<div class="alert alert-info"><strong>Policies Are Not Ticket-Specific</strong><p>Ticket expiration policies are not specific to a 
particular kind of ticket, so it is possible to apply a policy intended for service tickets to ticket-granting tickets, although 
it may make little sense to do so.</p></div>


### Ticket-Granting Ticket Policies
TGT expiration policy governs the time span during which an authenticated user may grant STs with a valid (non-expired) TGT without
having to re-authenticate. An attempt to grant a ST with an expired TGT would require the user to re-authenticate
to obtain a new (valid) TGT.

#### `TicketGrantingTicketExpirationPolicy`
This is default option, which provides a hard-time out as well as a sliding window.

```xml
<alias name="ticketGrantingTicketExpirationPolicy" alias="grantingTicketExpirationPolicy" />
```

Settings are controlled via:

```properties
# tgt.maxTimeToLiveInSeconds=28800
# tgt.timeToKillInSeconds=7200
```

#### `TimeoutExpirationPolicy`
The expiration policy applied to TGTs provides for most-recently-used expiration policy, similar to a Web server session timeout. 
For example, a 2-hour time span with this policy in effect would require a TGT to be used every 2 hours or less, otherwise 
it would be marked as expired.

```xml
<alias name="timeoutExpirationPolicy" alias="grantingTicketExpirationPolicy" />
```

Settings are controlled via:

```properties
# tgt.timeout.maxTimeToLiveInSeconds=28800
```

#### `HardTimeoutExpirationPolicy`
The hard timeout policy provides for finite ticket lifetime as measured from the time of creation. For example, a 4-hour time span 
for this policy means that a ticket created at 1PM may be used up until 5PM; subsequent attempts to use it will mark it expired 
and the user will be forced to re-authenticate.

```xml
<alias name="hardTimeoutExpirationPolicy" alias="grantingTicketExpirationPolicy" />
```

Settings are controlled via:

```properties
# tgt.timeout.hard.maxTimeToLiveInSeconds=28000
```

#### `ThrottledUseAndTimeoutExpirationPolicy`
The throttled timeout policy provides the concept of throttling where a ticket may be used at 
most every N seconds. This policy was designed to thwart denial of service conditions where a rogue or misconfigured client 
attempts to consume CAS server resources by requesting high volumes of service tickets in a short time.

```xml
<alias name="throttledUseAndTimeoutExpirationPolicy" alias="grantingTicketExpirationPolicy" />
```

Settings are controlled via:

```properties
# tgt.throttled.maxTimeToLiveInSeconds=28800
# tgt.throttled.timeInBetweenUsesInSeconds=5
```

#### `NeverExpiresExpirationPolicy`
The never expires policy allows tickets to exist indefinitely.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Use of this policy has significant consequences to overall 
security policy and should be enabled only after thorough review by a qualified security team. There are also implications to 
server resource usage for the ticket registries backed by filesystem storage. Since disk storage for tickets can never be reclaimed 
for those registries with this policy in effect, use of this policy with those ticket registry implementations 
is strongly discouraged.</p></div>

```xml
<alias name="neverExpiresExpirationPolicy" alias="grantingTicketExpirationPolicy" />
```

### Service Ticket Policies

#### `MultiTimeUseOrTimeoutExpirationPolicy`
This is the default policy applied to service tickets where a ticket is expired after a fixed number of uses or after a maximum 
period of inactivity elapses. This is default and only option.

```properties
# st.timeToKillInSeconds=10
# st.numberOfUses=1
```
