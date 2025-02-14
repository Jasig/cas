---
layout: default
title: CAS - Delegate Authentication w/ SAML2 Identity Providers
category: Authentication
---

# Delegated Authentication w/ SAML2

In the event that CAS is configured to delegate authentication to an external identity provider, the service provider (CAS) 
metadata as well as the identity provider metadata automatically become available at the following endpoints:

| Endpoint                         | Description
|----------------------------------|---------------------------------------------------------------------------
| `/sp/metadata`                   | Displays the service provider (CAS) metadata. Works well if there is only one SAML2 IdP is defined.
| `/sp/idp/metadata`               | Displays the identity provider metadata. Works well if there is only one SAML2 IdP is defined.
| `/sp/{clientName}/metadata`      | Displays the service provider metadata for the requested client name.
| `/sp/{clientName}/idp/metadata`  | Displays the identity provider metadata for the requested client name.

Note that you can use more than one external identity provider with CAS, where each integration may be done 
with a different set of metadata and keys for CAS acting as the service provider. Each integration (referred to as a client, 
since CAS itself becomes a client of the identity provider) may be given a name optionally.

Remember that the service provider (CAS) metadata is automatically generated once you access the above 
endpoints or view the CAS login screen. This is required because today, generating the metadata requires 
access to the HTTP request/response. In the event that metadata cannot 
be resolved, a status code of `406 - Not Acceptable` is returned.

## Identity Provider Discovery Service

<div class="alert alert-info"><strong>Note</strong><p>Using identity provider discovery requires 
delegated authentication to be available as the feature cannot be used on its own
as a standalone discovery service.</p></div>

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-saml-idp-discovery</artifactId>
    <version>${cas.version}</version>
</dependency>
```    

Identity provider discovery allows CAS to [embed and present a discovery service](https://wiki.shibboleth.net/confluence/display/EDS10/Embedded+Discovery+Service) as part of delegated authentication. Configured SAML2 identity providers in the CAS configuration
used for delegated authentication are presented as options for discovery. 

CAS is also able to directly consume multiple JSON feeds
that contain discovery metadata about available identity providers. The discovery JSON feed 
may be fetched from a URL (i.e. exposed by a Shibboleth Service Provider) or it may
directly be consumed as a JSON file with the following structure:

```json
[{
 "entityID": "https://idp.example.net/idp/saml",
 "DisplayNames": [{
  "value": "Example.net",
  "lang": "en"
  }],
 "Descriptions": [{
  "value": "An identity provider for the people, by the people.",
  "lang": "en"
  }],
 "Logos": [{
  "value": "https://example.net/images/logo.png",
  "height": "90",
  "width": "62"
  }]
}]
```

To see the relevant list of CAS properties, 
please [review this guide](../configuration/Configuration-Properties.html#saml2-identity-provider-discovery).

The following endpoints are available:

| Endpoint                         | Description
|----------------------------------|----------------------------------------------------------------
| `/idp/discovery`                 | Identity provider discovery landing page.
| `/idp/discovery/feed`            | Identity provider discovery JSON feed.
| `/idp/discovery/redirect`        | Return endpoint to let CAS invoke the identity provider after selection. 

Applications may directly invoke the discovery service via `[cas-server-prefix]/idp/discovery`. The discovery service may also 
be invoked using the discovery protocol via `[cas-server-prefix]/idp/discovery?entityID=[service-provider-entity-id]&return=[cas-server-prefix]/idp/discovery/redirect`. 
Additional parameters may be included as part of the `return` url and they all must be encoded.
