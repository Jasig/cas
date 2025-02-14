---
layout: default
title: CAS - Attribute Value Release Policies
---

# Attribute Value Filters

While each policy defines what principal attributes may be allowed for a given service,
there are optional attribute filters that can be set per policy to further weed out attributes based on their **values**.

## Chaining Filters

Attribute filters can be chained together so as to associate multiple filters with a single service definition.

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
      "@class" : "org.apereo.cas.services.support.RegisteredServiceChainingAttributeFilter",
      "policies": [ "java.util.ArrayList",
        [
            {
              "@class" : "org.apereo.cas.services.support.RegisteredServiceRegexAttributeFilter",
              "pattern" : "^\\w{3}$",
              "order": 10
            },
            {
              "@class" : "..."
            }
        ]
      ]
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```

Chained attribute filters are sorted given their `order` property first before execution.

## Regex

The regex filter that is responsible to make sure only attributes whose value
matches a certain regex pattern are released.

Suppose that the following attributes are resolved:

| Name                                    | Value
|-----------------------------------------|----------------------------------------------------------------
| `uid`                                   | jsmith
| `groupMembership`                       | std
| `cn`                                    | JohnSmith

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
      "pattern" : "^\\w{3}$"
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```

## Mapped Regex

The regex filter that is responsible to make sure only a selected set of attributes whose value matches a certain regex pattern are released. The filter selectively applies patterns to attributes mapped in the configuration. If an attribute is mapped, it is only allowed to be released if it matches the linked pattern. If an attribute is not mapped, it may optionally be excluded from the released set of attributes.

For example, the below example only allows release of `memberOf` if it contains a value that is 3 characters in length. If no values are found, the `memberOf` is excluded from the final released bundle.

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
      "@class": "org.apereo.cas.services.support.RegisteredServiceMappedRegexAttributeFilter",
      "patterns": {
          "@class" : "java.util.TreeMap",
          "memberOf": "^\\w{3}$"
      },
      "excludeUnmappedAttributes": false,
      "completeMatch": false,
      "caseInsensitive": true,
      "order": 0
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "memberOf" ] ]
  }
}
```

The following fields are supported by this filter:

| Name                 | Description
|----------------------|--------------------------------------------------------------------------
| `patterns`           | A map of attributes and their associated pattern tried against value(s).
| `completeMatch`      | Indicates whether pattern-matching should execute over the entire value region.
| `excludeUnmappedAttributes` | Indicates whether unmapped attributes should be removed from the final bundle.
| `caseInsensitive` | Indicates whether pattern matching should be done in a case-insensitive manner.

## Reverse Mapped Regex

Identical to the *Mapped Regex* filter, except that the filter only allows a selected set of attributes whose value
**does not match** a certain regex pattern are released.

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
      "@class": "org.apereo.cas.services.support.RegisteredServiceReverseMappedRegexAttributeFilter",
      "patterns": {
          "memberOf": "^\\w{3}$"
      },
      "excludeUnmappedAttributes": false,
      "completeMatch": false,
      "caseInsensitive": true,
      "order": 0
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "memberOf" ] ]
  }
}
```

## Mutant Mapped Regex

This filter structurally, in terms of settings and properties, is identical to the *Mapped Regex* filter. Its main ability is to filter attribute values by a collection of patterns and then supplant the value dynamically based on the results of the regex match.

For example, the following definition attempts to filter all values assigned to the attribute `memberOf` based on the given patterns. Each pattern is linked via `->` to the expected return value that may reference specific groups in the produced regex result. Assuming the attribute `memberOf` has values of `math101` and `marathon101`, the filter will produce values `courseA-athon101` and `courseB-h101` after processing. 

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
      "@class": "org.apereo.cas.services.support.RegisteredServiceMutantRegexAttributeFilter",
      "patterns": {
          "@class" : "java.util.TreeMap",
          "memberOf": [ "java.util.ArrayList", [ "^mar(.+)(101) -> courseA-$1$2", "^mat(.+)(101) -> courseB-$1$2" ] ]
      },
      "excludeUnmappedAttributes": false,
      "completeMatch": false,
      "caseInsensitive": true,
      "order": 0
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "memberOf" ] ]
  }
}
```

## Groovy

Attribute value filtering may also be carried out using an inline or external Groovy script.
Scripts have access to the current resolved attributes via `attributes` and a `logger`.
The returned result of the script must be a `Map<String, Object>`.

### Inlined Groovy

An inline groovy filter allows you to embed the script directly in the service definition.

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
      "@class" : "org.apereo.cas.services.support.RegisteredServiceScriptedAttributeFilter",
      "script" : "groovy { return attributes }"
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```

### External Groovy

An external groovy filter allows you to define the script in file located outside of the CAS web application.

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
      "@class" : "org.apereo.cas.services.support.RegisteredServiceScriptedAttributeFilter",
      "script" : "file:/etc/cas/filter-this.groovy"
    },
    "allowedAttributes" : [ "java.util.ArrayList", [ "uid", "groupMembership" ] ]
  }
}
```

The outline of the script may be as follows:

```groovy
import java.util.*
logger.info "Attributes currently resolved: ${attributes}"
def map =...
return map
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `attributes`      | A `Map` of current  attributes resolved from sources.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.
