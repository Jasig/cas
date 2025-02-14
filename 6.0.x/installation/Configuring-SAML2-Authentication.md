---
layout: default
title: CAS - SAML2 Authentication
category: Protocols
---

# SAML2 Authentication

CAS can act as a SAML2 identity provider accepting authentication requests and producing SAML assertions.

If you intend to allow CAS to delegate authentication to an external SAML2 identity provider, you need to [review this guide](../integration/Delegate-Authentication.html).

<div class="alert alert-info"><strong>SAML Specification</strong><p>This document solely focuses on what one might do to turn on SAML2 support inside CAS. It is not to describe/explain the numerous characteristics of the SAML2 protocol itself. If you are unsure about the concepts referred to on this page, please start with reviewing the <a href="http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html">SAML2 Specification</a>.</p></div>

## Federation Interop Evaluation

The CAS project strives to conform to the [SAML V2.0 Implementation Profile for Federation Interoperability](https://kantarainitiative.github.io/SAMLprofiles/fedinterop.html). An evaluation of the requirements against the current CAS release is available [here](https://docs.google.com/spreadsheets/d/1NYN5n6AaNxz0UxwkzIDuXMYL1JUKNZZlSzLZEDUw4Aw/edit?usp=sharing). It is recommended that you view, evaluate and comment on functionality that is currently either absent or marked questionable where verification is needed.

## SAML Endpoints

The following CAS endpoints respond to supported SAML2 profiles:

- `/idp/profile/SAML2/Redirect/SSO`
- `/idp/profile/SAML2/POST/SSO`
- `/idp/profile/SAML2/POST-SimpleSign/SSO`
- `/idp/profile/SAML2/POST/SLO`
- `/idp/profile/SAML2/Redirect/SLO`
- `/idp/profile/SAML2/Unsolicited/SSO`
- `/idp/profile/SAML2/SOAP/ECP`
- `/idp/profile/SAML2/SOAP/AttributeQuery`
- `/idp/profile/SAML1/SOAP/ArtifactResolution`

## IdP Metadata

The following CAS endpoints handle the generation of SAML2 metadata:

- `/idp/metadata`

This endpoint will display the CAS IdP SAML2 metadata upon receiving a GET request. If metadata is already available and generated,
it will be displayed. If metadata is absent, one will be generated automatically.
CAS configuration below dictates where metadata files/keys will be generated and stored.

Here is a generated metadata file as an example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EntityDescriptor xmlns="urn:oasis:names:tc:SAML:2.0:metadata" xmlns:ds="http://www.w3.org/2000/09/xmldsig#"
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

SAML2 identity provider metadata can be managed in dynamics ways as well. To learn more, please [review this guide](Configuring-SAML2-DynamicMetadata.html).

## Service Provider Metadata

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

You may also need to declare the following repository in
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

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-idp).

### SAML Services

SAML relying parties and services must be registered within the CAS service registry similar to the following example:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "evaluationOrder" : 10,
  "metadataLocation" : "https://url/to/metadata.xml"
}
```

The following fields are available for SAML services:

| Field                                | Description
|--------------------------------------|------------------------------------------------------------------
| `metadataLocation`                   | Location of service metadata defined from system files, classpath, directories or URL resources.
| `metadataSignatureLocation`          | Location of the metadata signing certificate/public key to validate the metadata which must be defined from system files or classpath. If defined, will enforce the `SignatureValidationFilter` validation filter on metadata.
| `metadataExpirationDuration`         | If defined, will expire metadata in the cache after the indicated duration which will force CAS to retrieve and resolve the metadata again.
| `signAssertions`                     | Whether assertions should be signed. Default is `false`.
| `signResponses`                      | Whether responses should be signed. Default is `true`.
| `encryptAssertions`                  | Whether assertions should be encrypted. Default is `false`.
| `encryptAttributes`                  | Whether assertion attributes should be encrypted. Default is `false`.
| `encryptableAttributes`              | Set of attributes nominated for encryption, disqualifying others absent in this collection. Default (i.e. `*`) is to encrypt all once `encryptAttributes` is true.
| `requiredAuthenticationContextClass` | If defined, will specify the SAML authentication context class in the final response. If undefined, the authentication class will either be `urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified` or `urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport` depending on the SAML authentication request.
| `requiredNameIdFormat`               | If defined, will force the indicated Name ID format in the final SAML response.
| `metadataCriteriaPattern`            | If defined, will force an entity id filter on the metadata aggregate based on the `PredicateFilter` to include/exclude specific entity ids based on a valid regex pattern.
| `metadataCriteriaDirection`          | If defined, will force an entity id filter on the metadata aggregate based on `PredicateFilter`. Allowed values are `INCLUDE`,`EXCLUDE`.
| `metadataCriteriaRoles`              | If defined, will whitelist the defined metadata roles (i.e. `SPSSODescriptor`, `IDPSSODescriptor`). Default is `SPSSODescriptor`.
| `metadataCriteriaRemoveEmptyEntitiesDescriptors` | Controls whether to keep entities descriptors that contain no entity descriptors. Default is `true`.
| `metadataCriteriaRemoveRolelessEntityDescriptors` | Controls whether to keep entity descriptors that contain no roles. Default is `true`.
| `attributeNameFormats` | Map that defines attribute name formats for a given attribute name to be encoded in the SAML response.
| `attributeFriendlyNames` | Map that defines attribute friendly names for a given attribute name to be encoded in the SAML response.
| `nameIdQualifier` | If defined, will overwrite the `NameQualifier` attribute of the produced subject's name id.
| `assertionAudiences` | Comma-separated list of audience urls to include in the assertion, in the addition to the entity id.
| `serviceProviderNameIdQualifier` | If defined, will overwrite the `SPNameQualifier` attribute of the produced subject's name id.
| `skipGeneratingAssertionNameId` | Whether generation of a name identifier should be skipped for assertions. Default is `false`.
| `skipGeneratingSubjectConfirmationInResponseTo` | Whether generation of the `InResponseTo` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationNotOnOrAfter` | Whether generation of the `NotOnOrBefore` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationRecipient` | Whether generation of the `Recipient` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationNotBefore` | Whether generation of the `NotBefore` element should be skipped for subject confirmations. Default is `true`.
| `skipGeneratingSubjectConfirmationNameId` | Whether generation of the `NameID` element should be skipped for subject confirmations. Default is `true`.
| `signingCredentialType` | Acceptable values are `BASIC` and `X509`. This setting controls the type of the signature block produced in the final SAML response for this application. The latter, being the default, encodes the signature in `PEM` format inside a `X509Data` block while the former encodes the signature based on the resolved public key under a `DEREncodedKeyValue` block.

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>You are encouraged to only keep and maintain properties and settings needed for a 
particular integration. It is UNNECESSARY to grab a copy of all service fields and try to configure them yet again based on their default. While 
you may wish to keep a copy as a reference, this strategy would ultimately lead to poor upgrades increasing chances of breaking changes and a messy 
deployment at that.</p></div>

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

### Metadata Resolution

Service provider metadata is fetched and loaded on demand for every service and then cached in a global cache for a 
configurable duration. Subsequent requests for service metadata will always consult the cache first and if missed, 
will resort to actually resolving the metadata by loading or contacting the configured resource. 
Each service provider definition that is registered with CAS may optionally also specifically an expiration period of 
metadata resolution to override the default global value.

#### Dynamic Metadata Resolution

In addition to the more traditional means of managing service provider metadata such as direct XML files or URLs, CAS 
provides support for a number of other strategies to fetch metadata more dynamically with the likes of MDQ and more.
To learn more, please [review this guide](Configuring-SAML2-DynamicMetadata.html).

### Attribute Name Formats

Attribute name formats can be specified per relying party in the service registry.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "metadataLocation" : "../../sp-metadata.xml",
  "id": 100001,
  "attributeNameFormats": {
    "@class": "java.util.HashMap",
    "attributeName": "basic|uri|unspecified|custom-format-etc"
  }
}
```

You may also have the option to define attributes and their relevant name format globally
via CAS properties. To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-idp).

### Attribute Friendly Names

Attribute friendly names can be specified per relying party in the service registry. If there is no friendly name defined for the attribute, the 
attribute name will be used instead in its place. Note that the name of the attribute is one that is designed to be released to the service provider,
specially if the original attribute is *mapped* to a different name.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "metadataLocation" : "../../sp-metadata.xml",
  "id": 100001,
  "attributeFriendlyNames": {
    "@class": "java.util.HashMap",
    "urn:oid:2.5.4.42": "friendly-name-to-use"
  }
}
```

### Attribute Release

Attribute filtering and release policies are defined per SAML service. See [this guide](Configuring-SAML2-Attribute-Release.html) for more info.

### Name ID Selection

Each service may specify a required Name ID format. If left undefined, the metadata will be consulted to find the right format.
The Name ID value is always simply the authenticated user that is designed to be returned to this service. In other words, if you
decide to configure CAS to return a particular attribute as
[the authenticated user name for this service](../integration/Attribute-Release-PrincipalId.html),
that value will then be used to construct the Name ID along with the right format.

## Unsolicited SSO

SAML2 IdP `Unsolicited/SSO` profile supports the following parameters:

| Parameter                         | Description
|-----------------------------------|-----------------------------------------------------------------
| `providerId`                      | Required. Entity ID of the service provider.
| `shire`                           | Optional. Response location (ACS URL) of the service provider.
| `target`                          | Optional. Relay state.
| `time`                            | Optional. Skew the authentication request.

## Attribute Queries

In order to allow CAS to support and respond to attribute queries, you need to make sure the generated metadata has
the `AttributeAuthorityDescriptor` element enabled, with protocol support enabled for `urn:oasis:names:tc:SAML:2.0:protocol`
and relevant binding that corresponds to the CAS endpoint(s). You also must ensure the `AttributeAuthorityDescriptor` tag lists all
`KeyDescriptor` elements and certificates that are used for signing as well as authentication, specially if the SOAP client of the service provider 
needs to cross-compare the certificate behind the CAS endpoint with what is defined for the `AttributeAuthorityDescriptor`. CAS by default 
will always use its own signing certificate for signing of the responses generated as a result of an attribute query.

Also note that support for attribute queries need to be explicitly enabled and the behavior is off by default, given it imposes a burden on 
CAS and the underlying ticket registry to keep track of attributes and responses as tickets and have them be later used and looked up.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-idp).

## Service Provider Integrations

A number of SAML2 service provider integrations are provided natively by CAS. To learn more,
please [review this guide](../integration/Configuring-SAML-SP-Integrations.html).

## Client Libraries

For Java-based applications, the following frameworks may be used to integrate your application with CAS acting as a SAML2 identity provider:

- [Spring Security SAML](http://projects.spring.io/spring-security-saml/)
- [Pac4j](http://www.pac4j.org/docs/clients/saml.html)

## Sample Client Applications

- [Spring Security SAML Sample Webapp](https://github.com/cas-projects/saml2-sample-java-webapp)
- [Okta](https://developer.okta.com/standards/SAML/setting_up_a_saml_application_in_okta)

## Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
<AsyncLogger name="org.opensaml" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="PROTOCOL_MESSAGE" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
```
