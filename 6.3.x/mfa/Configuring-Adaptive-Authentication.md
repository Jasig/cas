---
layout: default
title: CAS - Adaptive Authentication
category: Multifactor Authentication
---

# Adaptive Authentication

Adaptive authentication in CAS allows you to accept or reject authentication requests based on certain characteristics
of the client browser and/or device. When configured, you are provided with options to block authentication requests
from certain locations submitted by certain browser agents. For instance, you may consider authentication requests submitted
from `London, UK` to be considered suspicious, or you may want to block requests that are submitted from Internet Explorer, etc.

Adaptive authentication can also be configured to trigger multifactor based on specific days and times. For example, you may wish to trigger multifactor on select days or if the current hour is after 11pm or before 6am. Each rule block may be assigned to an mfa provider where successful matching of rules allows for the multifactor trigger to execute.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#adaptive-authentication).

To enable adaptive authentication, you will need to allow CAS to geo-locate authentication requests.
To learn more, please [see this guide](../installation/GeoTracking-Authentication-Requests.html)

## IP Intelligence

CAS provides you with the capability to examine the client IP address and decide whether access should be granted. This may be useful
to detect bot, proxy or VPN traffic and protect your deployment from fraud, automated attacks, crawlers, etc.

The result of the IP address examination may either ban and request the request, allow it to go through, or present a score 
to indicate the probability of an IP address that may be questionable. If the result is ranked score, it will be compared against
the configured risk threshold to determine whether the request may proceed.

Banned IP address can either be defined as patterns in the CAS settings, or they may be examined using the listed strategies below.

### REST

The client IP address is submitted to a REST endpoint as the header `clientIpAddress` under a `GET` request. The expected result status codes are the following:

| Code                  | Description
|-----------------------|------------------------------------------------------------------------------------------------------------
| `401`, `403`          | IP address is banned and the request will be rejected.
| `200`, `202`          | IP address is allowed and the request may proceed.
| All Others            | Response body is expected to contain a score between `1` and `0`, (`1=Banned` and `0=Allowed`), indicating IP address suspiciousness.

### Groovy

The client IP address may be examined using a Groovy script whose outline should match the following:

```groovy
import org.apereo.cas.authentication.principal.*
import org.apereo.cas.authentication.*
import org.apereo.cas.util.*
import org.apereo.cas.authentication.adaptive.intel.*

def run(Object[] args) {
    def requestContext = args[0]
    def clientIpAddress = args[1]
    def logger = args[2]
    logger.info("Client ip address provided is ${clientIpAddress}")
    
    if (ipAddressIsRejected())
        return IPAddressIntelligenceResponse.banned()
    
    return IPAddressIntelligenceResponse.allows()
}
```

### BlackDot IP Intel

Please [see this link](https://getipintel.net/) for more info. A valid subscription is required for large query counts.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>This is a free service, primarily useful for development, testing and demos. Production deployments 
of this service require a subscription that can handle the expected query count and load.</p></div>

Note that a valid email that is checked frequently must be used in the contact field or else the service might be disabled without notice. Furthermore, **DO NOT** exceed more 
than 500 queries per day & 15 queries per minute. See [FAQ](https://getipintel.net/#FAQ) for further information.

# Risk-based Authentication

CAS is able to track and examine authentication requests for suspicious behavior.
To learn more, please [see this guide](../installation/Configuring-RiskBased-Authentication.html).
