---
layout: default
title: CAS - JAAS Authentication
category: Authentication
---

# JAAS Authentication

[JAAS](https://docs.oracle.com/javase/9/security/java-authentication-and-authorization-service-jaas1.htm) is a Java standard
authentication and authorization API. JAAS is configured via externalized plain text configuration file.
Using JAAS with CAS allows modification of the authentication process without having to rebuild and redeploy CAS
and allows for PAM-style multi-module "stacked" authentication.

## Configuration

JAAS components are provided in the CAS core module and require no additional dependencies to use.
The JAAS handler delegates to the built-in JAAS subsystem to perform authentication according to the
directives in the JAAS config file.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#jaas-authentication).

## JAAS Configuration File

The default JAAS configuration file is located at `$JRE_HOME/lib/security/java.security`. It's important to note
that JAAS configuration applies to the entire JVM. The path to the JAAS configuration file in effect may be altered
by setting the `java.security.auth.login.config` system property to an alternate file path (i.e. `file:/etc/cas/config/jaas.config`).

A sample JAAS configuration file is provided for reference.

```
/**
  * Login Configuration for JAAS with the realm name defined as CAS.
  */
CAS {
  org.sample.jaas.login.SampleLoginModule sufficient
    debug=FALSE;
};
```

## Login Modules

The following login modules are available with CAS:

### JNDI

The module prompts for a username and password and then verifies the password against the password stored in a directory service configured under JNDI.

```
CAS {
  com.sun.security.auth.module.JndiLoginModule sufficient
    user.provider.url=name_service_url
    group.provider.url=name_service_url
    debug=FALSE;
};
```

The value of `name_service_url` specifies the directory service and path where this module can access the relevant user and group information. Because this module only performs one-level searches to find the relevant user information, the URL must point to a directory one level above where the user and group information is stored in the directory service.

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/JndiLoginModule.html) for more info.

### Kerberos

This module authenticates users using Kerberos protocols. The configuration entry for module has several options that control the 
authentication process and additions to the `Subject`'s private credential set. Irrespective of these options, the `Subject`'s principal set and private credentials set are updated only when commit is called. When commit is called, the `KerberosPrincipal` is added to the `Subject`'s principal set and KerberosTicket is added to the `Subject`'s private credentials.

If the configuration entry for module has the option `storeKey` set to true, then `KerberosKey` will also be added to 
the subject's private credentials. `KerberosKey`, the principal's key will be either obtained from the keytab or derived from user's password.

This module also recognizes the `doNotPrompt` option. If set to true the user will not be prompted for the password. 
The user can specify the location of the ticket cache by using the option `ticketCache` in the configuration entry. The user can specify the keytab location by using the option `keyTab` in the configuration entry.

The principal name can be specified in the configuration entry by using the option `principal`. The principal name can 
either be a simple user name or a service name such as `host/mission.eng.sun.com`. The principal can also be set using 
the system property `sun.security.krb5.principal`. This property is checked during login. If this property is not set, 
then the principal name from the configuration is used. In the case where the principal property is not set and the principal 
entry also does not exist, the user is prompted for the name. When this property of entry is set, and `useTicketCache` is set to 
true, only TGT belonging to this principal is used.

Note that a valid `krb5.conf` must be supplied to the JVM for Kerberos auth via setting `-Djava.security.krb5.conf=/etc/krb5.conf`.

```
CAS {
  com.sun.security.auth.module.Krb5LoginModule sufficient
    refreshKrb5Config=TRUE/FALSE
    useTicketCache=TRUE/FALSE
    ticketCache=...
    renewTGT=TRUE/FALSE
    useKeyTab=TRUE/FALSE
    doNotPrompt=TRUE/FALSE
    keyTab=TRUE/FALSE
    storeKey=TRUE/FALSE
    principal=...
    debug=FALSE;
};
```

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/Krb5LoginModule.html) for more info.

### UNIX

This module imports a user's Unix Principal information (`UnixPrincipal`, `UnixNumericUserPrincipal`, and `UnixNumericGroupPrincipal`) and associates them with the current `Subject`.

```
CAS {
  com.sun.security.auth.module.UnixLoginModule sufficient
    debug=FALSE;
};
```

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/UnixLoginModule.html) for more info.

### NT

This module renders a user's NT security information as some number of `Principal`s and associates them with a `Subject`.

```
CAS {
  com.sun.security.auth.module.NTLoginModule sufficient
    debugNative=TRUE
    debug=FALSE;
};
```

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/NTLoginModule.html) for more info.

### LDAP

This module performs LDAP-based authentication. A username and password is verified against the corresponding user credentials stored in an LDAP directory. If authentication is successful then a new `LdapPrincipal` is created using the user's distinguished name and a new `UserPrincipal` is created using the user's username and both are associated with the current `Subject`.

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/LdapLoginModule.html) for more info.

This module operates in one of three modes. A mode is selected by specifying a particular set of options: 

#### Search First

In search-first mode, the LDAP directory is searched to determine the user's distinguished name and then authentication is attempted. An (anonymous) search is performed using the supplied username in conjunction with a specified search filter. If successful then authentication is attempted using the user's distinguished name and the supplied password. To enable this mode, set the `userFilter` option and omit the `authIdentity` option. Use `search-first` mode when the user's distinguished name is not known in advance.

The example below identifies the LDAP server and specifies that users' entries be located by their `uid` and `objectClass` attributes. It also specifies that an identity based on the user's `employeeNumber` attribute should be created.

```
CAS {
  com.sun.security.auth.module.LdapLoginModule REQUIRED
    userProvider="ldap://ldap-svr/ou=people,dc=example,dc=com"
    userFilter="(&(uid={USERNAME})(objectClass=inetOrgPerson))"
    authzIdentity="{EMPLOYEENUMBER}"
    debug=true;
};
```

#### Authentication First

In `authentication-first` mode, authentication is attempted using the supplied username and password and then the LDAP directory is searched. If authentication is successful then a search is performed using the supplied username in conjunction with a specified search filter. To enable this mode, set the `authIdentity` and the `userFilter` options. Use `authentication-first` mode when accessing an LDAP directory that has been configured to disallow anonymous searches.

The example below requests that the LDAP server be located dynamically, that authentication be performed using the supplied username directly but without the protection of SSL and that users' entries be located by one of three naming attributes and their `objectClass` attribute.

```
CAS {
  com.sun.security.auth.module.LdapLoginModule REQUIRED
    userProvider="ldap:///cn=users,dc=example,dc=com"
    authIdentity="{USERNAME}"
    userFilter="(&(|(samAccountName={USERNAME})(userPrincipalName={USERNAME})(cn={USERNAME}))(objectClass=user))"
    useSSL=false
    debug=true;
};
```

#### Authentication Only

In `authentication-only` mode, authentication is attempted using the supplied username and password. The LDAP directory is not searched because the user's distinguished name is already known. To enable this mode, set the authIdentity option to a valid distinguished name and omit the userFilter option. Use authentication-only mode when the user's distinguished name is known in advance.

The example below identifies alternative LDAP servers, it specifies the distinguished name to use for authentication and a fixed identity to use for authorization. No directory search is performed.

```
CAS {
  com.sun.security.auth.module.LdapLoginModule REQUIRED
    userProvider="ldap://ldap-svr1 ldap://ldap-svr2"
    authIdentity="cn={USERNAME},ou=people,dc=example,dc=com"
    authzIdentity="staff"
    debug=true;
  };
```

### Ldaptive

Ldaptive provides several [login modules for authentication and authorization](http://www.ldaptive.org/docs/guide/jaas.html) against an LDAP. Each module accepts properties that correspond to the setters on objects in the ldaptive code base. If you are looking to set a specific configuration option that is available as a setter, the chances are that it will be accepted on the module. Any unknown options will be passed to the provider as a generic property.

In order to take advantage of the login modules provided by Ldaptive, the following dependency must be present and added to the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ldap-core</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### Keystore

This module prompts for a key store alias and populates the subject with the alias's principal and credentials. Stores an `X500Principal` for the subject distinguished name of the first certificate in the alias's credentials in the subject's principals, the alias's certificate path in the subject's public credentials, and a `X500PrivateCredential` whose certificate is the first certificate in the alias's certificate path and whose private key is the alias's private key in the subject's private credentials.

```
CAS {
  com.sun.security.auth.module.KeyStoreLoginModule sufficient
    keyStoreURL=...
    keyStoreType=
    keyStoreProvider=...
    keyStoreAlias=...
    keyStorePasswordURL=...
    privateKeyPasswordURL=...
    protected=...
    debug=FALSE;
};
```

For a list of all other options and more comprehensive documentation, please see [this guide](http://docs.oracle.com/javase/8/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/KeyStoreLoginModule.html) for more info.

## Deployments

If your deployment strategy ultimately uses an [embedded container](Configuring-Servlet-Container.html#embedded), 
you can pass along the location of the JAAS configuration file in form of a system property as such:

```bash
java -Djava.security.auth.login.config=file:/etc/cas/config/jaas.config -jar ...
```

Alternatively, you may activate the login configuration type to be `JavaLoginConfig` in the CAS settings and simply specify the path
to the jaas configuration file there in the settings directly.

For more information on configuration management, please [review this guide](../configuration/Configuration-Management.html).
