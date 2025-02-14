---
layout: default
title: CAS - Adaptive Risk-based Authentication
category: Authentication
---

# Risk-based Authentication

Risk-based authentication allows CAS to detect suspicious and seemingly-fraudulent authentication requests based on past user behavior
and collected authentication events, statistics, etc. Once and *after* primary authentication where the principal is identified,
the authentication transaction is analyzed via a number of configurable criteria and fences to determine how *risky* the attempt may be.
The result of the evaluation step is a cumulative risk score that is then weighed against a risk threshold set by the CAS operator.
In the event that the authentication attempt is considered risky well beyond the risk threshold, CAS may be allowed to take action and
mitigate that risk.

Simply put, the story told is:

>If an authentication request is at least [X%] risky, take action to mitigate that risk.

The functionality of this feature is **ENTIRELY** dependent upon collected statistics and authentication events in the past.
Without data, there is nothing to analyze and no risk to detect.

Note that evaluation of attempts and mitigation of risks are all recorded in the audit log.

<div class="alert alert-info"><strong>Adaptive Authentication</strong><p>
If you need to preemptively evaluate authentication attempts based on various characteristics of the request,
you may be interested in <a href="../mfa/Configuring-Adaptive-Authentication.html">this guide</a> instead.</p></div>

## Risk Calculation

One or more risk calculators may be enabled to allow an analysis of authentication requests.

A high-level explanation of the risk calculation strategy follows:

- If there is no recorded event at all present for the principal, consider the request suspicious.
- If the number of recorded events for the principal based on the active criteria matches the total number of events, consider the
request safe.

### IP Address

This calculator looks into past authentication events that match the client ip address. It is applicable if you wish
to consider authentication requests from unknown ip addresses suspicious for the user. The story here is:

> Find all past authentication events that match the current client ip address and calculate an averaged score.

### Browser User Agent

This calculator looks into past authentication events that match the client's `user-agent` string. It is applicable if you wish
to consider authentication requests from unknown browsers suspicious for the user. The story here is:

> Find all past authentication events that match the current client browser and calculate an averaged score.

### Geolocation

This calculator looks into past authentication events that contain geolocation data, and compares those with the current geolocation.
If current geolocation data is unavailable, it will attempt to geocode the location based on the current client ip address. This feature
mostly depends on whether or not geodata is made available to CAS via the client browser.   The story here is:

> Find all past authentication events that match the current client location and calculate an average score.

### Date/Time

This calculator looks into past authentication events that fit within the defined time-window. It is applicable if you wish
to consider authentication requests outside that window suspicious for the user. The story here is:

> Find all past authentication events that are established X hours before/after now and calculate an averaged score.

## Risk Mitigation

Once an authentication attempt is deemed risky, a contingency plan may be enabled to mitigate risk. If configured and allowed,
CAS may notify both the principal and deployer via both email and sms.

### Block Authentication

Prevent the authentication flow to proceed and disallow the establishment of the SSO session.

### Multifactor Authentication

Force the authentication event into a [multifactor flow of choice](../mfa/Configuring-Multifactor-Authentication.html),
identified by the provider id.

## Configuration

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-electrofence</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#risk-based-authentication).

### Messaging & Notifications

Users may be notified of risky authentication attempts via text messages and/or email.
To learn more about available options, please [see this guide](../notifications/SMS-Messaging-Configuration.html)
or [this guide](../notifications/Sending-Email-Configuration.html).

### Remember

- You **MUST** allow and configure CAS to track and record [authentication events](Configuring-Authentication-Events.html).
- You **MUST** allow and configure CAS to [geolocate authentication requests](GeoTracking-Authentication-Requests.html).
- If the selected contingency plan is to force the user into a multifactor authentication flow, you then **MUST** configure CAS for
[multifactor authentication](../mfa/Configuring-Multifactor-Authentication.html) and the relevant provider.
