---
layout: default
title: CAS - Attribute Release Policies
category: Attributes
---

# Attribute Release Policies

The attribute release policy decides how attributes are selected and provided to a given application in the final CAS response. Additionally, each policy has the ability to apply an optional filter to weed out their attributes based on their values.

The following settings are shared by all attribute release policies:

| Name                                     | Value
|------------------------------------------|----------------------------------------------------------------
| `authorizedToReleaseCredentialPassword`  | Boolean to define whether the service is authorized to [release the credential as an attribute](ClearPass.html).
| `authorizedToReleaseProxyGrantingTicket` | Boolean to define whether the service is authorized to [release the proxy-granting ticket id as an attribute](../installation/Configuring-Proxy-Authentication.html).
| `excludeDefaultAttributes`               | Boolean to define whether this policy should exclude the default global bundle of attributes for release.
| `authorizedToReleaseAuthenticationAttributes`   | Boolean to define whether this policy should exclude the authentication/protocol attributes for release. Authentication attributes are considered those that are not tied to a specific principal and define extra supplementary metadata about the authentication event itself, such as the commencement date.
| `principalIdAttribute`                   | An attribute name of your own choosing that will be stuffed into the final bundle of attributes, carrying the CAS authenticated principal identifier. By default, the principal id is *NOT* released as an attribute.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>Think <strong>VERY CAREFULLY</strong> before turning on the above settings. Blindly authorizing an application to receive a proxy-granting ticket or the user credential
may produce an opportunity for security leaks and attacks. Make sure you actually need to enable those features and that you understand the why. Avoid where and when you can, specially when it comes to sharing the user credential.</p></div>

CAS makes a distinction between attributes that convey metadata about the authentication event versus
those that contain personally identifiable data for the authenticated principal.

## Authentication Attributes

During the authentication process, a number of attributes get captured and collected by CAS
to describe metadata and additional properties about the nature of the authentication event itself.
These typically include attributes that are documented and classified by the underlying protocol
or attributes that are specific to CAS which may describe the type of credentials used, successfully-executed
authentication handlers, date/time of the authentication, etc.

Releasing authentication attributes to service providers and applications can be
controlled to some extent. To learn more and see the relevant list of CAS properties,
please [review this guide](../configuration/Configuration-Properties.html#authentication-attributes).


## Principal Attributes

Principal attributes typically convey personally identifiable data about the authenticated user,
such as address, last name, etc. Release policies are available in CAS and documented below
to explicitly control the collection of attributes that may be authorized for release to a given application.

### Default

CAS provides the ability to release a bundle of principal attributes to all services by default. This bundle is not defined on a per-service basis and is always combined with attributes produced by the specific release policy of the service, such that for instance, you can devise rules to always release `givenName` and `cn` to every application, and additionally allow other specific principal attributes for only some applications per their attribute release policy.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#default-bundle).

### Return All

Return all resolved principal attributes to the service.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
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

Only return the principal attributes that are explicitly allowed by the service definition.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "mail", "sn" ] ]
  }
}
```

### REST

Only return the principal attributes that are explicitly allowed by contacting a REST endpoint. Endpoints must be designed to accept/process `application/json`. The expected response status code is `200` where the body of the response includes a `Map` of attributes linked to their values.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnRestfulAttributeReleasePolicy",
    "endpoint" : "https://somewhere.example.org"
  }
}
```

The following parameters are passed to the endpoint:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `principal`           | The object representing the authenticated principal.
| `service`             | The object representing the corresponding service definition in the registry.

The body of the submitted request may also include a `Map` of currently resolved attributes. 

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



### Return MultiMapped

The same policy may allow attribute definitions to be renamed and remapped to multiple attribute names, 
with duplicate attributes values mapped to different names.

For example, the following configuration will recognize the resolved attribute `eduPersonAffiliation` and will then release `affiliation` and `personAffiliation` whose values stem from the original `eduPersonAffiliation` attribute while `groupMembership` is released as `group`. In other words, the `eduPersonAffiliation` attribute is released twice under two different names each sharing the same value.


```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "eduPersonAffiliation" : [ "java.util.ArrayList", [ "affiliation", "personAffiliation" ] ],
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
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "uid" : "groovy { return attributes['uid'].get(0) + ' is great' }"
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
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.GroovyScriptAttributeReleasePolicy",
    "groovyScript" : "classpath:/script.groovy"
  }
}
```

The script itself may be designed in Groovy as:

```groovy
import java.util.*

def Map<String, List<Object>> run(final Object... args) {
    def currentAttributes = args[0]
    def logger = args[1]
    def principal = args[2]
    def service = args[3]

    logger.debug("Current attributes received are {}", currentAttributes)
    return [username:["something"], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

The following parameters are passed to the script:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `currentAttributes`   | `Map` of attributes currently resolved and available for release.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.
| `principal`           | The object representing the authenticated principal.
| `service`             | The object representing the corresponding service definition in the registry.

### Javascript/Python/Groovy Script

Let an external javascript, groovy or python script decide how principal attributes should be released.
This approach takes advantage of scripting functionality built into the Java platform.
While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ScriptedRegisteredServiceAttributeReleasePolicy",
    "scriptFile" : "classpath:/script.[py|js|groovy]"
  }
}
```

The scripts need to design a `run` function
that receives a list of parameters. The collection of current attributes in process
as well as a logger object are passed to this function. The result must produce a
map whose `key`s are attributes names and whose `value`s are a list of attribute values.

As an example, the script itself may be designed in Groovy as:

```groovy
import java.util.*

def Map<String, List<Object>> run(final Object... args) {
    def currentAttributes = args[0]
    def logger = args[1]

    logger.debug("Current attributes received are {}", currentAttributes)
    return[username:["something"], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

You are also allowed to stuff inlined groovy scripts into the `scriptFile` attribute. The script
has access to the collection of resolved `attributes` as well as a `logger` object.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ScriptedRegisteredServiceAttributeReleasePolicy",
    "scriptFile" : "groovy { return attributes }"
  }
}
```

### Chaining Policies

Attribute release policies can be chained together to process multiple rules.
The order of policy invocation is the same as the definition order defined for the service itself.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 300,
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ChainingAttributeReleasePolicy",
    "policies": [ "java.util.ArrayList",
      [
          {"@class": "..."},
          {"@class": "..."}
      ]
    ]
  }
}
```

## Attribute Value Filters

While each policy defines what principal attributes may be allowed for a given service,
there are optional attribute filters that can be set per policy to further weed out attributes based on their **values**.

[See this guide](Attribute-Value-Release-Policies.html) to learn more.
