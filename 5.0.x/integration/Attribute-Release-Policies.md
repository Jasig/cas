---
layout: default
title: CAS - Attribute Release Policies
---

# Attribute Release Policies

The release policy decides how attributes are to be released for a given service. Each policy has
the ability to apply an optional filter.

The following settings are shared by all attribute release policies:

| Name                    | Value
|---------------------------------------|----------------------------------------------------------------
| `authorizedToReleaseCredentialPassword` | Boolean to define whether the service is authorized to [release the credential as an attribute](ClearPass.html).
| `authorizedToReleaseProxyGrantingTicket` | Boolean to define whether the service is authorized to [release the proxy-granting ticket id as an attribute](../installation/Configuring-Proxy-Authentication.html)

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Think <strong>VERY CAREFULLY</strong> before turning on the above settings. Blindly authorizing an application to receive a proxy-granting ticket or the user credential
may produce an opportunity for security leaks and attacks. Make sure you actually need to enable those features and that you understand the why. Avoid where and when you can, specially when it comes to sharing the user credential.</p></div>

CAS makes a distinction between attributes that convey metadata about the authenication event versus
those that contain personally identifiable data for the authenticated principal. 

## Authentication Attributes

During the authentication process, a number of attributes get captured and collected by CAS
to describe metadata and additional properties about the nature of the authentication event itself.
These typically include attributes that are documented and classified by the underlying protocol
or attributes that are specific to CAS which may describe the type of credentials used, successfully-executed
authentication handlers, date/time of the authentication, etc.

Releasing authentication attributes to service providers and applications can be
controlled to some extent. To learn more and see the relevant list of CAS properties, 
please [review this guide](../installation/Configuration-Properties.html).
 

## Principal Attributes

Principal attributes typically convey personally identifiable data about the authenticated user,
such as address, last name, etc. Release policies are available in CAS and docuemnted below 
to explicitly control the collection 
of attributes that may be authorized for release to a given application.

### Default

CAS provides the ability to release a bundle of principal attributes to all services by default. This bundle is not defined on a per-service
basis and is always combined with attributes produced by the specific release policy of the service, such that for instance, you can devise
rules to always release `givenName` and `cn` to every application, and additionally allow other specific principal attributes for only some
applications per their attribute release policy.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

### Return All

Return all resolved principal attributes to the service.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
  }
}
```

### Deny All

Never ever return principal attributes to applications. Note that this policy
also skips and refuses to release default attributes, if any.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.DenyAllAttributeReleasePolicy"
  }
}
```

### Return Allowed

Only return the principal attributes that are explicitly allowed by the configuration.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "mail", "sn" ] ]
  }
}
```


### Return Mapped

Similar to above, this policy will return a collection of allowed principal attributes for the
service, but also allows those principal attributes to be mapped and "renamed" at the more granular service level.

For example, the following configuration will recognize the resolved
attributes `eduPersonAffiliation` and `groupMembership` and will then
release `affiliation` and `group` to the web application configured.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "eduPersonAffiliation" : "affiliation",
      "groupMembership" : "group"
    }
  }
}
```

### Inline Groovy Attributes

Principal attributes that are mapped may produce their values from an inline groovy script. As an example, if you currently 
have resolved a `uid` attribute with a value of `piper`, you could then consider the following:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "uid" : "groovy { return attributes['uid'] + ' is great' }"
    }
  }
}
```

In the above snippet, the value of the `uid` attribute name is mapped to the result of the inline groovy script.
Inline scripts always begin with the syntax `groovy {...}` and are passed the current collection of resolved
attributes as an `attributes` binding variable. The result of the script can be a single/collection of value(s).

The above configuration will produce a `uid` attribute for the application whose value is a concatenation of
the original value of `uid` plus the words " is great", so the final result would be "piper is great".

### File-based Groovy Attributes

Identical to inline groovy attribute definitions, except the groovy script can also be externalized to a `.groovy` file:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "uid" : "file:/etc/cas/uid-for-sample-service.groovy"
    }
  }
}
```

### Groovy Script

Let an external Groovy script decide how principal attributes should be released.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.GroovyScriptAttributeReleasePolicy",
    "groovyScript" : "classpath:/script.groovy"
  }
}
```

The script itself may be designed as:

```groovy
import java.util.*

class SampleGroovyPersonAttributeDao {
    def Map<String, List<Object>> run(final Object... args) {
        def currentAttributes = args[0]
        def logger = args[1]

        logger.debug("Current attributes received are {}", currentAttributes)
        return[username:["something"], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
    }
}
```

## Attribute Filters

While each policy defines what principal attributes may be allowed for a given service,
there are optional attribute filters that can be set per policy to further weed out attributes based on their **values**.

### Regex

The regex filter that is responsible to make sure only attributes whose value
matches a certain regex pattern are released.

Suppose that the following attributes are resolved:

| Name       							| Value
|---------------------------------------|----------------------------------------------------------------
| `uid`        							| jsmith
| `groupMembership`        	| std  
| `cn`        							| JohnSmith   

The following configuration for instance considers the initial list of `uid`,
`groupMembership` and then only allows and releases attributes whose value's length
is 3 characters. Therefor, out of the above list, only `groupMembership` is released to the application.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 200,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "attributeFilter" : {
      "@class" : "org.apereo.cas.services.support.RegisteredServiceRegexAttributeFilter",
      "pattern" : "^\w{3}$"
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```
