---
layout: default
title: CAS - Attribute Resolution
---

# Attribute Resolution

Attribute resolution strategies are controlled by
the [Person Directory project](https://github.com/apereo/person-directory).
The Person Directory dependency is automatically bundled with the CAS server. Therefor,
declaring an additional dependency will not be required.
This Person Directory project supports both LDAP and JDBC attribute resolution,
caching, attribute aggregation from multiple attribute sources, etc.

<div class="alert alert-info"><strong>Default Caching Policy</strong><p>By default,
attributes are cached to the length of the SSO session.
This means that while the underlying component provided by Person Directory may have
a different caching model, attributes by default and from
a CAS perspective will not be refreshed and retrieved again on subsequent requests
as long as the SSO session exists.</p></div>

## Person Directory

A framework for resolving persons and attributes from a variety of underlying sources.
It consists of a collection of components that retrieve, cache, resolve, aggregate,
merge person attributes from JDBC, LDAP and more.

Attributes sources are defined and configured to describe the global set of attributes to be fetched
for each authenticated principal. That global set of attributes is then filtered by the
service manager according to service-specific attribute release rules.

The goal of the resolver is to construct a final authenticated principal for CAS that is identified by an id carries a number of attributes inside it. The behavior of the person-directory resolver is such that it attempts to locate the principal id, which in most cases is the same thing as the credential id provided during authentication or it could be a noted by a custom attribute. Then it starts to construct attributes from attribute repositories defined. If it realizes that a custom attribute is used to determine the principal id AND the same attribute is also set to be collected into the final set of attributes, it will then remove that attribute from the final collection because its value would become the principal id.

Suppose CAS is configured to authenticate against Active Directory. The account whose details are defined below
authenticates via `sAMAccountName`.

| Attribute            | Value
|------------|--------------------------------
| `sAMAccountName`     | `johnsmith`
| `cn`                 | `John Smith`

### Example #1

If the resolver is configured to use `sAMAccoutName` as the attribute for the principal id, then When authentication is complete the resolver attempts to construct attributes from attribute repository sources. It then sees `sAMAccoutName` as the attribute and sees the principal id is to be created by `sAMAccoutName`. So it would remove the `sAMAccoutName` from the attributes.
The final result is is a principal whose id is `johnsmith` who has a `cn` attribute of `John Smith`.

### Example #2

If the resolver is configured to use `cn` as the attribute for the principal id, then When authentication is complete the resolver attempts to construct attributes from attribute repository sources. It then sees `sAMAccoutName` as the attribute and sees the principal id is to be created by `cn`. So it would remove the `cn` from the attributes. The final result is is a principal whose id is `John Smith`
who has a `sAMAccountName` attribute of `johnsmith`.

Note that by default, CAS auto-creates attribute repository sources that are appropriate for LDAP and JDBC sources, etc.
If you need something more, you will need to resort to more elaborate measures of defining the bean configuration directly
on your own.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).
More about the Person Directory and its configurable sources [can be found here](https://github.com/apereo/person-directory).

<div class="alert alert-info"><strong>Principal Resolution</strong><p>Note that in most if not all cases,
CAS authentication is able to retrieve and resolve attributes from the authentication source, which would
eliminate the need for configuring a separate DAO specially if both the authentication and the attribute source are the same.
Using separate resolvers should only be required when sources are different, or when there is a need to tackle more advanced attribute
resolution use cases such as cascading, merging, etc. <a href="../installation/Configuring-Principal-Resolution.html">See this guide</a> for more info.</p></div>

## Shibboleth

<div class="alert alert-warning"><strong>Tread Lightly!</strong><p>Note that this module is <strong>EXPERIMENTAL</strong>.</p></div>

Uses a Shibboleth `attribute-resolver.xml` style file to define and populate person attributes.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-shibboleth-attributes</artifactId>
    <version>${cas.version}</version>
</dependency>
```

You may also need to declare the following Maven repository in your
CAS overlay to be able to resolve dependencies:

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

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).
