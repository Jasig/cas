---
layout: default
title: CAS - SAML2 Attribute Release
---

# SAML2 Attribute Release

Attribute filtering and release policies are defined per SAML service.  See [this guide](../integration/Attribute-Release-Policies.html) for more info.

A few additional policies specific to SAML services are also provided below.

## InCommon Research and Scholarship

A specific attribute release policy is available to release the [attribute bundles](https://spaces.internet2.edu/display/InCFederation/Research+and+Scholarship+Attribute+Bundle)
needed for InCommon's Research and Scholarship service providers:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "entity-ids-allowed-via-regex",
  "name": "SAML",
  "id": 10,
  "metadataLocation": "path/to/incommon/metadata.xml",
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ChainingAttributeReleasePolicy",
    "policies": [ "java.util.ArrayList",
      [
         {"@class": "org.apereo.cas.support.saml.services.InCommonRSAttributeReleasePolicy"},
         {"@class": "org.apereo.cas.support.saml.services.RefedsRSAttributeReleasePolicy"}
      ]
    ]
  }
}
```

## Groovy Script

This policy allows a Groovy script to calculate the collection of released attributes.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "entity-ids-allowed-via-regex",
  "name": "SAML",
  "id": 10,
  "metadataLocation": "path/to/incommon/metadata.xml",
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.support.saml.services.GroovySamlRegisteredServiceAttributeReleasePolicy",
    "groovyScript": "file:/etc/cas/config/script.groovy"
  }
}
```

The outline of the script may be designed as:

```groovy
import java.util.*
import org.apereo.cas.support.saml.services.*
import org.apereo.cas.support.saml.*

def Map<String, Object> run(final Object... args) {
    def attributes = args[0]
    def service = args[1]
    def resolver = args[2]
    def facade = args[3]
    def entityDescriptor = args[4]
    def applicationContext = args[5]
    def logger = args[6]
    ...
    return null;
}
```

The following parameters are passed to the script:

| Parameter        | Description
|------------------|--------------------------------------------------------------------------------------------
| `attributes`     | Map of current attributes resolved and available for release.
| `service`        | The SAML service definition matched in the service registry.
| `resolver`       | The metadata resolver instance of this service provider.
| `facade`         | A wrapper on top of the metadata resolver that allows access to utility functions.
| `entityDescriptor`    | The `EntityDescriptor` object matched and linked to this service provider's metadata.
| `applicationContext`  | CAS application context allowing direct access to beans, etc.
| `logger`         | The object responsible for issuing log messages such as `logger.info(...)`.

An example script follows:

```groovy
import java.util.*
import org.apereo.cas.support.saml.services.*
import org.apereo.cas.support.saml.*

def Map<String, Object> run(final Object... args) {
    def attributes = args[0]
    def service = args[1]
    def resolver = args[2]
    def facade = args[3]
    def entityDescriptor = args[4]
    def applicationContext = args[5]
    def logger = args[6]

    if (entityDescriptor.entityId == "TestingSAMLApplication") {
      return [username:["something"], another:"attribute"]
    }
    return [:]
}
```

## Pattern Matching Entity Ids

In the event that an aggregate is defined containing multiple entity ids, the below attribute release policy may be used to release a collection of allowed attributes to entity ids grouped together by a regular expression pattern:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "entity-ids-allowed-via-regex",
  "name": "SAML",
  "id": 10,
  "metadataLocation": "path/to/incommon/metadata.xml",
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.support.saml.services.PatternMatchingEntityIdAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "mail", "sn" ] ],
    "fullMatch" : "true",
    "entityIds" : "entityId1|entityId2|somewhere.+"
  }
}
```

## Entity Attributes Filter

This attribute release policy authorizes the release of defined attributes, provided the accompanying metadata for the service provider contains attributes that match certain values.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "entity-ids-allowed-via-regex",
  "name": "SAML",
  "id": 10,
  "metadataLocation": "path/to/metadata.xml",
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.support.saml.services.MetadataEntityAttributesAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "mail", "sn" ] ],
    "entityAttributeValues" : [ "java.util.LinkedHashSet", [ "entity-attribute-value" ] ],
    "entityAttribute" : "http://somewhere.org/category-x",
    "entityAttributeFormat" : "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified"
  }
}
```

The specification of `entityAttributeFormat` is optional.

## Requested Attributes Filter

This attribute release policy authorizes the release of defined attributes, based on the accompanying metadata for the service provider having requested attributes as part of its `AttributeConsumingService` element.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "entity-ids-allowed-via-regex",
  "name": "SAML",
  "id": 10,
  "metadataLocation": "path/to/metadata.xml",
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.support.saml.services.MetadataRequestedAttributesAttributeReleasePolicy",
    "useFriendlyName" : false
  }
}
```

The `useFriendlyName` allows the filter to compare the requested attribute's friendly name with the resolved attribute.
