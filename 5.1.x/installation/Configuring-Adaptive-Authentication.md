---
layout: default
title: CAS - Adaptive Authentication
---

# Adaptive Authentication

Adaptive authentication in CAS allows you to accept or reject authentication requests based on certain characteristics
of the client browser and/or device. When configured, you are provided with options to block authentication requests
from certain locations submitted by certain browser agents. For instance, you may consider authentication requests submitted
from `London, UK` to be considered suspicious, or you may want to block requests that are submitted from Internet Explorer, etc.

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#adaptive-authentication).

To enable adaptive authentication, you will need to allow CAS to geo-locate authentication requests.
To learn more, please [see this guide](GeoTracking-Authentication-Requests.html)

# Risk-based Authentication

CAS is able to track and examine authentication requests for suspicious behavior.
To learn more, please [see this guide](Configuring-RiskBased-Authentication.html).
