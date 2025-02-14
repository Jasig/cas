---
layout: default
title: CAS - LDAP Authentication
---

# LDAP Authentication
LDAP integration is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-support-ldap</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration
The `LdapAuthenticationHandler` must first be configured to handle the task of credential verification. The following
configuration needs to be put into the `deployerConfigContext.xml` file:

```xml
<bean id="ldapAuthenticationHandler"
class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="sAMAccountName"
      c:authenticator-ref="authenticator" />
```

The above configuration attempts to authenticate credentials via an `authenticator` to be defined later, and constructs a final CAS authenticated subject based on the `sAMAccountName`. The attribute configuration is an optional setting.

The `ldapAuthenticationHandler` defined above needs to also be added to the list of available active authentication handlers in the same file:

```xml
<util:map id="authenticationHandlersResolvers">
   ...
   <entry key-ref="ldapAuthenticationHandler" value-ref="primaryPrincipalResolver" />
   ...
</util:map>
```

## Attribute Retrieval
The `LdapAuthenticationHandler` is also capable of resolving and retrieving principal attributes independently without the need for [extra principal resolver machinery](../integration/Attribute-Resolution.html).

```xml
<bean id="ldapAuthenticationHandler"
class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="sAMAccountName"
      c:authenticator-ref="authenticator">
    <property name="principalAttributeMap">
        <map>
            <entry key="displayName" value="simpleName" />
            <entry key="mail" value="email" />
            <entry key="memberOf" value="membership" />
        </map>
    </property>
</bean>
```

The above configuration defines a map of attributes. Keys are LDAP attribute names and values are CAS attribute names which allow you to optionally, retrieve a given attribute and release it under a virtual name. (i.e. Retrieve `mail` from LDAP and remap/rename it to `email` to be released later). If you have no need for this virtual mapping mechanism, you could directly specify the attributes as a list, in which case the above configuration would become:

```xml
<bean id="ldapAuthenticationHandler"
    class="org.jasig.cas.authentication.LdapAuthenticationHandler"
    p:principalIdAttribute="sAMAccountName"
    c:authenticator-ref="authenticator">
    <property name="principalAttributeList">
       <list>
          <value>displayName</value>
          <value>mail</value>
          <value>memberOf</value>
       </list>
    </property>
</bean>
```

If you do decide to let the authentication handler retrieve attributes instead of a separate principal resolver, you will need to ensure the linked resolver is made inactive:

```xml
<util:map id="authenticationHandlersResolvers">
   ...
   <entry key-ref="ldapAuthenticationHandler" value="#{null}" />
</util:map>
```

## Schema Declaration
LDAP authentication is declared using a custom schema to reduce configuration noise. Before configuration, ensure that 
the XML configuration file contains the `ldaptive` namespace declarations:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:ldaptive="http://www.ldaptive.org/schema/spring-ext"
       xsi:schemaLocation="
       http://www.ldaptive.org/schema/spring-ext
       http://www.ldaptive.org/schema/spring-ext.xsd">
```

## Directory Authenticator
`LdapAuthenticationHandler` authenticates a username/password against an LDAP directory such as Active Directory or OpenLDAP. There are numerous directory architectures and we provide configuration for four common cases:

1. [Active Directory](#active-directory-authentication) - Users authenticate with _sAMAccountName_.
2. [Authenticated Search](#ldap-requiring-authenticated-search) - Manager bind/search followed by user simple bind.
3. [Anonymous Search](#ldap-supporting-anonymous-search) - Anonymous search followed by user simple bind.
4. [Direct Bind](#ldap-supporting-direct-bind) - Compute user DN from format string and perform simple bind.

See the [ldaptive documentation](http://www.ldaptive.org/) for more information or to accommodate other situations.

## Active Directory Authentication
The following configuration authenticates users with a custom filter,
which requires manager/administrator credentials in most cases. If the filter is left blank,
the authentication is executed by _UPN_ without performing a search. It is
therefore the most performant and secure solution for the typical Active Directory deployment.
Simply copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.

```xml
<ldaptive:ad-authenticator id="authenticator"
        ldapUrl="${ldap.url}"
        userFilter="${ldap.authn.searchFilter}"
        bindDn="${ldap.managerDn}"
        bindCredential="${ldap.managerPassword}"
        allowMultipleDns="${ldap.allowMultipleDns:false}"
        connectTimeout="${ldap.connectTimeout}"
        validateOnCheckOut="${ldap.pool.validateOnCheckout}"
        failFastInitialize="true"
        blockWaitTime="${ldap.pool.blockWaitTime}"
        idleTime="${ldap.pool.idleTime}"
        baseDn="${ldap.baseDn}"
        maxPoolSize="${ldap.pool.maxSize}"
        minPoolSize="${ldap.pool.minSize}"
        validatePeriodically="${ldap.pool.validatePeriodically}"
        validatePeriod="${ldap.pool.validatePeriod}"
        prunePeriod="${ldap.pool.prunePeriod}"
        useSSL="${ldap.use.ssl:false}"
        subtreeSearch="${ldap.subtree.search:true}"
        useStartTLS="${ldap.useStartTLS}" />
```

## LDAP Authenticated Search
The following configuration snippet provides a template for LDAP authentication performed with manager credentials
followed by a bind. Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.

```xml
<ldaptive:bind-search-authenticator id="authenticator"
        ldapUrl="${ldap.url}"
        baseDn="${ldap.baseDn}"
        userFilter="${ldap.authn.searchFilter}"
        bindDn="${ldap.managerDn}"
        bindCredential="${ldap.managerPassword}"
        connectTimeout="${ldap.connectTimeout}"
        useStartTLS="${ldap.useStartTLS}"
        blockWaitTime="${ldap.pool.blockWaitTime}"
        maxPoolSize="${ldap.pool.maxSize}"
        allowMultipleDns="${ldap.allowMultipleDns:false}"
        usePasswordPolicy="${ldap.usePpolicy:false}"
        minPoolSize="${ldap.pool.minSize}"
        validateOnCheckOut="${ldap.pool.validateOnCheckout}"
        validatePeriodically="${ldap.pool.validatePeriodically}"
        validatePeriod="${ldap.pool.validatePeriod}"
        idleTime="${ldap.pool.idleTime}"
        prunePeriod="${ldap.pool.prunePeriod}"
        failFastInitialize="true"
        subtreeSearch="${ldap.subtree.search:true}"
        useSSL="${ldap.use.ssl:false}"
/>
```

## LDAP Anonymous Search
The following configuration snippet provides a template for LDAP authentication performed with an anonymous search
followed by a bind. Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.

```xml
<ldaptive:anonymous-search-authenticator id="authenticator"
       ldapUrl="${ldap.url}"
       connectTimeout="${ldap.connectTimeout}"
       validateOnCheckOut="${ldap.pool.validateOnCheckout}"
       failFastInitialize="true"
       blockWaitTime="${ldap.pool.blockWaitTime}"
       idleTime="${ldap.pool.idleTime}"
       maxPoolSize="${ldap.pool.maxSize}"
       minPoolSize="${ldap.pool.minSize}"
       validatePeriodically="${ldap.pool.validatePeriodically}"
       validatePeriod="${ldap.pool.validatePeriod}"
       prunePeriod="${ldap.pool.prunePeriod}"
       useSSL="${ldap.use.ssl:false}"
       useStartTLS="${ldap.useStartTLS}"
       usePasswordPolicy="${ldap.usePpolicy:true}"
       allowMultipleDns="${ldap.allowMultipleDns:false}"
       baseDn="${ldap.baseDn}"
       subtreeSearch="${ldap.subtree.search:true}"
       userFilter="${ldap.authn.searchFilter}"
/>
```

## LDAP Direct Bind
The following configuration snippet provides a template for LDAP authentication where no search is required to
compute the DN needed for a bind operation. There are two requirements for this use case:

1. All users are under a single branch in the directory, e.g. `ou=Users,dc=example,dc=org`.
2. The username provided on the CAS login form is part of the DN, e.g. `uid=%s,ou=Users,dc=exmaple,dc=org`.

Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.

```xml
<ldaptive:direct-authenticator id="authenticator"
        format="${ldap.authn.searchFilter}"
        ldapUrl="${ldap.url}"
        connectTimeout="${ldap.connectTimeout}"
        validateOnCheckOut="${ldap.pool.validateOnCheckout}"
        failFastInitialize="true"
        blockWaitTime="${ldap.pool.blockWaitTime}"
        idleTime="${ldap.pool.idleTime}"
        usePasswordPolicy="${ldap.usePpolicy:false}"
        maxPoolSize="${ldap.pool.maxSize}"
        minPoolSize="${ldap.pool.minSize}"
        validatePeriodically="${ldap.pool.validatePeriodically}"
        validatePeriod="${ldap.pool.validatePeriod}"
        prunePeriod="${ldap.pool.prunePeriod}"
        useSSL="${ldap.use.ssl:false}"
        useStartTLS="${ldap.useStartTLS}" 
/>
```

## LDAP Provider Configuration
In certain cases, it may be desirable to use a specific provider implementation when
attempting to establish connections to LDAP. In order to do this, the `authenticator`
configuration must be modified to include a reference to the selected provider.

Here's an example for configuring an UnboundID provider:

```xml
...
<ldaptive:ad-authenticator id="authenticator"
    ...
    provider="org.ldaptive.provider.unboundid.UnboundIDProvider"
    ...
/>
...
```

Note that additional dependencies must be available to CAS at runtime depending on the probider, so it's able to locate the provider implementation and supply that to connections.

## LDAP Properties Starter
The following LDAP configuration properties provide a reasonable starting point for configuring the LDAP
authentication handler. The `ldap.url` property must be changed at a minumum. LDAP properties may be added to the
`cas.properties` configuration file; alternatively they may be isolated in an `ldap.properties` file and loaded
into the Spring application context by modifying the `propertyFileConfigurer.xml` configuration file.

```properties
#========================================
# General properties
#========================================
ldap.url=ldap://localhost:1389

# Start TLS for SSL connections
ldap.useStartTLS=false

# Directory root DN
ldap.rootDn=dc=example,dc=org

# Base DN of users to be authenticated
ldap.baseDn=ou=people,dc=example,dc=org

# LDAP connection timeout in milliseconds
ldap.connectTimeout=3000

# Manager credential DN
ldap.managerDn=cn=Directory Manager,dc=example,dc=org

# Manager credential password
ldap.managerPassword=Password

#========================================
# LDAP connection pool configuration
#========================================
ldap.pool.minSize=1
ldap.pool.maxSize=10
ldap.pool.validateOnCheckout=false
ldap.pool.validatePeriodically=true

# Amount of time in milliseconds to block on pool exhausted condition
# before giving up.
ldap.pool.blockWaitTime=3000

# Frequency of connection validation in seconds
# Only applies if validatePeriodically=true
ldap.pool.validatePeriod=300

# Attempt to prune connections every N seconds
ldap.pool.prunePeriod=300

# Maximum amount of time an idle connection is allowed to be in
# pool before it is liable to be removed/destroyed
ldap.pool.idleTime=600

#========================================
# Authentication
#========================================
ldap.authn.searchFilter=cn={user}

# Ldap domain used to resolve dn
ldap.domain=example.org

# Should LDAP Password Policy be enabled?
ldap.usePpolicy=false

# Allow multiple DNs during authentication?
ldap.allowMultipleDns=false
```

## LDAP Password Policy Enforcement
The purpose of the LPPE is twofold:

- Detect a number of scenarios that would otherwise prevent user authentication, specifically using an Ldap instance as the primary source of user accounts.
- Warn users whose account status is near a configurable expiration date and redirect the flow to an external identity management system.

### Reflecting LDAP Account Status
The below scenarios are by default considered errors preventing authentication in a generic manner through the normal CAS login flow. LPPE intercepts the authentication flow, detecting the above standard error codes. Error codes are then translated into proper messages in the CAS login flow and would allow the user to take proper action, fully explaining the nature of the problem.

- `ACCOUNT_LOCKED`
- `ACCOUNT_DISABLED`
- `INVALID_LOGON_HOURS`
- `INVALID_WORKSTATION`
- `PASSWORD_MUST_CHANGE`
- `PASSWORD_EXPIRED`

The translation of LDAP errors into CAS workflow is all handled by [ldaptive](http://www.ldaptive.org/docs/guide/authentication/accountstate).

### Account Expiration Notification
LPPE is also able to warn the user when the account is about to expire. The expiration policy is determined through pre-configured Ldap attributes with default values in place.

<div class="alert alert-danger"><strong>No Password Management!</strong><p>LPPE is not about password management. If you are looking for that sort of capability integrating with CAS, you might be interested in:

<ul>
    <li><a href="https://github.com/pwm-project/pwm">PWM Project</a></li>‎
</ul></p></div>

## Configuration
```xml
<alias name="ldapPasswordPolicyConfiguration" alias="passwordPolicyConfiguration" />
```

The following settings are applicable:

```properties
# password.policy.warnAll=false
# password.policy.warningDays=30
# password.policy.url=https://password.example.edu/change
```

Next, in your `ldapAuthenticationHandler` bean, configure the password policy configuration above:

```xml
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:passwordPolicyConfiguration-ref="passwordPolicyConfiguration">
      ...
</bean>
```  

Next, make sure `Authenticator` is set to enable/use password policy:

```xml
<ldaptive:bind-search-authenticator id="authenticator"
      ...
      usePasswordPolicy="${ldap.usePpolicy:true}"
      ...
/>
```

### Components

#### `DefaultAccountStateHandler`
The default account state handler, that calculates the password expiration warning period,
maps LDAP errors to the CAS workflow.

#### `OptionalWarningAccountStateHandler`
Supports both opt-in and opt-out warnings on a per-user basis.

```xml
<alias name="optionalWarningAccountStateHandler" alias="passwordPolicyConfiguration" />
```

```properties
# password.policy.warn.attribute.name=attributeName
# password.policy.warn.attribute.value=attributeValue
# password.policy.warn.display.matched=true
```

## Troubleshooting
To enable additional logging, modify the log4j configuration file to add the following:

```xml
<Logger name="org.ldaptive" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
```
