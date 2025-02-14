---
layout: default
title: CAS - Surrogate Authentication
---

# Surrogate Authentication

Surrogate authentication is the ability to authenticate on behalf of another user. The two actors in this case are:

1. The primary admin user whose credentials are verified upon authentication.
2. The surrogate user, selected by the admin, to which CAS will switch after credential verification and is one that is linked to the single sign-on session.

Surrogate authentication is enabled by including the following dependencies in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-surrogate-authentication</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

### Surrogate Account Storage

The following account stores may be configured and used to locate surrogates authorized for a particular user.

#### Static

Surrogate accounts may be defined statically in the CAS configuration. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

#### JSON

Similar to above, except that surrogate accounts may be defined in an external JSON file whose path is specified via the CAS configuration. The syntax of the JSON file should match the following snippet:

```json
{
    "casuser": ["jsmith", "banderson"],
    "adminuser": ["jsmith", "tomhanks"]
}
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

#### LDAP

Surrogate accounts may also be retrieved from an LDAP instance. Such accounts are expected to be found in a configured attribute defined for the primary user in LDAP whose value(s) may be examined against a regular expression pattern of your own choosing to further narrow down the list of authorized surrogate accounts. To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#surrogate-authentication).

### Surrogate Account Selection

The surrogate user selection can happen via the following ways.

#### Preselected

This is the case where the surrogate user identity is known beforehand and is provided to CAS upon login using a special syntax.
When entering credentials, the following syntax should be used:

```bash
[surrogate-userid][separator][primary-userid]
```

For example, if you are `casuser` and you need to switch to `jsmith` as the surrogate user, the credential id provided to CAS would be `jsmith+casuser` where the separator is `+` and can be altered via the CAS configuration. You will need to provide your own password of course.

#### GUI

This is the case where the surrogate user identity is *not* known beforehand, and you wish to choose the account from a prepopulated list. When entering credentials, the following syntax should be used:

```bash
[separator][primary-userid]
```

For example, if you are `casuser` and you need to locate the surrogate account to which you may want to switch, the credential id provided to CAS would be `+casuser` where the separator is `+` and can be altered via the CAS configuration. You will need to provide your own password of course.

