---
layout: default
title: CAS - Configuring Service Proxy Policy
category: Services
---

# Configure Proxy Authentication Policy

Each registered application in the registry may be assigned a proxy policy to determine whether the service is allowed for proxy authentication. This means that a PGT will not be issued to a service unless the proxy policy is configured to allow it. Additionally, the policy could also define which endpoint urls are in fact allowed to receive the PGT.

Note that by default, the proxy authentication is disallowed for all applications.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Think <strong>VERY CAREFULLY</strong> before allowing an application to exercise proxy authentication. Blindly authorizing an application to receive a proxy-granting ticket may produce an opportunity for security leaks and attacks. Make sure you actually need to enable those features and that you understand the why. Avoid where and when you can.</p></div>

## Refuse

Disallows proxy authentication for a service. This is default policy and need not be configured explicitly.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "proxyPolicy" : {
    "@class" : "org.apereo.cas.services.RefuseRegisteredServiceProxyPolicy"
  }
}
```

## Regex

A proxy policy that only allows proxying to PGT urls that match the specified regex pattern.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "proxyPolicy" : {
    "@class" : "org.apereo.cas.services.RegexMatchingRegisteredServiceProxyPolicy",
    "pattern" : "^https?://.*"
  }
}
```
