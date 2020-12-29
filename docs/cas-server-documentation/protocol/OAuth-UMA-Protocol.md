---
layout: default
title: CAS - OAuth User-Managed Access Protocol
category: Protocols
---

{% include variables.html %}

# User-Managed Access Protocol

User-Managed Access (UMA) is a lightweight access control protocol that defines 
a centralized workflow to allow an entity (user or corporation) 
to manage access to their resources.

To learn more about UMA, please [read the specification](https://docs.kantarainitiative.org/uma/rec-uma-core.html).

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-oauth-uma" %}

{% include {{ version }}/uma-configuration.md %}

## Resources Storage

Resource definitions are by default kept inside an in-memory repository. 

CAS also provides an alternative implementation backed by the relational database
of choice to track and manage such definitions. The repository choice is activated in CAS properties.

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-oauth-uma-jpa" %}

{% include {{ version }}/rdbms-configuration.md configKey="cas.authn.uma.resource-set.jpa" %}

## Endpoints

### Requesting Party Token

Issue a `GET` request to `/oauth2.0/umaJwks` to retrieve signing public keys.

### Policies

#### Create

Issue a `POST` request to `/oauth2.0/${resourceId}/policy` with the payload body as:

```json
{
  "permissions": [{
    "subject": "casuser",
    "scopes": ["read","write"],
    "claims": {
        "givenName": "CAS"
      }
    }]
}
```

#### Delete

Issue a `DELETE` request as `/oauth2.0/${resourceId}/policy/${policyId}`

#### Update

Issue a `PUT` request as `/oauth2.0/${resourceId}/policy/${policyId}` with the payload body as one matching the `POST` method.

#### Find

- Issue a `GET` request as `/oauth2.0/${resourceId}/policy/` to fetch all policy definitions for a resource.
- Issue a `GET` request as `/oauth2.0/${resourceId}/policy/${policyId}` to fetch a specific policy definition for a resource.

### Resources

Resource-related operations are handled at endpoint `/oauth2.0/resourceSet`.

#### Create

The expected `POST` payload body is:

```json
{
  "uri": "...",
  "type": "...",
  "name": "...",
  "icon_uri": "...",
  "resource_scopes": ["read","write"]
}
```

#### Delete

Issue a `DELETE` request as `${resourceSetEndpoint}/${resourceId}`

#### Update

Issue a `PUT` request as `${resourceSetEndpoint}/${resourceId}` with the payload body as one matching the `POST` method.

#### Find

- Issue a `GET` request as `${resourceSetEndpoint}/${resourceId}` to fetch a specific resource definition. 
- Issue a `GET` request as `${resourceSetEndpoint}` to fetch all resource definitions.

### Permission Tickets

Issue a `POST` request to `/oauth2.0/permission` with the payload body as:

```json
{
    "claims": {"givenName":"CAS"},
    "resource_id": 100,
    "resource_scopes": ["read"]
}
```

### Claims Collection

Issue a `GET` request to `/oauth2.0/rqpClaims` with the following query parameters:

- `client_id`
- `redirect_uri`
- `ticket`
- `state` (Optional)

### Discovery

UMA discovery is available via `GET` at `/oauth2.0/.well-known/uma-configuration`.

### Authorization

Issue a `POST` request to `/oauth2.0/rptAuthzRequest` with the payload body as:

```json
{
    "ticket": "...",
    "rpt": "...",
    "grant_type":"urn:ietf:params:oauth:grant-type:uma-ticket",
    "claim_token": "...",
    "claim_token_format": "..."
}
```
