---
layout: default
title: CAS - Attribute Definitions 
category: Attributes
---

# Attribute Definitions

The definition of an attribute in CAS, when fetched and resolved from an authentication or attribute repository source, tends to be defined
and referenced using its name without any additional *metadata* or decorations. For example, you may wish to retrieve a `uid` attribute and virtually
rename and map it to a `userIdentifier` attribute either globally or for specific application integrations. For most use cases, this configuration
works quite comfortably and yet, depending on the nature of the target application and the authentication protocol used to complete the integration,
additional requirements could be imposed and may have to be specified to define an attribute with additional pointers, when shared and released with a 
relying party. For example, a SAML2 service provider may require a *scoped* attribute for an `eduPersonPrincipalName` whose value 
is always determined from the `uid` attribute with a special friendly-name that is always provided regardless of the target application. 

While bits and pieces of metadata about a given attribute can be defined either globally in CAS configuration settings 
or defined inside a service definition, an attribute definition store allows one to describe metadata about necessary attributes 
with special decorations to be considered during attribute resolution and release. The specification of the attribute definition store is entirely 
optional and the store may not contain any attribute definitions.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#attribute-definitions).

## JSON Attribute Definitions

Attribute definitions may be defined inside a JSON file whose location is provided via CAS settings. The structure of the JSON file
may match the following:

```json 
{
    "@class" : "java.util.TreeMap",
    "employeeId" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "employeeId",
      "scoped" : true,
      "attribute" : "empl_identifier"
    }
}
```         

Attribute definitions are specified using a `Map` whose key is the attribute name, as resolved by the CAS [attribute resolution engine](Attribute-Resolution.html).
The attribute name as the key to the `Map` must match the `key` attribute of the attribute definition itself.

The following settings can be specified by an attribute definition:

| Name                    | Description
|-------------------------|--------------------------------------------------------------------------------------------------------
| `key`                   | Attribute name, as resolved by the CAS [attribute resolution engine](Attribute-Resolution.html)
| `name`                  | Attribute name to be used and shared with the target application during attribute release.
| `scoped`                | (Optional) If `true`, the attribute value be scoped to the scope of the CAS server deployment defined in settings.
| `encrypted`             | (Optional) If `true`, the attribute value will be encrypted and encoded in base-64 using the service definition's defined public key.
| `attribute`             | (Optional) The source attribute to provide values for the attribute definition itself, replacing that of the original source.
| `patternFormat`         | (Optional) Template used in a `java.text.MessageFormat` to decorate the attribute values.
| `script`                | (Optional) Groovy script, external or embedded to process and produce attributes values.

The following operations in the order given should take place, if an attribute definition is to produce values:

- Produce attribute values based on the `attribute` setting specified in the attribute definition, if any.
- Produce attribute values based on the `script` setting specified in the attribute definition, if any.
- Produce attribute values based on the `scoped` setting specified in the attribute definition, if any.
- Produce attribute values based on the `patternFormat` setting specified in the attribute definition, if any.
- Produce attribute values based on the `encrypted` setting specified in the attribute definition, if any.

## Examples

### Basic

Define an attribute definition for `employeeId` to produce scoped attributes 
based on another attribute `empl_identifier` as the source:

```json 
{
    "@class" : "java.util.TreeMap",
    "employeeId" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "employeeId",
      "scoped" : true,
      "attribute" : "empl_identifier"
    }
}
```  

Now that the definition is available globally, the attribute [can then be released](Attribute-Release-Policies.html) 
as usual with the following definition:

```json
...
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "employeeId" ] ]
  }
...
```

### Encrypted Attribute

Same use case as above, except the attribute value will be encrypted and encoded using the service definition's public key:

```json 
{
    "@class" : "java.util.TreeMap",
    "employeeId" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "employeeId",
      "encrypted" : true,
      "attribute" : "empl_identifier"
    }
}
```  

The service definition should have specified a public key definition:

```json
...
  "publicKey" : {
    "@class" : "org.apereo.cas.services.RegisteredServicePublicKeyImpl",
    "location" : "classpath:public.key",
    "algorithm" : "RSA"
  }
...
```

The keys can be generated via the following commands:

```bash
openssl genrsa -out private.key 1024
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
openssl pkcs8 -topk8 -inform PER -outform DER -nocrypt -in private.key -out private.p8
```

### Pattern Formats

Define an attribute definition to produce values based on a pattern format:

```json 
{
    "@class" : "java.util.TreeMap",
    "eduPersonPrincipalName" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "eduPersonPrincipalName",
      "name" : "urn:oid:1.3.6.1.4.1.5923.1.1.1.6",
      "friendlyName" : "eduPersonPrincipalName",
      "scoped" : true,
      "patternFormat": "hello,{0}",
      "attribute" : "uid"
    }
}
```

If the resolved set of attributes are `uid=[test1, test2]` and the CAS server has a scope of `example.org`, 
the final values of `eduPersonPrincipalName` would be [`hello,test1@example.org`,`hello,test2@example.org`]
released as `urn:oid:1.3.6.1.4.1.5923.1.1.1.6` with a friendly name of `eduPersonPrincipalName`.

### Embedded Script

Same use case as above, except the attribute value be additional processed by an embedded Groovy script

```json 
{
    "@class" : "java.util.TreeMap",
    "eduPersonPrincipalName" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "eduPersonPrincipalName",
      "name" : "urn:oid:1.3.6.1.4.1.5923.1.1.1.6",
      "friendlyName" : "eduPersonPrincipalName",
      "scoped" : true,
      "script": " groovy { logger.info(\" name: ${attributeName}, values: ${attributeValues} \"); return ['hello', 'world'] } "
    }
}
```  

If the CAS server has a scope of `example.org`, 
the final values of `eduPersonPrincipalName` would be [`hello@example.org`, `world@example.org`]
released as `urn:oid:1.3.6.1.4.1.5923.1.1.1.6` with a friendly name of `eduPersonPrincipalName`.

### External Script

Same use case as above, except the attribute value be additionally processed by an external Groovy script:

```json 
{
    "@class" : "java.util.TreeMap",
    "eduPersonPrincipalName" : {
      "@class" : "org.apereo.cas.authentication.attribute.DefaultAttributeDefinition",
      "key" : "eduPersonPrincipalName",
      "name" : "urn:oid:1.3.6.1.4.1.5923.1.1.1.6",
      "friendlyName" : "eduPersonPrincipalName",
      "scoped" : true,
      "script": "file:/attribute-definitions.groovy"
    }
}
```  

The outline of the Groovy script should be defined as:

```groovy
def run(Object[] args) {
    def attributeName = args[0]
    def attributeValues = args[1]
    def logger = args[2]
    def registeredService = args[3]
    def attributes = args[4]
    logger.info("name: ${attributeName}, values: ${attributeValues}")
    return ["casuser", "groovy"]
}
```

If the CAS server has a scope of `example.org`, 
the final values of `eduPersonPrincipalName` would be [`casuser@example.org`, `groovy@example.org`]
released as `urn:oid:1.3.6.1.4.1.5923.1.1.1.6` with a friendly name of `eduPersonPrincipalName`.
