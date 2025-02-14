---
layout: default
title: CAS - SAML2 Authentication
---

# SAML2 Authentication

CAS can act as a SAML2 identity provider accepting authentication requests and producing SAML assertions.

If you intend to allow CAS to delegate authentication to an external SAML2 identity provider, you need to [review this guide](../integration/Delegate-Authentication.html).

<div class="alert alert-info"><strong>SAML Specification</strong><p>This document solely focuses on what one might do to turn on SAML2 support inside CAS. It is not to describe/explain the numerous characteristics of the SAML2 protocol itself. If you are unsure about the concepts referred to on this page, please start with reviewing the <a href="http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html">SAML2 Specification</a>.</p></div>

## SAML Endpoints

The following CAS endpoints respond to supported SAML2 profiles:

- `/cas/idp/profile/SAML2/Redirect/SSO`
- `/cas/idp/profile/SAML2/POST/SSO`
- `/cas/idp/profile/SAML2/POST/SLO`
- `/cas/idp/profile/SAML2/Redirect/SLO`
- `/cas/idp/profile/SAML2/Unsolicited/SSO`
- `/cas/idp/profile/SAML2/SOAP/ECP`

SAML2 IdP `Unsolicited/SSO` profile supports the following parameters:

| Parameter                         | Description
|-----------------------------------|-----------------------------------------------------------------
| `providerId`                      | Required. Entity ID of the service provider.
| `shire`                           | Optional. Response location (ACS URL) of the service provider.
| `target`                          | Optional. Relay state.
| `time`                            | Optional. Skew the authentication request.

## IdP Metadata

The following CAS endpoints handle the generation of SAML2 metadata:

- `/cas/idp/metadata`

This endpoint will display the CAS IdP SAML2 metadata upon receiving a GET request. If metadata is already available and generated,
it will be displayed. If metadata is absent, one will be generated automatically.
CAS configuration below dictates where metadata files/keys will be generated and stored.

Here is a generated metadata file as an example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EntityDescriptor  xmlns="urn:oasis:names:tc:SAML:2.0:metadata" xmlns:ds="http://www.w3.org/2000/09/xmldsig#"
                xmlns:shibmd="urn:mace:shibboleth:metadata:1.0" xmlns:xml="http://www.w3.org/XML/1998/namespace"
                xmlns:mdui="urn:oasis:names:tc:SAML:metadata:ui" entityID="ENTITY_ID">
    <IDPSSODescriptor protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
        <Extensions>
            <shibmd:Scope regexp="false">SCOPE</shibmd:Scope>
        </Extensions>
        <KeyDescriptor use="signing">
            <ds:KeyInfo>
              <ds:X509Data>
                  <ds:X509Certificate>...</ds:X509Certificate>
              </ds:X509Data>
            </ds:KeyInfo>
        </KeyDescriptor>
        <KeyDescriptor use="encryption">
            <ds:KeyInfo>
              <ds:X509Data>
                  <ds:X509Certificate>...</ds:X509Certificate>
              </ds:X509Data>
            </ds:KeyInfo>
        </KeyDescriptor>

        <NameIDFormat>urn:mace:shibboleth:1.0:nameIdentifier</NameIDFormat>
        <NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</NameIDFormat>

        <SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                             Location="https://HOST_NAME/cas/idp/profile/SAML2/POST/SLO"/>
        <SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                             Location="https://HOST_NAME/cas/idp/profile/SAML2/POST/SSO"/>
        <SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"
                             Location="https://HOST_NAME/cas/idp/profile/SAML2/Redirect/SSO"/>
    </IDPSSODescriptor>
</EntityDescriptor>
```

### Server Configuration

<div class="alert alert-info"><strong>Server Configuration</strong><p>If you have deployed CAS in an external application server/servlet container (i.e. Apache Tomcat) you will need to make sure that the server is adjusted to handle large-enough <code>HttpHeaderSize</code> and <code>HttpPostSize</code> values (i.e. 2097152). The embedded container that ships with CAS handles this automatically.</p></div>

### Mapping Endpoints

Note that CAS metadata endpoints for various bindings are typically available under `/cas/idp/...`. If you
mean you use an existing metadata file whose binding endpoints begin with `/idp/...`, you may need to deploy
CAS at the root context path so it's able to respond to those requests. (i.e. `https://sso.example.org/cas/login` becomes
`https://sso.example.org/login`). Alternatively, you may try to use URL-rewriting route requests from `/idp/` to `/cas/idp/`,etc.

## SP Metadata

If the SP you wish to integrate with does not produce SAML metadata, you may be able to
use [this service](https://www.samltool.com/sp_metadata.php) to create the metadata,
save it in an XML file and then reference and register it with CAS for the SP.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp</artifactId>
  <version>${cas.version}</version>
</dependency>
```

You may also need to declare the following Maven repository in
your CAS overlay to be able to resolve dependencies:

```xml
<repositories>
    ...
    <repository>
        <id>shibboleth-releases</id>
        <url>https://build.shibboleth.net/nexus/content/repositories/releases</url>
    </repository>
    ...
</repositories>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#saml-idp).

### SAML Services

SAML relying parties and services must be registered within the CAS service registry similar to the following example:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "evaluationOrder" : 10,
  "metadataLocation" : "http://www.testshib.org/metadata/testshib-providers.xml"
}
```

The following fields are available for SAML services:

| Field                                | Description
|--------------------------------------|------------------------------------------------------------------
| `metadataLocation`                   | Location of service metadata defined from system files, classpath, directories or URL resources.
| `metadataSignatureLocation`          | Location of the metadata signing certificate/public key to validate the metadata which must be defined from system files or classpath. If defined, will enforce the `SignatureValidationFilter` validation filter on metadata.
| `metadataMaxValidity`                | If defined, will enforce the `RequiredValidUntilFilter` validation filter on metadata.
| `signAssertions`                     | Whether assertions should be signed. Default is `false`.
| `signResponses`                      | Whether responses should be signed. Default is `true`.
| `encryptAssertions`                  | Whether assertions should be encrypted. Default is `false`.
| `requiredAuthenticationContextClass` | If defined, will specify the SAML authentication context class in the final response. If undefined, the authentication class will either be `urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified` or `urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport` depending on the SAML authentication request.
| `requiredNameIdFormat`               | If defined, will force the indicated Name ID format in the final SAML response.
| `metadataCriteriaPattern`            | If defined, will force an entity id filter on the metadata aggregate based on the `PredicateFilter` to include/exclude specific entity ids based on a valid regex pattern.
| `metadataCriteriaDirection`          | If defined, will force an entity id filter on the metadata aggregate based on `PredicateFilter`. Allowed values are `INCLUDE`,`EXCLUDE`.
| `metadataCriteriaRoles`              | If defined, will whitelist the defined metadata roles (i.e. `SPSSODescriptor`, `IDPSSODescriptor`). Default is `SPSSODescriptor`.
| `metadataCriteriaRemoveEmptyEntitiesDescriptors` | Controls whether to keep entities descriptors that contain no entity descriptors. Default is `true`.
| `metadataCriteriaRemoveRolelessEntityDescriptors` | Controls whether to keep entity descriptors that contain no roles. Default is `true`.
| `attributeNameFormats` | Map that defines attribute name formats for a given attribute name to be encoded in the SAML response.
| `nameIdQualifier` | If defined, will overwrite the `NameQualifier` attribute of the produced subject's name id.
| `serviceProviderNameIdQualifier` | If defined, will overwrite the `SPNameQualifier` attribute of the produced subject's name id.


### Metadata Aggregates

CAS services are fundamentally recognized and loaded by service identifiers taught to CAS typically via
regular expressions. This allows for common groupings of applications and services by
url patterns (i.e. "Everything that belongs to `example.org` is registered with CAS).
With aggregated metadata, CAS essentially does double
authorization checks because it will first attempt to find the entity id
in its collection of resolved metadata components and then it looks to
see if that entity id is authorized via the pattern that is assigned to
that service definition. This means you can do one of several things:

1. Open up the pattern to allow everything that is authorized in the metadata.
2. Restrict the pattern to only a select few entity ids found in the
metadata. This is essentially the same thing as defining metadata criteria
to filter down the list of resolved relying parties and entity ids except that its done
after the fact once the metadata is fully loaded and parsed.
3. You can also instruct CAS to filter metadata
entities by a defined criteria at resolution time when it reads the
metadata itself. This is essentially the same thing as forcing the pattern
to match entity ids, except that it's done while CAS is reading the
metadata and thus load times are improved.

### Attribute Name Formats

Attribute name formats can be specified per relying party in the service registry.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "id": 100001,
  "attributeNameFormats":
  {
    "@class": "java.util.HashMap",
    "attributeName": "basic|uri|unspecified|custom-format-etc"
  }
}
```

You may also have the option to define attributes and their relevant name format globally
via CAS properties. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#saml-idp).

### Attribute Release

Attribute filtering and release policies are defined per SAML service.
See [this guide](../integration/Attribute-Release-Policies.html) for more info.

A few additional policies specific to SAML services are also provided below.

#### InCommon Research and Scholarship

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
         {"@class": "org.apereo.cas.support.saml.services.InCommonRSAttributeReleasePolicy"}
      ]
    ]
  }
}
```

#### Pattern Matching Entity Ids

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

### Name ID Selection

Each service may specify a required Name ID format. If left undefined, the metadata will be consulted to find the right format.
The Name ID value is always simply the authenticated user that is designed to be returned to this service. In other words, if you
decide to configure CAS to return a particular attribute as
[the authenticated user name for this service](../integration/Attribute-Release-PrincipalId.html),
that value will then be used to construct the Name ID along with the right format.

### Dynamic Metadata

CAS also supports the [Dynamic Metadata Query Protocol](https://spaces.internet2.edu/display/InCFederation/Metadata+Query+Protocol)
which is a REST-like API for requesting and receiving arbitrary metadata. In order to configure a CAS SAML service to retrieve its metadata
from a Metadata query server, the metadata location must be configured to point to the query server instance. Here is an example:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "evaluationOrder" : 10,
  "metadataLocation" : "http://mdq.server.org/entities/{0}"
}
```

...where `{0}` serves as an entityID placeholder for which metadata is to be queried.

## SP Integrations

A number of SAML2 service provider integrations are provided natively by CAS. To learn more,
please [review this guide](../integration/Configuring-SAML-SP-Integrations.html).

## Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
<AsyncLogger name="org.opensaml" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
```
