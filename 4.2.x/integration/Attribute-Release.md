---
layout: default
title: CAS - Attribute Release
---

# Attribute Release
Resolved attributes are returned to scoped services via the [SAML 1.1 protocol](../protocol/SAML-Protocol.html) or the [CAS protocol](../protocol/CAS-Protocol.html).

Attributes pass through a two-step process:

* [Attribute Resolution](Attribute-Resolution.html): Done at the time of establishing the principal, *usually* via `PrincipalResolver` components where attributes are resolved from various sources.
* Attribute Release: Adopters must explicitly configure attribute release for services in order for the resolved attributes to be released to a service in the validation response.


<div class="alert alert-info"><strong>Service Management</strong><p>Attribute release may also be configured via the
<a href="../installation/Service-Management.html">Service Management tool</a>.</p></div>

## Principal-Id Attribute
The service registry component of CAS has the ability to allow for configuration of a
`usernameAttributeProvider` to be returned for the given registered service. When this property is set for a service, CAS will return the value of the configured attribute as part of its validation process.

* Ensure the attribute is available and resolved for the principal
* Set the `usernameAttributeProvider` property of the given service to one of the attribute providers below

### Default
The default configuration which need not explicitly be defined, simply returns the resolved
principal id as the username for this service.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample"
}
```

### Principal Attribute
Returns an attribute that is already resolved for the principal as the username for this service. If the attribute
is not available, the default principal id will be used.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 600,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.jasig.cas.services.PrincipalAttributeRegisteredServiceUsernameProvider",
    "usernameAttribute" : "cn"
  }
}
```


### Anonymous
Provides an opaque identifier for the username. The opaque identifier by default conforms to the requirements
of the [eduPersonTargetedID](http://www.incommon.org/federation/attributesummary.html#eduPersonTargetedID) attribute.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 500,
  "description" : "sample",
  "usernameAttributeProvider" : {
    "@class" : "org.jasig.cas.services.AnonymousRegisteredServiceUsernameAttributeProvider",
    "persistentIdGenerator" : {
      "@class" : "org.jasig.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator",
      "salt" : "aGVsbG93b3JsZA=="
    }
  }
}
```

## Attribute Release Policy
The release policy decides how attributes are to be released for a given service. Each policy has
the ability to apply an optional filter.

The following settings are shared by all attribute release policies:

| Name                    | Value
|---------------------------------------+---------------------------------------------------------------+
| `authorizedToReleaseCredentialPassword` | Boolean to define whether the service is authorized to [release the credential as an attribute](ClearPass.html).
| `authorizedToReleaseProxyGrantingTicket` | Boolean to define whether the service is authorized to [release the proxy-granting ticket id as an attribute](../installation/Configuring-Proxy-Authentication.html)

### Components

#### Return All
Return all resolved attributes to the service.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllAttributeReleasePolicy"
  }
}
```

#### Return Allowed
Only return the attributes that are explicitly allowed by the configuration.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "mail", "sn" ] ]
  }
}
```


#### Return Mapped
Similar to above, this policy will return a collection of allowed attributes for the
service, but also allows those attributes to be mapped and "renamed" at the more granular service level.

For example, the following configuration will recognize the resolved
attributes `uid`, `eduPersonAffiliation` and `groupMembership` and will then
release `uid`, `affiliation` and `group` to the web application configured.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "eduPersonAffiliation" : "affiliation",
      "groupMembership" : "group"
    }
  }
}
```


### Attribute Filters
While each policy defines what attributes may be allowed for a given service,
there are optional attribute filters that can be set per policy to further weed out attributes based on their **values**.

##### Regex
The regex filter that is responsible to make sure only attributes whose value
matches a certain regex pattern are released.

Suppose that the following attributes are resolved:

| Name       							| Value
|-------------------------+---------------------------------------------------------------+
| `uid`        							| jsmith
| `groupMembership`        	| std  
| `cn`        							| JohnSmith   

The following configuration for instance considers the initial list of `uid`,
`groupMembership` and then only allows and releases attributes whose value's length
is 3 characters. Therefor, out of the above list, only `groupMembership` is released to the application.

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 200,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "attributeFilter" : {
      "@class" : "org.jasig.cas.services.support.RegisteredServiceRegexAttributeFilter",
      "pattern" : "^\w{3}$"
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```

## Caching/Updating Attributes
By default, [resolved attributes](Attribute-Resolution.html) are cached to the
length of the SSO session. If there are any attribute value changes since the
commencement of SSO session, the changes are not reflected and returned back
to the service upon release time.

### Components

### `PrincipalAttributesRepository`
Parent component that describes the relationship between a CAS `Principal`
and the underlying attribute repository source.

### `DefaultPrincipalAttributesRepository`
The default relationship between a CAS `Principal` and the underlying attribute
repository source, such that principal attributes are kept as they are without
any additional processes to evaluate and update them. This need not be configured explicitly.

### `CachingPrincipalAttributesRepository`
The relationship between a CAS `Principal` and the underlying attribute
repository source, that describes how and at what length the CAS `Principal` attributes should
be cached. Upon attribute release time, this component is consulted to ensure that appropriate
attribute values are released to the scoped service, per the cache expiration policy.
If the expiration policy has passed, the underlying attribute repository source will be consulted
to figure out the available set of attributes.

The default caching policy is 2 hours. This component also has the ability to resolve conflicts between
existing principal attributes and
those that are retrieved from repository source via a `mergingStrategy` property.
This is useful if you want to preserve the collection of attributes that are already
available to the principal that were retrieved from a different place during the authentication event, etc.

<div class="alert alert-info"><strong>Caching Upon Release</strong><p>Note
that the policy is only consulted at release time, upon a service ticket validation event. If there are
any custom webflows and such that wish to rely on the resolved <code>Principal</code> AND also wish to r
eceive an updated set of attributes, those components must consult the underlying source directory
without relying on the <code>Principal</code>.</p></div>

Sample configuration follows:

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
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


#### Merging Strategies
By default, no merging strategy takes place, which means the principal attributes are always ignored and
attributes from the source are always returned. But any of the following merging strategies may be a suitable option:

* `MultivaluedAttributeMerger`
Attributes with the same name are merged into multi-valued lists.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=[123-456-7890, 111-222-3333, 000-999-8888], office=3233}`


```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
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

* `NoncollidingAttributeAdder`
Attributes are merged such that attributes from the source that don't already exist for the principal are produced.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=123-456-7890, office=3233}`

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
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

* `ReplacingAttributeAdder`
Attributes are merged such that attributes from the source always replace principal attributes.

For example:

1. Principal has attributes `{email=eric.dalquist@example.com, phone=123-456-7890}`
2. Source has attributes `{phone=[111-222-3333, 000-999-8888], office=3233}`
3. The resulting merged would have attributes: `{email=eric.dalquist@example.com, phone=[111-222-3333, 000-999-8888], office=3233}`


```xml
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
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

### Encrypting Attributes
CAS by default supports the ability to encrypt certain attributes, such as the proxy-granting ticket and the credential conditionally.
If you wish to take this a step further and encrypt other attributes that you deem sensitive, you can use the following components
as a baseline to carry out the task at hand:

`DefaultCasAttributeEncoder`
The default implementation of the attribute encoder that will use a per-service key-pair
to encrypt. It will attempt to query the collection of attributes that resolved to determine
which attributes can be encoded. Attributes will be encoded via a `RegisteredServiceCipherExecutor`.

```xml
<bean id="cas3ServiceSuccessView"
    class="Cas30ResponseView"
    c:view-ref="cas3JstlSuccessView"
    p:successResponse="true"
    p:servicesManager-ref="servicesManager"
    p:casAttributeEncoder-ref="casAttributeEncoder"  />

<bean id="casRegisteredServiceCipherExecutor"
    class="org.jasig.cas.services.DefaultRegisteredServiceCipherExecutor" />

<bean id="casAttributeEncoder"
    class="org.jasig.cas.authentication.support.DefaultCasAttributeEncoder"
    c:servicesManager-ref="servicesManager"
    c:cipherExecutor-ref="casRegisteredServiceCipherExecutor"  />
```
