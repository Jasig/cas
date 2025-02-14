---
layout: default
title: CAS - Configuring Principal Resolution
category: Configuration
---

# Overview
Principal resolution converts information in the authentication credential into a security principal
that commonly contains additional
metadata attributes (i.e. user details such as affiliations, group membership, email, display name).

A CAS principal contains a unique identifier by which the authenticated user will be known to all requesting
services. A principal also contains optional [attributes that may be released](../integration/Attribute-Release.html)
to services to support authorization and personalization. Principal resolution is a requisite part of the
authentication process that happens after credential authentication.

CAS `AuthenticationHandler` components provide basic principal resolution machinery by default. For example,
the `LdapAuthenticationHandler` component supports fetching attributes and setting the principal ID attribute from
an LDAP query. In all cases principals are resolved from the same store as that which provides authentication.

In many cases it is necessary to perform authentication by one means and resolve principals by another.
The `PrincipalResolver` component provides this functionality. A common use case for this this mix-and-match strategy
arises with X.509 authentication. It is common to store certificates in an LDAP directory and query the directory to
resolve the principal ID and attributes from directory attributes. The `X509CertificateAuthenticationHandler` may
be be combined with an LDAP-based principal resolver to accommodate this case.

## Configuration

CAS uses the Person Directory library to provide a flexible principal resolution services against a number of data
sources. The key to configuring `PersonDirectoryPrincipalResolver` is the definition of an `IPersonAttributeDao` object.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#principal-resolution).

## PrincipalResolver vs. AuthenticationHandler

The principal resolution machinery provided by `AuthenticationHandler` components should be used in preference to
`PrincipalResolver` in any situation where the former provides adequate functionality.
If the principal that is resolved by the authentication handler
suffices, then a `null` value may be passed in place of the resolver bean id in the final map.

