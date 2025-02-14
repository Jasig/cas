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

Note that the endpoint can accept a `service` parameter either by entity id or numeric identifier. This parameter
is matched against the CAS service registry allowing the endpoint to calculate and combine any identity provider 
metadata overrides that may have been specified.

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

### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata artifacts that would be applicable to a specific service definition and managed via the file system need to be stored
in a directory location named using the following pattern `<service_name>-<service_numeric_identifier>`
inside the canonical metadata directory. For example, if global metadata artifacts are managed on disk at `/etc/cas/config/saml/metadata`,
then metadata applicable to a service definition whose name is configured as `SampleService` and its numeric identifier is `1`,
are expected to be found at `/etc/cas/config/saml/metadata/SampleService-1`.

SAML2 identity provider metadata can be managed in dynamics ways as well. To learn more, please [review this guide](Configuring-SAML2-DynamicMetadata.html).

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

### Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint          | Description
|-------------------|-------------------------------------------------------------------------------------------------------
| `samlPostProfileResponse` | Obtain a SAML2 response payload by supplying a `username`, `password` and `entityId` as parameters.

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
| `requireSignedRoot`                  | Whether incoming metadata's root element is required to be signed. Default is `true`.
| `signUnsolicitedAuthnRequest`        | When dealing with Unsolicited SSO, determine whether the authentication request should be forcefully signed.
| `signAssertions`                     | Whether assertions should be signed. Default is `false`.
| `signResponses`                      | Whether responses should be signed. Default is `true`.
| `encryptionOptional`                 | Encrypt whenever possible (i.e a compatible key is found in the peer's metadata) or skip encryption otherwise. Default is `false`.
| `encryptAssertions`                  | Whether assertions should be encrypted. Default is `false`.
| `encryptAttributes`                  | Whether assertion attributes should be encrypted. Default is `false`.
| `encryptableAttributes`              | Set of attributes nominated for encryption, disqualifying others absent in this collection. Default (i.e. `*`) is to encrypt all once `encryptAttributes` is true.
| `requiredAuthenticationContextClass` | If defined, will specify the SAML authentication context class in the final response. If undefined, the authentication class will either be `urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified` or `urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport` depending on the SAML authentication request.
| `requiredNameIdFormat`               | If defined, will force the indicated Name ID format in the final SAML response.
| `skewAllowance`                      | If defined, indicates number of seconds used to skew authentication dates such as valid-from and valid-until elements, etc.
| `metadataCriteriaPattern`            | If defined, will force an entity id filter on the metadata aggregate based on the `PredicateFilter` to include/exclude specific entity ids based on a valid regex pattern.
| `metadataCriteriaDirection`          | If defined, will force an entity id filter on the metadata aggregate based on `PredicateFilter`. Allowed values are `INCLUDE`,`EXCLUDE`.
| `metadataCriteriaRoles`              | If defined, will whitelist the defined metadata roles (i.e. `SPSSODescriptor`, `IDPSSODescriptor`). Default is `SPSSODescriptor`.
| `metadataCriteriaRemoveEmptyEntitiesDescriptors` | Controls whether to keep entities descriptors that contain no entity descriptors. Default is `true`.
| `metadataCriteriaRemoveRolelessEntityDescriptors` | Controls whether to keep entity descriptors that contain no roles. Default is `true`.
| `attributeNameFormats` | Map that defines attribute name formats for a given attribute name to be encoded in the SAML response.
| `attributeFriendlyNames` | Map that defines attribute friendly names for a given attribute name to be encoded in the SAML response.
| `attributeValueTypes` | Map that defines the type of attribute values for a given attribute name.
| `nameIdQualifier` | If defined, will overwrite the `NameQualifier` attribute of the produced subject's name id.
| `issuerEntityId` | If defined, will override the issue value with the given identity provider entity id. This may be useful in cases where CAS needs to maintain multiple identity provider entity ids.
| `assertionAudiences` | Comma-separated list of audience urls to include in the assertion, in the addition to the entity id.
| `serviceProviderNameIdQualifier` | If defined, will overwrite the `SPNameQualifier` attribute of the produced subject's name id.
| `skipGeneratingAssertionNameId` | Whether generation of a name identifier should be skipped for assertions. Default is `false`.
| `skipGeneratingTransientNameId` | Whether transient name identifier generation should be skipped. Default is `false`.
| `skipGeneratingSubjectConfirmationInResponseTo` | Whether generation of the `InResponseTo` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationNotOnOrAfter` | Whether generation of the `NotOnOrBefore` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationRecipient` | Whether generation of the `Recipient` element should be skipped for subject confirmations. Default is `false`.
| `skipGeneratingSubjectConfirmationNotBefore` | Whether generation of the `NotBefore` element should be skipped for subject confirmations. Default is `true`.
| `skipGeneratingSubjectConfirmationNameId` | Whether generation of the `NameID` element should be skipped for subject confirmations. Default is `true`.
| `signingCredentialFingerprint` | `SHA-1` digest of the signing credential's public key, parsed as a regular expression, used for the purposes of key rotation when dealing with multiple credentials.
| `signingCredentialType` | Acceptable values are `BASIC` and `X509`. This setting controls the type of the signature block produced in the final SAML response for this application. The latter, being the default, encodes the signature in `PEM` format inside a `X509Data` block while the former encodes the signature based on the resolved public key under a `DEREncodedKeyValue` block.
| `signingSignatureReferenceDigestMethods` | Collection of signing signature reference digest methods, if any, to override the global defaults.
| `signingKeyAlgorithm` | Signing key algorithm to forcibly use for signing operations when loading the private key. Default is `RSA`.
| `signingSignatureAlgorithms` | Collection of signing signature algorithms, if any, to override the global defaults.
| `signingSignatureBlackListedAlgorithms` | Collection of signing signature blacklisted algorithms, if any, to override the global defaults.
| `signingSignatureWhiteListedAlgorithms` | Collection of signing signature whitelisted algorithms, if any, to override the global defaults.
| `signingSignatureCanonicalizationAlgorithm` | The signing signature canonicalization algorithm, if any, to override the global defaults.
| `encryptionDataAlgorithms` | Collection of encryption data algorithms, if any, to override the global defaults.
| `encryptionKeyAlgorithms` | Collection of encryption key transport algorithms, if any, to override the global defaults.
| `encryptionBlackListedAlgorithms` | Collection of encryption blacklisted algorithms, if any, to override the global defaults.
| `encryptionWhiteListedAlgorithms` | Collection of encryption whitelisted algorithms, if any, to override the global defaults.
| `whiteListBlackListPrecedence` | Preference value indicating which should take precedence when both whitelist and blacklist are non-empty. Accepted values are `WHITELIST` or `BLACKLIST`. Default is `WHITELIST`. 

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

### Security Configuration

There are several levels of configuration that control the security configuration of objects that are signed, encrypted, etc. These configurations include things 
like the keys to use, preferred/default algorithms, and algorithm whitelists or blacklists to enforce. 

The configurations are generally determined based on the following order:

- Service provider metadata
- Per-service configuration overrides
- Global CAS default settings
- OpenSAML initial defaults

In almost all cases, you should leave the defaults in place.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-algorithms--security).

#### Encryption

The following examples demonstrate encryption security configuration overrides per service provider.

#### CBC

The following example demonstrates how to configure CAS to use `CBC` encryption for a particular service provider:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "sp.example.org",
  "name": "SAML",
  "id": 1,
  "metadataLocation": "/path/to/sp-metadata.xml",
  "encryptionDataAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2001/04/xmlenc#aes128-cbc"
    ]
  ],
  "encryptionKeyAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"
    ]
  ]
}
```

#### GCM

The following example demonstrates how to configure CAS to use `GCM` encryption for a particular service provider:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "sp.example.org",
  "name": "SAML",
  "id": 1,
  "metadataLocation": "/path/to/sp-metadata.xml",
  "encryptionDataAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2009/xmlenc11#aes128-gcm"
    ]
  ],
  "encryptionKeyAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"
    ]
  ]
}
```

#### Signing

The following examples demonstrate signing security configuration overrides per service provider.

##### SHA-1

The following example demonstrates how to configure CAS to use `SHA-1` signing and digest algorithms for a particular service provider:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "sp.example.org",
  "name": "SAML",
  "id": 1,
  "metadataLocation": "/path/to/sp-metadata.xml",
  "signingSignatureAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
      "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"
    ]
  ],
  "signingSignatureReferenceDigestMethods": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2000/09/xmldsig#sha1"
    ]
  ]
}
```

##### SHA-256

The following example demonstrates how to configure CAS to use `SHA-256` signing and digest algorithms for a particular service provider:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "sp.example.org",
  "name": "SAML",
  "id": 1,
  "metadataLocation": "/path/to/sp-metadata.xml",
  "signingSignatureAlgorithms": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
      "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"
    ]
  ],
  "signingSignatureReferenceDigestMethods": [
    "java.util.ArrayList",
    [
      "http://www.w3.org/2001/04/xmlenc#sha256"
    ]
  ]
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

#### Examples

The following service definition instructs CAS to use the `urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress` as the final Name ID format,
and use the `mail` attribute value as the final Name ID value.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "metadataLocation": "/path/to/sp-metadata.xml",
  "id": 1,
  "requiredNameIdFormat": "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.PrincipalAttributeRegisteredServiceUsernameProvider",
    "usernameAttribute" : "mail",
  }
}
```

The following service definition instructs CAS to use the `urn:oasis:names:tc:SAML:2.0:nameid-format:transient` as the final Name ID format,
and use the `cn` attribute value in upper-case as the final Name ID value, skipping the generation of transient value per the required format.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "metadataLocation": "/path/to/sp-metadata.xml",
  "id": 1,
  "requiredNameIdFormat": "urn:oasis:names:tc:SAML:2.0:nameid-format:transient",
  "skipGeneratingTransientNameId" : true,
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.PrincipalAttributeRegisteredServiceUsernameProvider",
    "usernameAttribute" : "cn",
    "canonicalizationMode" : "UPPER"
  }
}
```

The following service definition instructs CAS to use the `cn` attribute value to create a persistent Name ID.

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": "the-entity-id-of-the-sp",
  "name": "SAML Service",
  "metadataLocation": "/path/to/sp-metadata.xml",
  "id": 1,
  "requiredNameIdFormat": "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.AnonymousRegisteredServiceUsernameAttributeProvider",
    "persistentIdGenerator" : {
      "@class" : "org.apereo.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator",
      "salt" : "aGVsbG93b3JsZA==",
      "attribute": "cn"
    }
  }
}
```
  
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

## Service Provider Metadata

If the SP you wish to integrate with does not produce SAML metadata, you may be able to
use [this service](https://www.samltool.com/sp_metadata.php) to create the metadata,
save it in an XML file and then reference and register it with CAS for the SP.

Alternatively, you may take advantage of a standalone `saml-sp-metadata.json` file that may be found in the same directory
as the CAS metadata artifacts. The contents of this file may be as follows:

```json
{
  "https://example.org/saml": {
    "entityId": "https://example.org/saml",
    "certificate": "MIIDUj...",
    "assertionConsumerServiceUrl": "https://example.org/sso/"
  }
}
```

Each entry in the file is identified by the service provider entity id, allowing CAS to dynamically locate and build the required metadata on the fly
to resume the authentication flow. This may prove easier for those service providers that only present a URL and a signing certificate for the
integration relieving you from creating and managing XML metadata files separately.
 
The service providers are registered with the CAS service registry as such:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "https://example.org/saml",
  "name" : "SAMLService",
  "id" : 10000003,
  "metadataLocation" : "json://"
}
```
 
<div class="alert alert-info"><strong>Metadata Location</strong><p>The metadata location in the registration record above simply needs to be specified as <code>json://</code> to signal to CAS that SAML metadata for registered service provider must be fetched from the designated JSON file.</p></div>
 
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
