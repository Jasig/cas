---
layout: default
title: CAS - X.509 Authentication
---

# X.509 Authentication
CAS X.509 authentication components provide a mechanism to authenticate users who present client certificates during
the SSL/TLS handshake process. The X.509 components require configuration ouside the CAS application since the
SSL handshake happens outside the servlet layer where the CAS application resides. There is no particular requirement
on deployment architecture (i.e. Apache reverse proxy, load balancer SSL termination) other than any client
certificate presented in the SSL handshake be accessible to the servlet container as a request attribute named
`javax.servlet.request.X509Certificate`. This happens naturally for configurations that terminate SSL connections
directly at the servlet container and when using Apache/mod_jk; for other architectures it may be necessary to do
additional work.


## X.509 Components
X.509 support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-x509</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

CAS provides an X.509 authentication handler, a handful of X.509-specific principal resolvers, some certificate
revocation machinery, and some Webflow actions to provide for non-interactive authentication.

###### `X509CredentialsAuthenticationHandler`
The X.509 handler technically performs additional checks _after_ the real SSL client authentication process performed
by the Web server terminating the SSL connection. Since an SSL peer may be configured to accept a wide range of
certificates, the CAS X.509 handler provides a number of properties that place additional restrictions on
acceptable client certificates.

* `trustedIssuerDnPattern` - Regular expression defining allowed issuer DNs. (must be specified)
* `subjectDnPattern` - Regular expression defining allowed subject DNs. (default=`.*`)
* `maxPathLength` - Maximum number of certs allowed in certificate chain. (default=1)
* `maxPathLengthAllowUnspecified` - True to allow unspecified path length, false otherwise. (default=false)
* `checkKeyUsage` - True to enforce certificate `keyUsage` field (if present), false otherwise. (default=false)
* `requireKeyUsage` - True to require the existence of a `keyUsage` certificate field, false otherwise. (default=false)
* `revocationChecker` - Instance of `RevocationChecker` used for certificate expiration checks.
(default=`NoOpRevocationChecker`)


### Principal Resolver Components

###### `X509SubjectPrincipalResolver`
Creates a principal ID from a format string composed of components from the subject distinguished name.
The following configuration snippet produces prinicpals of the form _cn@example.com_. For example, given a
certificate with the subject _DC=edu, DC=vt/UID=jacky, CN=Jascarnella Ellagwonto_ it would produce the ID
_jacky@vt.edu_.

{% highlight xml %}
<bean id="x509SubjectResolver"
      class="org.jasig.cas.adaptors.x509.authentication.principal.X509SubjectPrincipalResolver"
      p:descriptor="$CN@$DC.$DC" />
{% endhighlight %}

See the Javadocs for a thorough discussion of the format string specification.


###### `X509SubjectDNPrincipalResolver`
Creates a principal ID from the certificate subject distinguished name.


###### `X509SerialNumberPrincipalResolver`
Creates a principal ID from the certificate serial number.


###### `X509SerialNumberAndIssuerDNPrincipalResolver`
Creates a principal ID by concatenating the certificate serial number, a delimiter, and the issuer DN.
The serial number may be prefixed with an optional string. See the Javadocs for more information.

###### `X509SubjectAlternativeNameUPNPrincipalResolver`
Adds support the embedding of a `UserPrincipalName` object as a `SubjectAlternateName` extension within an X509 certificate,
allowing properly-empowered certificates to be used for network logon (via SmartCards, or alternately by 'soft certs' in certain environments).
This resolver extracts the Subject Alternative Name UPN extension from the provided certificate if available as a resolved principal id.

{% highlight xml %}
<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
    <constructor-arg>
        <map>
            <entry key-ref="x509AuthenticationHandler"
                   value-ref="x509UPNPrincipalResolver" />
            ...
        </map>
    </constructor-arg>
</bean>
...

<bean id="x509AuthenticationHandler"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.X509CredentialsAuthenticationHandler">

<bean id="x509UPNPrincipalResolver"
      class="org.jasig.cas.adaptors.x509.authentication.principal.X509SubjectAlternativeNameUPNPrincipalResolver">
{% endhighlight %}


### Certificate Revocation Checking Components
CAS provides a flexible policy engine for certificate revocation checking. This facility arose due to lack of
configurability in the revocation machinery built into the JSSE.

The following configuration is shared by all components:

| Field                             | Description
|-----------------------------------+---------------------------------------------------------+
| `unavailableCRLPolicy`   | Policy applied when CRL data is unavailable upon fetching. (default=`DenyRevocationPolicy`)
| `expiredCRLPolicy`   | Policy applied when CRL data is expired. (default=`ThresholdExpiredCRLRevocationPolicy`)

The following policies are available by default:

| Policy                             | Description
|-----------------------------------+---------------------------------------------------------+
| `AllowRevocationPolicy`   | Allow policy
| `DenyRevocationPolicy`   | Deny policy
| `ThresholdExpiredCRLRevocationPolicy`   | Deny if CRL is more than X seconds expired.


#### `ResourceCRLRevocationChecker`
Performs a certificate revocation check against a CRL hosted at a fixed location. Any resource type supported by the
Spring [`Resource`]() class may be specified for the CRL resource. The CRL is fetched at periodic intervals and cached.

Configuration properties:

| Field                             | Description
|-----------------------------------+---------------------------------------------------------+
| `crls`              | Spring resource describing the location/kind of CRL resource. This MUST be specified. A single CRL resource can be alternatively specified via the `crl` parameter.
| `refreshInterval`   | Periodic CRL refresh interval in seconds. (default=3600)
| `fetcher`           | Component responsible for fetching of the CRL resource. (default=`ResourceCRLFetcher`)


`ResourceCRLRevocationChecker` Example:
{% highlight xml %}
<bean id="crlResource"
      class="org.springframework.core.io.UrlResource"
      c:path="https://pki.example.com/exampleca/crl" />

<bean id="allowPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.AllowRevocationPolicy" />

<bean id="thresholdPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ThresholdExpiredCRLRevocationPolicy"
      p:threshold="3600" />

<bean id="revocationChecker"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ResourceCRLRevocationChecker"
      c:crl-ref="crlResource"
      p:refreshInterval="600"
      p:unavailableCRLPolicy-ref="allowPolicy"
      p:thresholdPolicy-ref="thresholdPolicy" />
{% endhighlight %}


#### `CRLDistributionPointRevocationChecker`
Performs certificate revocation checking against the CRL URI(s) mentioned in the certificate _cRLDistributionPoints_
extension field. The component leverages a cache to prevent excessive IO against CRL endpoints; CRL data is fetched
if does not exist in the cache or if it is expired.

Configuration properties:

| Field                             | Description
|-----------------------------------+---------------------------------------------------------+
| `cache`               | Ehcache `Cache` component.
| `fetcher`             | Component responsible for fetching of the CRL resource. (default=`ResourceCRLFetcher`)
| `throwOnFetchFailure` | Throws errors if fetching of the CRL resource fails.
| `checkAll`   | Flag to indicate whether all CRLs should be checked for the certificate resource. 

`CRLDistributionPointRevocationChecker` Example:

{% highlight xml %}
<!-- timeToLive, timeToIdle are in seconds -->
<bean id="crlCache" class="org.springframework.cache.ehcache.EhCacheFactoryBean"
      p:cacheName="CRLCache"
      p:eternal="false"
      p:overflowToDisk="false"
      p:maxElementsInMemory="100"
      p:timeToLive="36000"
      p:timeToIdle="36000">
  <property name="cacheManager">
    <bean class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean" />
  </property>
</bean>

<bean id="denyPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.DenyRevocationPolicy" />

<bean id="thresholdPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ThresholdExpiredCRLRevocationPolicy"
      p:threshold="3600" />

<bean id="revocationChecker"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.CRLDistributionPointRevocationChecker"
      c:cache-ref="crlCache"
      p:checkAll="false"
      p:unavailableCRLPolicy-ref="denyPolicy"
      p:thresholdPolicy-ref="thresholdPolicy" />
{% endhighlight %}

#### CRL Fetching Configuration
By default, all revocation checks use the `ResourceCRLFetcher` component to fetch the CRL resource from the specified location. The following alternatives are available:

##### `LdaptiveResourceCRLFetcher`
Fetches a CRL resource from a preconfigured attribute, in the event that the CRL resource is an LDAP instance:


##### `PoolingLdaptiveResourceCRLFetcher`
Fetches a CRL resource from a preconfigured attribute, in the event that the CRL resource is an LDAP instance. This component is able to use connection pooling.

##### Example Configuration
The following example demonstrates the configuration required to fetch a CRL resource from LDAP. Both fetchers are demonstrated here, but only one generally is necessary.

The example below searches an LDAP instance found in the X509 certificate, based on the filter and the baseDN given. It then attempts to obtain the binary attribute `certificateRevocationList` and fetch the resource. The value will be decoded to Base64 first by the fetcher.

{% highlight xml %}
<context:component-scan base-package="org.jasig.cas" />
<context:annotation-config />

<context:property-placeholder location="classpath:/ldap.properties"/>

<bean id="ldapCertFetcher"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ldap.LdaptiveResourceCRLFetcher"
      c:connectionConfig-ref="provisioningConnectionConfig"
      c:searchExecutor-ref="searchExecutor"  />

<!-- Enabled connection pooling -->
<bean id="poolingLdapCertFetcher"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ldap.PoolingLdaptiveResourceCRLFetcher"
      c:connectionConfig-ref="provisioningConnectionConfig"
      c:searchExecutor-ref="searchExecutor"
      c:connectionPool-ref="connectionPool"/>

<bean id="baseDn" class="java.lang.String">
    <constructor-arg type="java.lang.String" value="${ldap.baseDn}" />
</bean>

<util:list id="returnAttributes">
    <value>certificateRevocationList</value>
</util:list>

<bean id="searchFilter" class="org.ldaptive.SearchFilter"
      p:filter="${ldap.searchfilter.cert}" />

<bean id="searchExecutor" class="org.ldaptive.SearchExecutor"
      p:baseDn-ref="baseDn"
      p:searchFilter-ref="searchFilter"
      p:returnAttributes-ref="returnAttributes"
      p:binaryAttributes-ref="returnAttributes" />

<bean id="provisioningConnectionFactory" class="org.ldaptive.DefaultConnectionFactory"
      p:connectionConfig-ref="provisioningConnectionConfig"
      p:provider-ref="unboundidLdapProvider"  />

<bean id="unboundidLdapProvider"
      class="org.ldaptive.provider.unboundid.UnboundIDProvider"/>

<bean id="provisioningConnectionConfig" class="org.ldaptive.ConnectionConfig"
      p:connectTimeout="${ldap.connectTimeout}"
      p:useStartTLS="${ldap.useStartTLS: false}"
      p:connectionInitializer-ref="bindConnectionInitializer"
      p:sslConfig-ref="provisionSslConfig"/>

<bean id="provisionSslConfig" class="org.ldaptive.ssl.SslConfig">
    <property name="credentialConfig">
        <bean class="org.ldaptive.ssl.KeyStoreCredentialConfig" />
    </property>
</bean>

<bean id="bindConnectionInitializer"
      class="org.ldaptive.BindConnectionInitializer"
      p:bindDn="${ldap.managerDn}">
    <property name="bindCredential">
        <bean class="org.ldaptive.Credential"
              c:password="${ldap.managerPassword}" />
    </property>
</bean>

<bean id="connectionPool"
      class="org.ldaptive.pool.BlockingConnectionPool"
      lazy-init="true"
      init-method="initialize"
      destroy-method="close"
      p:poolConfig-ref="ldapPoolConfig"
      p:blockWaitTime="${ldap.pool.blockWaitTime}"
      p:validator-ref="searchValidator"
      p:pruneStrategy-ref="pruneStrategy" />

<bean id="pruneStrategy" class="org.ldaptive.pool.IdlePruneStrategy"
      p:prunePeriod="${ldap.pool.prunePeriod}"
      p:idleTime="${ldap.pool.idleTime}" />

<bean id="searchValidator" class="org.ldaptive.pool.SearchValidator" />

<bean id="ldapPoolConfig" class="org.ldaptive.pool.PoolConfig"
      p:minPoolSize="${ldap.pool.minSize}"
      p:maxPoolSize="${ldap.pool.maxSize}"
      p:validateOnCheckOut="${ldap.pool.validateOnCheckout}"
      p:validatePeriodically="${ldap.pool.validatePeriodically}"
      p:validatePeriod="${ldap.pool.validatePeriod}" />
{% endhighlight %}

### Webflow Components
A single Webflow component, `X509CertificateCredentialsNonInteractiveAction`, is required to extract the certificate
from the HTTP request context and perform non-interactive authentication.


## X.509 Configuration
X.509 configuration requires substantial configuration outside the CAS Web application. The configuration of Web
server SSL components varies dramatically with software and is outside the scope of this document. We offer some
general advice for SSL configuration:

* Configuring SSL components for optional client certificate behavior generally provides better user experience.
Requiring client certificates prevents SSL negotiation in cases where the certificate is not present, which prevents
user-friendly server-side error messages.
* Accept certificates only from trusted issuers, generally those within your PKI.
* Specify all certificates in the certificate chain(s) of allowed issuers.


### Configure Authentication Components
Use the following template to configure authentication in `deployerConfigContext.xml`:
{% highlight xml %}
<bean id="crlResource"
      class="org.springframework.core.io.UrlResource"
      c:path="https://pki.example.com/exampleca/crl" />

<bean id="allowPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.AllowRevocationPolicy" />

<bean id="thresholdPolicy"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ThresholdExpiredCRLRevocationPolicy"
      p:threshold="3600" />

<bean id="revocationChecker"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.ResourceCRLRevocationChecker"
      c:crl-ref="crlResource"
      p:refreshInterval="600"
      p:unavailableCRLPolicy-ref="allowPolicy"
      p:thresholdPolicy-ref="thresholdPolicy" />

<bean id="x509Handler"
      class="org.jasig.cas.adaptors.x509.authentication.handler.support.X509CredentialsAuthenticationHandler"
      p:trustedIssuerDnPattern="CN=(DEV )*Virginia Tech [A-Za-z ]*User CA.*"
      p:maxPathLength="2147483647"
      p:maxPathLengthAllowUnspecified="true"
      p:checkKeyUsage="true"
      p:requireKeyUsage="true"
      p:revocationChecker-ref="revocationChecker" />

<bean id="x509PrincipalResolver"
      class="org.jasig.cas.adaptors.x509.authentication.principal.X509SubjectPrincipalResolver"
      p:descriptor="$UID" />

<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
  <constructor-arg>
    <map>
      <entry key-ref="x509Handler" value-ref="x509PrincipalResolver"/>
    </map>
  </constructor-arg>
  <property name="authenticationMetaDataPopulators">
    <list>
      <bean class="org.jasig.cas.authentication.SuccessfulHandlerMetaDataPopulator" />
    </list>
  </property>
</bean>
{% endhighlight %}


### X.509 Webflow Configuration
Uncomment the `startAuthenticate` state in `login-webflow.xml`:

{% highlight xml %}
<action-state id="startAuthenticate">
  <evaluate expression="x509Check" />
  <transition on="success" to="sendTicketGrantingTicket" />
  <transition on="warn" to="warn" />
  <transition on="error" to="generateLoginTicket" />
</action-state>
{% endhighlight %}

Replace all instances of the `generateLoginTicket` transition in other states with `startAuthenticate`.

Define the `x509Check` bean in `cas-servlet.xml`:
{% highlight xml %}
<bean id="x509Check"
   class="org.jasig.cas.adaptors.x509.web.flow.X509CertificateCredentialsNonInteractiveAction"
   p:centralAuthenticationService-ref="centralAuthenticationService" />
{% endhighlight %}
