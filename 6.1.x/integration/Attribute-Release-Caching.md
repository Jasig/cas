---
layout: default
title: CAS - Attribute Release Caching
category: Attributes
---
# Attribute Release Caching

By default, [resolved attributes](Attribute-Resolution.html) are cached to the
length of the SSO session. If there are any attribute value changes since the
commencement of SSO session, the changes are not reflected and returned back
to the service upon release time.

Note: Remember that while the below policies are typically applied at release time on a per-service level, 
CAS automatically does create attribute release caching policies at a more global with configurable timeouts
and durations. See [the relevant settings](../configuration/Configuration-Properties.html#authentication-attributes) for more info.

The following settings are shared by all principal attribute repositories:

| Name                                     | Value
|------------------------------------------|--------------------------------------------------------------------------------------------------------
| `mergingStrategy`  | Indicate the merging strategy when combining attributes from multiple sources. Accepted values are `MULTIVALUED`, `ADD`, `NONE`, `MULTIVALUED` 
| `attributeRepositoryIds`  | A `Set` of attribute repository identifiers to consult for attribute resolution at release time.
| `ignoreResolvedAttributes`  | Ignore the collection of attributes that may have been resolved during the principal resolution phase, typically via attribute repositories.

## Default

The default relationship between a CAS `Principal` and the underlying attribute
repository source, such that principal attributes are kept as they are without
any additional processes to evaluate and update them. This need not be configured explicitly.

## Caching

The relationship between a CAS `Principal` and the underlying attribute
repository source, that describes how and at what length the CAS `Principal` attributes should
be cached. Upon attribute release time, this component is consulted to ensure that appropriate
attribute values are released to the scoped service, per the cache expiration policy.
If the expiration policy has passed, the underlying attribute repository source will be consulted
to figure out the available set of attributes.

This component also has the ability to resolve conflicts between existing principal attributes and
those that are retrieved from repository source via a `mergingStrategy` property.
This is useful if you want to preserve the collection of attributes that are already
available to the principal that were retrieved from a different place during the authentication event, etc.

<div class="alert alert-info"><strong>Caching Upon Release</strong><p>Note
that the policy is only consulted at release time, upon a service ticket validation event. If there are
any custom webflows and such that wish to rely on the resolved <code>Principal</code> AND also wish to
receive an updated set of attributes, those components must consult the underlying source directory
without relying on the <code>Principal</code>.</p></div>

Sample configuration follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
      "duration" : {
        "@class" : "javax.cache.expiry.Duration",
        "timeUnit" : [ "java.util.concurrent.TimeUnit", "HOURS" ],
        "expiration" : 2
      },
      "mergingStrategy" : "NONE"
    }
  }
}
```

## Merging Strategies

By default, no merging strategy takes place, which means the principal attributes are always ignored and
attributes from the source are always returned. But any of the following merging strategies may be a suitable option:

### Merge

Attributes with the same name are merged into multi-valued lists.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=[123-456-7890, 111-222-3333, 000-999-8888], office=3233}`


```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
      "duration" : {
        "@class" : "javax.cache.expiry.Duration",
        "timeUnit" : [ "java.util.concurrent.TimeUnit", "HOURS" ],
        "expiration" : 2
      },
      "mergingStrategy" : "MULTIVALUED"
    }
  }
}
```

### Add

Attributes are merged such that attributes from the source that don't already exist for the principal are produced.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=123-456-7890, office=3233}`

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
      "duration" : {
        "@class" : "javax.cache.expiry.Duration",
        "timeUnit" : [ "java.util.concurrent.TimeUnit", "HOURS" ],
        "expiration" : 2
      },
      "mergingStrategy" : "ADD"
    }
  }
}
```

### Replace

Attributes are merged such that attributes from the source always replace principal attributes.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=[111-222-3333, 000-999-8888], office=3233}`


```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
      "duration" : {
        "@class" : "javax.cache.expiry.Duration",
        "timeUnit" : [ "java.util.concurrent.TimeUnit", "HOURS" ],
        "expiration" : 2
      },
      "mergingStrategy" : "REPLACE"
    }
  }
}
```


## Attribute Repository Filtering

Principal attribute repositories can consult attribute sources defined and controlled by [Person Directory](Attribute-Resolution.html). Assuming a JSON attribute 
repository source is defined with the identifier `MyJsonRepository`, the following definition disregards all previously-resolved attributes and contacts `MyJsonRepository`
again to fetch attributes and cache them for `30` minutes.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "name" : "HTTPS and IMAPS",
  "id" : 1,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllAttributeReleasePolicy",
    "principalAttributesRepository" : {
        "@class" : "org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
        "duration" : {
          "@class" : "javax.cache.expiry.Duration",
          "timeUnit" : [ "java.util.concurrent.TimeUnit", "MINUTES" ],
          "expiration" : 30
        },
        "ignoreResolvedAttributes": true,
        "attributeRepositoryIds": ["java.util.HashSet", [ "MyJsonRepository" ]],
        "mergingStrategy" : "MULTIVALUED"
    }
  }
}
```

Here is a similar example with caching turned off for the service where CAS attempts to combine previously-resolved attributes with the results from the attribute
repository identified as `MyJsonRepository`. The expectation is that the attribute source `MyJsonRepository` is excluded from principal resolution during the authentication phase
and should only be contacted at release time for this service:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "name" : "HTTPS and IMAPS",
  "id" : 1,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllAttributeReleasePolicy",
    "principalAttributesRepository" : {
        "@class" : "org.apereo.cas.authentication.principal.DefaultPrincipalAttributesRepository",
        "ignoreResolvedAttributes": false,
        "attributeRepositoryIds": ["java.util.HashSet", [ "MyJsonRepository" ]],
        "mergingStrategy" : "MULTIVALUED"
    }
  }
}
```
