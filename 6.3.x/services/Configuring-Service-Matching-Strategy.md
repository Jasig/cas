---
layout: default
title: CAS - Configuring Service Matching Strategy
category: Services
---

# Configuring Service Matching Strategy

Authentication requests that carry a client application identifier are compared against the service identifier
that is assigned to a service definition. By default, the service identifier is treated as a regular expression pattern
that needs to be properly encoded and defined for matching operation to successfully execute. This strategy can be
defined on a per-service basis to allow for alternative options or a full strategy implementation that may want to
take external factors and variables into account.

See below for details on matching strategies.

## Full Regex

This is the default option that treats the `serviceId` as a regular expression. With this option,
CAS tries to match the expression against the entire requested service identifier and implicitly 
adds a `^` at the start and `$` at the end of the defined pattern, meaning it will not look for substring matches.

A sample JSON file follows:

```json
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": "https://.*",
  "name": "sample",
  "id": 1,
  "matchingStrategy": {
    "@class": "org.apereo.cas.services.FullRegexRegisteredServiceMatchingStrategy"
  }
}
```

## Partial Regex

This strategy treats the `serviceId` as a regular expression. With this option, CAS will look and allow for substring matches.

A sample JSON file follows:

```json
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": "\\d\\d\\d",
  "name": "sample",
  "id": 1,
  "matchingStrategy": {
    "@class": "org.apereo.cas.services.PartialRegexRegisteredServiceMatchingStrategy"
  }
}
```

For example, the above pattern will match against `https://example123.com`.

## Literal

This strategy treats the `serviceId` as a literal text and will look for exact matches. This might be useful in scenarios where you 
may not wish to deal with encoding individual/special characters such as `?` in the URL.

A sample JSON file follows:

```json
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": "https://example.com?key=value",
  "name": "sample",
  "id": 1,
  "matchingStrategy": {
    "@class": "org.apereo.cas.services.LiteralRegisteredServiceMatchingStrategy",
    "caseInsensitive": true
  }
}
```

