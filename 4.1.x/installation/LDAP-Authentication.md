---
layout: default
title: CAS - LDAP Authentication
---

# LDAP Authentication
LDAP integration is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-support-ldap</artifactId>
     <version>${cas.version}</version>
</dependency>
{% endhighlight %}

`LdapAuthenticationHandler` authenticates a username/password against an LDAP directory such as Active Directory
or OpenLDAP. There are numerous directory architectures and we provide configuration for these common cases:

1. [Active Directory](#active-directory-authentication) - Users authenticate with _sAMAAccountName_.
2. [Authenticated Search](#ldap-requiring-authenticated-search) - Manager bind/search followed by user simple bind.
3. [Anonymous Search](#ldap-supporting-anonymous-search) - Anonymous search followed by user simple bind.
4. [Direct Bind](#ldap-supporting-direct-bind) - Compute user DN from format string and perform simple bind.
5. [Principal Attributes Retrieval](#ldap-authentication-principal-attributes) - Resolve principal attributes directly as part of LDAP authentication.

See the [ldaptive documentation](http://www.ldaptive.org/) for more information or to accommodate other situations.

You also need to make sure component scanning is turned on when you configure LDAP authentication. Be sure to include the following in the same configuration file that houses the LDAP configuration for CAS:

{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

     ...
     <context:component-scan base-package="org.jasig.cas" />
     <context:annotation-config/>
     ...
{% endhighlight %}

## Ldap Authentication Principal Attributes
The `LdapAuthenticationHandler` is capable of resolving and retrieving principal attributes independently without the need for [extra principal resolver machinery](../integration/Attribute-Resolution.html). 

{% highlight xml %}
<bean id="ldapAuthenticationHandler"
class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      init-method="initialize"
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
{% endhighlight %}

The above configuration defines a map of attribtues. Keys are LDAP attribute names and values are CAS attribute names which allow you to optionally, retrieve a given attribute and release it under a virtual name. (i.e. Retrieve `mail` from LDAP and remap/rename it to `email` to be released later). If you have no need for this virtual mapping mechanism, you could directly specify the attributes as a list, in which case the above configuration would become:

{% highlight xml %}
<bean id="ldapAuthenticationHandler"
    class="org.jasig.cas.authentication.LdapAuthenticationHandler"
    init-method="initialize"
    p:principalIdAttribute-ref="usernameAttribute"
    c:authenticator-ref="authenticator">
    <property name="principalAttributeList">
       <list>
          <value>displayName</value>
          <value>mail</value>
          <value>memberOf</value>
       </list>
    </property>
</bean>
{% endhighlight %}


## Active Directory Authentication
The following configuration authenticates users by _sAMAccountName_ without performing a search,
which requires manager/administrator credentials in most cases. It is therefore the most performant and secure
solution for the typical Active Directory deployment. Note that the resolved principal ID, which becomes the NetID
passed to CAS client applications, is the _sAMAccountName_ in the following example.
Simply copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.
{% highlight xml %}
<!--
   | Change principalIdAttribute to use another directory attribute,
   | e.g. userPrincipalName, for the NetID
   -->
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="sAMAccountName"
      c:authenticator-ref="authenticator">
    <property name="principalAttributeMap">
        <map>
            <!--
               | This map provides a simple attribute resolution mechanism.
               | Keys are LDAP attribute names, values are CAS attribute names.
               | Use this facility instead of a PrincipalResolver if LDAP is
               | the only attribute source.
               -->
            <entry key="displayName" value="displayName" />
            <entry key="mail" value="mail" />
            <entry key="memberOf" value="memberOf" />
        </map>
    </property>
</bean>

<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
      c:resolver-ref="dnResolver"
      c:handler-ref="authHandler"
      p:entryResolver-ref="entryResolver">
      <property name="authenticationResponseHandlers">
          <list>
              <bean class="org.ldaptive.auth.ext.ActiveDirectoryAuthenticationResponseHandler" />
          </list>
      </property>
</bean>

<!-- Active Directory UPN format. -->
<bean id="dnResolver"
      class="org.ldaptive.auth.FormatDnResolver"
      c:format="%s@${ldap.domain}" />

<bean id="authHandler" class="org.ldaptive.auth.PooledBindAuthenticationHandler"
      p:connectionFactory-ref="pooledLdapConnectionFactory" />

<bean id="pooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="connectionPool" />

<bean id="connectionPool"
      class="org.ldaptive.pool.BlockingConnectionPool"
      init-method="initialize"
      p:poolConfig-ref="ldapPoolConfig"
      p:blockWaitTime="${ldap.pool.blockWaitTime}"
      p:validator-ref="searchValidator"
      p:pruneStrategy-ref="pruneStrategy"
      p:connectionFactory-ref="connectionFactory" />

<bean id="ldapPoolConfig" class="org.ldaptive.pool.PoolConfig"
      p:minPoolSize="${ldap.pool.minSize}"
      p:maxPoolSize="${ldap.pool.maxSize}"
      p:validateOnCheckOut="${ldap.pool.validateOnCheckout}"
      p:validatePeriodically="${ldap.pool.validatePeriodically}"
      p:validatePeriod="${ldap.pool.validatePeriod}" />

<bean id="connectionFactory" class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="connectionConfig" />

<bean id="connectionConfig" class="org.ldaptive.ConnectionConfig"
      p:ldapUrl="${ldap.url}"
      p:connectTimeout="${ldap.connectTimeout}"
      p:useStartTLS="${ldap.useStartTLS}"
      p:sslConfig-ref="sslConfig"/>

<bean id="sslConfig" class="org.ldaptive.ssl.SslConfig">
    <property name="credentialConfig">
        <bean class="org.ldaptive.ssl.X509CredentialConfig"
              p:trustCertificates="${ldap.trustedCert}" />
    </property>
</bean>

<bean id="pruneStrategy" class="org.ldaptive.pool.IdlePruneStrategy"
      p:prunePeriod="${ldap.pool.prunePeriod}"
      p:idleTime="${ldap.pool.idleTime}" />

<bean id="searchValidator" class="org.ldaptive.pool.SearchValidator" />

<!-- If you wish to search by user, rather than by dn, change {dn} to {user} -->
<bean id="entryResolver"
      class="org.ldaptive.auth.SearchEntryResolver"
      p:baseDn="${ldap.authn.baseDn}"
      p:userFilter="userPrincipalName={dn}"
      p:subtreeSearch="true" />
{% endhighlight %}


## LDAP Requiring Authenticated Search
The following configuration snippet provides a template for LDAP authentication performed with manager credentials
followed by a bind. Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.
{% highlight xml %}
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="mail"
      c:authenticator-ref="authenticator">
    <property name="principalAttributeMap">
        <map>
            <!--
               | This map provides a simple attribute resolution mechanism.
               | Keys are LDAP attribute names, values are CAS attribute names.
               | Use this facility instead of a PrincipalResolver if LDAP is
               | the only attribute source.
               -->
            <entry key="member" value="member" />
            <entry key="mail" value="mail" />
            <entry key="displayName" value="displayName" />
        </map>
    </property>
</bean>

<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
      c:resolver-ref="dnResolver"
      c:handler-ref="authHandler" />

<bean id="dnResolver" class="org.ldaptive.auth.PooledSearchDnResolver"
      p:baseDn="${ldap.authn.baseDn}"
      p:subtreeSearch="true"
      p:allowMultipleDns="false"
      p:connectionFactory-ref="searchPooledLdapConnectionFactory"
      p:userFilter="${ldap.authn.searchFilter}" />

<bean id="searchPooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="searchConnectionPool" />

<bean id="searchConnectionPool" parent="abstractConnectionPool"
      p:connectionFactory-ref="searchConnectionFactory" />

<bean id="searchConnectionFactory"
      class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="searchConnectionConfig" />

<bean id="searchConnectionConfig" parent="abstractConnectionConfig"
      p:connectionInitializer-ref="bindConnectionInitializer" />

<bean id="bindConnectionInitializer"
      class="org.ldaptive.BindConnectionInitializer"
      p:bindDn="${ldap.authn.managerDN}">
    <property name="bindCredential">
        <bean class="org.ldaptive.Credential"
              c:password="${ldap.authn.managerPassword}" />
    </property>
</bean>

<bean id="abstractConnectionPool" abstract="true"
      class="org.ldaptive.pool.BlockingConnectionPool"
      init-method="initialize"
      destroy-method="close"
      p:poolConfig-ref="ldapPoolConfig"
      p:blockWaitTime="${ldap.pool.blockWaitTime}"
      p:validator-ref="searchValidator"
      p:pruneStrategy-ref="pruneStrategy" />

<bean id="abstractConnectionConfig" abstract="true"
      class="org.ldaptive.ConnectionConfig"
      p:ldapUrl="${ldap.url}"
      p:connectTimeout="${ldap.connectTimeout}"
      p:useStartTLS="${ldap.useStartTLS}"
      p:sslConfig-ref="sslConfig" />

<bean id="ldapPoolConfig" class="org.ldaptive.pool.PoolConfig"
      p:minPoolSize="${ldap.pool.minSize}"
      p:maxPoolSize="${ldap.pool.maxSize}"
      p:validateOnCheckOut="${ldap.pool.validateOnCheckout}"
      p:validatePeriodically="${ldap.pool.validatePeriodically}"
      p:validatePeriod="${ldap.pool.validatePeriod}" />

<bean id="sslConfig" class="org.ldaptive.ssl.SslConfig">
    <property name="credentialConfig">
        <bean class="org.ldaptive.ssl.X509CredentialConfig"
              p:trustCertificates="${ldap.trustedCert}" />
    </property>
</bean>

<bean id="pruneStrategy" class="org.ldaptive.pool.IdlePruneStrategy"
      p:prunePeriod="${ldap.pool.prunePeriod}"
      p:idleTime="${ldap.pool.idleTime}" />

<bean id="searchValidator" class="org.ldaptive.pool.SearchValidator" />

<bean id="authHandler" class="org.ldaptive.auth.PooledBindAuthenticationHandler"
      p:connectionFactory-ref="bindPooledLdapConnectionFactory" />

<bean id="bindPooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="bindConnectionPool" />

<bean id="bindConnectionPool" parent="abstractConnectionPool"
      p:connectionFactory-ref="bindConnectionFactory" />

<bean id="bindConnectionFactory"
      class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="bindConnectionConfig" />

<bean id="bindConnectionConfig" parent="abstractConnectionConfig" />
{% endhighlight %}


## LDAP Supporting Anonymous Search
The following configuration snippet provides a template for LDAP authentication performed with an anonymous search
followed by a bind. Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.
{% highlight xml %}
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="mail"
      c:authenticator-ref="authenticator">
    <property name="principalAttributeMap">
        <map>
            <!--
               | This map provides a simple attribute resolution mechanism.
               | Keys are LDAP attribute names, values are CAS attribute names.
               | Use this facility instead of a PrincipalResolver if LDAP is
               | the only attribute source.
               -->
            <entry key="member" value="member" />
            <entry key="mail" value="mail" />
            <entry key="displayName" value="displayName" />
        </map>
    </property>
</bean>

<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
      c:resolver-ref="dnResolver"
      c:handler-ref="authHandler" />

<bean id="dnResolver" class="org.ldaptive.auth.PooledSearchDnResolver"
      p:baseDn="${ldap.authn.baseDn}"
      p:subtreeSearch="true"
      p:allowMultipleDns="false"
      p:connectionFactory-ref="searchPooledLdapConnectionFactory"
      p:userFilter="${ldap.authn.searchFilter}" />

<bean id="searchPooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="searchConnectionPool" />

<bean id="searchConnectionPool" parent="abstractConnectionPool" />

<bean id="abstractConnectionPool" abstract="true"
      class="org.ldaptive.pool.BlockingConnectionPool"
      init-method="initialize"
      destroy-method="close"
      p:poolConfig-ref="ldapPoolConfig"
      p:blockWaitTime="${ldap.pool.blockWaitTime}"
      p:validator-ref="searchValidator"
      p:pruneStrategy-ref="pruneStrategy"
      p:connectionFactory-ref="connectionFactory" />

<bean id="ldapPoolConfig" class="org.ldaptive.pool.PoolConfig"
      p:minPoolSize="${ldap.pool.minSize}"
      p:maxPoolSize="${ldap.pool.maxSize}"
      p:validateOnCheckOut="${ldap.pool.validateOnCheckout}"
      p:validatePeriodically="${ldap.pool.validatePeriodically}"
      p:validatePeriod="${ldap.pool.validatePeriod}" />

<bean id="connectionFactory" class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="connectionConfig" />

<bean id="connectionConfig" class="org.ldaptive.ConnectionConfig"
      p:ldapUrl="${ldap.url}"
      p:connectTimeout="${ldap.connectTimeout}"
      p:useStartTLS="${ldap.useStartTLS}"
      p:sslConfig-ref="sslConfig" />

<bean id="sslConfig" class="org.ldaptive.ssl.SslConfig">
    <property name="credentialConfig">
        <bean class="org.ldaptive.ssl.X509CredentialConfig"
              p:trustCertificates="${ldap.trustedCert}" />
    </property>
</bean>

<bean id="pruneStrategy" class="org.ldaptive.pool.IdlePruneStrategy"
      p:prunePeriod="${ldap.pool.prunePeriod}"
      p:idleTime="${ldap.pool.idleTime}" />

<bean id="searchValidator" class="org.ldaptive.pool.SearchValidator" />

<bean id="authHandler" class="org.ldaptive.auth.PooledBindAuthenticationHandler"
      p:connectionFactory-ref="bindPooledLdapConnectionFactory" />

<bean id="bindPooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="bindConnectionPool" />

<bean id="bindConnectionPool" parent="abstractConnectionPool" />
{% endhighlight %}


## LDAP Supporting Direct Bind
The following configuration snippet provides a template for LDAP authentication where no search is required to
compute the DN needed for a bind operation. There are two requirements for this use case:

1. All users are under a single branch in the directory, e.g. `ou=Users,dc=example,dc=org`.
2. The username provided on the CAS login form is part of the DN, e.g. `uid=%s,ou=Users,dc=exmaple,dc=org`.

Copy the configuration to `deployerConfigContext.xml` and provide values for property placeholders.
{% highlight xml %}
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:principalIdAttribute="uid"
      c:authenticator-ref="authenticator">
    <property name="principalAttributeMap">
        <map>
            <!--
               | This map provides a simple attribute resolution mechanism.
               | Keys are LDAP attribute names, values are CAS attribute names.
               | Use this facility instead of a PrincipalResolver if LDAP is
               | the only attribute source.
               -->
            <entry key="member" value="member" />
            <entry key="mail" value="mail" />
            <entry key="displayName" value="displayName" />
        </map>
    </property>
</bean>

<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
      c:resolver-ref="dnResolver"
      c:handler-ref="authHandler" />

<!--
   | The following DN format works for many directories, but may need to be
   | customized.
   -->
<bean id="dnResolver"
      class="org.ldaptive.auth.FormatDnResolver"
      c:format="uid=%s,${ldap.authn.baseDn}" />

<bean id="authHandler" class="org.ldaptive.auth.PooledBindAuthenticationHandler"
      p:connectionFactory-ref="pooledLdapConnectionFactory" />

<bean id="pooledLdapConnectionFactory"
      class="org.ldaptive.pool.PooledConnectionFactory"
      p:connectionPool-ref="connectionPool" />

<bean id="connectionPool"
      class="org.ldaptive.pool.BlockingConnectionPool"
      init-method="initialize"
      p:poolConfig-ref="ldapPoolConfig"
      p:blockWaitTime="${ldap.pool.blockWaitTime}"
      p:validator-ref="searchValidator"
      p:pruneStrategy-ref="pruneStrategy"
      p:connectionFactory-ref="connectionFactory" />

<bean id="ldapPoolConfig" class="org.ldaptive.pool.PoolConfig"
      p:minPoolSize="${ldap.pool.minSize}"
      p:maxPoolSize="${ldap.pool.maxSize}"
      p:validateOnCheckOut="${ldap.pool.validateOnCheckout}"
      p:validatePeriodically="${ldap.pool.validatePeriodically}"
      p:validatePeriod="${ldap.pool.validatePeriod}" />

<bean id="connectionFactory" class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="connectionConfig" />

<bean id="connectionConfig" class="org.ldaptive.ConnectionConfig"
      p:ldapUrl="${ldap.url}"
      p:connectTimeout="${ldap.connectTimeout}"
      p:useStartTLS="${ldap.useStartTLS}"
      p:sslConfig-ref="sslConfig" />

<bean id="sslConfig" class="org.ldaptive.ssl.SslConfig">
    <property name="credentialConfig">
        <bean class="org.ldaptive.ssl.X509CredentialConfig"
              p:trustCertificates="${ldap.trustedCert}" />
    </property>
</bean>

<bean id="pruneStrategy" class="org.ldaptive.pool.IdlePruneStrategy"
      p:prunePeriod="${ldap.pool.prunePeriod}"
      p:idleTime="${ldap.pool.idleTime}" />

<bean id="searchValidator" class="org.ldaptive.pool.SearchValidator" />
{% endhighlight %}

## LDAP Provider Configuration
In certain cases, it may be desirable to use a specific provider implementation when
attempting to establish connections to LDAP. In order to do this, the `connectionFactory`
configuration must be modified to include a reference to the selected provider.

Here's an example for configuring an UnboundID provider for a given connection factory:

{% highlight xml %}
...
<bean id="unboundidLdapProvider"
      class="org.ldaptive.provider.unboundid.UnboundIDProvider" />

<bean id="connectionFactory" class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="connectionConfig"
      p:provider-ref="unboundidLdapProvider"  />
...
{% endhighlight %}

Note that additional dependencies must be available to CAS at runtime, so it's able to locate
the provider implementation and supply that to connections. 

## LDAP Properties Starter
The following LDAP configuration properties provide a reasonable starting point for configuring the LDAP
authentication handler. The `ldap.url` property must be changed at a minumum. LDAP properties may be added to the
`cas.properties` configuration file; alternatively they may be isolated in an `ldap.properties` file and loaded
into the Spring application context by modifying the `propertyFileConfigurer.xml` configuration file.

    #========================================
    # General properties
    #========================================
    ldap.url=ldap://directory.ldaptive.org

    # LDAP connection timeout in milliseconds
    ldap.connectTimeout=3000

    # Whether to use StartTLS (probably needed if not SSL connection)
    ldap.useStartTLS=false

    #========================================
    # LDAP connection pool configuration
    #========================================
    ldap.pool.minSize=3
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

    # Base DN of users to be authenticated
    ldap.authn.baseDn=ou=Users,dc=example,dc=org

    # Manager DN for authenticated searches
    ldap.authn.managerDN=uid=manager,ou=Users,dc=example,dc=org

    # Manager password for authenticated searches
    ldap.authn.managerPassword=nonsense

    # Search filter used for configurations that require searching for DNs
    #ldap.authn.searchFilter=(&(uid={user})(accountState=active))
    ldap.authn.searchFilter=(uid={user})
    
    # Search filter used for configurations that require searching for DNs
    #ldap.authn.format=uid=%s,ou=Users,dc=example,dc=org
    ldap.authn.format=%s@example.com

	# A path to trusted X.509 certificate for StartTLS 
	ldap.trustedCert=/path/to/cert.cer

	
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
    <li><a href="https://github.com/Unicon/cas-password-manager">https://github.com/Unicon/cas-password-manager</a></li>
    <li><a href="http://code.google.com/p/pwm/">http://code.google.com/p/pwm/</a></li>‎
</ul></p></div>

## Configuration
LPPE is by default turned off. To enable the functionally, navigate to `src/main/webapp/WEB-INF/unused-spring-configuration` and move the `lppe-configuration.xml` xml file over to the `spring-configuration` directory.

{% highlight xml %}
<bean id="passwordPolicy" class="org.jasig.cas.authentication.support.LdapPasswordPolicyConfiguration"
        p:alwaysDisplayPasswordExpirationWarning="${password.policy.warnAll}"
        p:passwordWarningNumberOfDays="${password.policy.warningDays}"
        p:passwordPolicyUrl="${password.policy.url}"
        p:accountStateHandler-ref="accountStateHandler" />

  <!-- This component is suitable for most cases but can be replaced with a custom component for special cases. -->
<bean id="accountStateHandler" class="org.jasig.cas.authentication.support.DefaultAccountStateHandler" />
{% endhighlight %}      

Next, in your `ldapAuthenticationHandler` bean, configure the password policy configuration above:

{% highlight xml %}
<bean id="ldapAuthenticationHandler"
      class="org.jasig.cas.authentication.LdapAuthenticationHandler"
      p:passwordPolicyConfiguration-ref="passwordPolicy">

      ...
</bean>
{% endhighlight %}  
 
Next, you have to explicitly define an LDAP-specific response handler in your `Authenticator`. 

### Generic 

{% highlight xml %}
<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
    c:resolver-ref="dnResolver"
    c:handler-ref="authHandler">
    <property name="authenticationResponseHandlers">
        <util:list>
            <bean class="org.ldaptive.auth.ext.PasswordPolicyAuthenticationResponseHandler" />
            <!--
            <bean class="org.ldaptive.auth.ext.PasswordExpirationAuthenticationResponseHandler" />
            -->
        </util:list>
</property>
</bean>
{% endhighlight %}  

Also, you have to handle the `PasswordPolicy` controls in the `BindAuthenticationHandler`:

{% highlight xml %}
<bean id="authHandler" class="org.ldaptive.auth.PooledBindAuthenticationHandler"
    p:connectionFactory-ref="bindPooledLdapConnectionFactory">
    <property name="authenticationControls">
        <util:list>
                <bean class="org.ldaptive.control.PasswordPolicyControl" />
        </util:list>
    </property>
</bean>
{% endhighlight %} 

### Active Directory

{% highlight xml %}
<bean id="authenticator" class="org.ldaptive.auth.Authenticator"
    c:resolver-ref="dnResolver"
    c:handler-ref="authHandler">
    <property name="authenticationResponseHandlers">
        <util:list>
            <bean class="org.ldaptive.auth.ext.ActiveDirectoryAuthenticationResponseHandler" />
        </util:list>
</property>
</bean>
{% endhighlight %} 

### Components

#### `DefaultAccountStateHandler`
The default account state handler, that calculates the password expiration warning period, maps LDAP errors to the CAS workflow.

#### `OptionalWarningAccountStateHandler`
Supports both opt-in and opt-out warnings on a per-user basis.

{% highlight xml %}
<bean id="accountStateHandler" class="org.jasig.cas.authentication.support.OptionalWarningAccountStateHandler"
        p:warningAttributeName="${password.warning.attr.name}"
        p:warningAttributeValue="${password.warning.attr.value}"
        p:displayWarningOnMatch="${password.warning.display.match}" />
{% endhighlight %}  

The first two parameters define an attribute on the user entry to match on, and the third parameter determines
whether password expiration warnings should be displayed on match.

**Note:** Deployers MUST configure LDAP components to provide `warningAttributeName` in the set of attributes returned from the LDAP query for user details.

## Troubleshooting
To enable additional logging, modify the log4j configuration file to add the following:

{% highlight xml %}
<Logger name="org.ldaptive" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
{% endhighlight %} 

