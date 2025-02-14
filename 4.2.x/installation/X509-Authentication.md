---
layout: default
title: CAS - X.509 Authentication
---

# X.509 Authentication
CAS X.509 authentication components provide a mechanism to authenticate users who present client certificates during
the SSL/TLS handshake process. The X.509 components require configuration outside the CAS application since the
SSL handshake happens outside the servlet layer where the CAS application resides. There is no particular requirement
on deployment architecture (i.e. Apache reverse proxy, load balancer SSL termination) other than any client
certificate presented in the SSL handshake be accessible to the servlet container as a request attribute named
`javax.servlet.request.X509Certificate`. This happens naturally for configurations that terminate SSL connections
directly at the servlet container and when using Apache/mod_jk; for other architectures it may be necessary to do
additional work.


## X.509 Components
X.509 support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-x509-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

CAS provides an X.509 authentication handler, a handful of X.509-specific principal resolvers, some certificate
revocation machinery, and some Webflow actions to provide for non-interactive authentication.

### `X509CredentialsAuthenticationHandler`
The X.509 handler technically performs additional checks _after_ the real SSL client authentication process performed
by the Web server terminating the SSL connection. Since an SSL peer may be configured to accept a wide range of
certificates, the CAS X.509 handler provides a number of properties that place additional restrictions on
acceptable client certificates.

```xml
<alias name="x509CredentialsAuthenticationHandler" alias="primaryAuthenticationHandler" />
```

The following settings are applicable:

```properties
# cas.x509.authn.crl.checkAll=false
# cas.x509.authn.crl.throw.failure=true
# cas.x509.authn.crl.refresh.interval=
# cas.x509.authn.revocation.policy.threshold=
# cas.x509.authn.trusted.issuer.dnpattern=
# cas.x509.authn.max.path.length=
# cas.x509.authn.max.path.length.unspecified=
# cas.x509.authn.check.key.usage=
# cas.x509.authn.require.key.usage=
# cas.x509.authn.subject.dnpattern=
# cas.x509.authn.principal.descriptor=
# cas.x509.authn.principal.serial.no.prefix=
# cas.x509.authn.principal.value.delim=
```

## Principal Resolver Components

### `X509SubjectPrincipalResolver`
Creates a principal ID from a format string composed of components from the subject distinguished name.
The following configuration snippet produces principals of the form `cn@example.com`. For example, given a
certificate with the subject `DC=edu, DC=vt/UID=jacky, CN=Jascarnella Ellagwonto` it would produce the ID
`jacky@vt.edu`.

```xml
<alias name="x509SubjectPrincipalResolver" alias="primaryPrincipalResolver" />
```

### `X509SubjectDNPrincipalResolver`
Creates a principal ID from the certificate subject distinguished name.

```xml
<alias name="x509SubjectDNPrincipalResolver" alias="primaryPrincipalResolver" />
```

### `X509SerialNumberPrincipalResolver`
Creates a principal ID from the certificate serial number.

```xml
<alias name="x509SerialNumberPrincipalResolver" alias="primaryPrincipalResolver" />
```

### `X509SerialNumberAndIssuerDNPrincipalResolver`
Creates a principal ID by concatenating the certificate serial number, a delimiter, and the issuer DN.
The serial number may be prefixed with an optional string.

```xml
<alias name="x509SerialNumberAndIssuerDNPrincipalResolver" alias="primaryPrincipalResolver" />
```

### `X509SubjectAlternativeNameUPNPrincipalResolver`
Adds support the embedding of a `UserPrincipalName` object as a `SubjectAlternateName` extension within an X509 certificate,
allowing properly-empowered certificates to be used for network logon (via SmartCards, or alternately by 'soft certs' in certain environments).
This resolver extracts the Subject Alternative Name UPN extension from the provided certificate if available as a resolved principal id.

```xml
<alias name="x509SubjectAlternativeNameUPNPrincipalResolver" alias="primaryPrincipalResolver" />
```

## Certificate Revocation Checking Components
CAS provides a flexible policy engine for certificate revocation checking. This facility arose due to lack of
configurability in the revocation machinery built into the JSSE.

The following configuration is shared by all components:

| Field                             | Description
|-----------------------------------+---------------------------------------------------------+
| `unavailableCRLPolicy`    | Policy applied when CRL data is unavailable upon fetching. (default=`DenyRevocationPolicy`)
| `expiredCRLPolicy`        | Policy applied when CRL data is expired. (default=`ThresholdExpiredCRLRevocationPolicy`)

The following policies are available by default:

| Policy                                  
|--------------------------------------
| `allowRevocationPolicy
| `denyRevocationPolicy`
| `thresholdExpiredCRLRevocationPolicy`

### `ResourceCRLRevocationChecker`
Performs a certificate revocation check against a CRL hosted at a fixed location. The CRL is fetched at periodic intervals and cached.

```xml
<alias name="resourceCrlRevocationChecker" alias="x509RevocationChecker" />
<util:set id="x509CrlResources" />
...
<alias name="allowRevocationPolicy" alias="x509ResourceUnavailableRevocationPolicy" />
<alias name="thresholdExpiredCRLRevocationPolicy" alias="x509ResourceExpiredRevocationPolicy" />
```

### `CRLDistributionPointRevocationChecker`
Performs certificate revocation checking against the CRL URI(s) mentioned in the certificate _cRLDistributionPoints_
extension field. The component leverages a cache to prevent excessive IO against CRL endpoints; CRL data is fetched
if does not exist in the cache or if it is expired.

```xml
<alias name="crlDistributionPointRevocationChecker" alias="x509RevocationChecker" />
...
<alias name="allowRevocationPolicy" alias="x509CrlUnavailableRevocationPolicy" />
<alias name="thresholdExpiredCRLRevocationPolicy" alias="x509CrlExpiredRevocationPolicy" />
```

## CRL Fetching Configuration
By default, all revocation checks use the `ResourceCRLFetcher` component to fetch the CRL resource from the specified location.

```xml
<alias name="resourceCrlFetcher" alias="x509CrlFetcher" />
```

The following alternatives are available:

### `LdaptiveResourceCRLFetcher`
Fetches a CRL resource from a preconfigured attribute, in the event that the CRL resource is an LDAP instance.

```xml
<alias name="ldaptiveResourceCRLFetcher" alias="x509CrlFetcher" />
<alias name="customLdapSearchExecutor" alias="ldaptiveResourceCRLSearchExecutor" />
<alias name="customLdapConnectionConfig" alias="ldaptiveResourceCRLConnectionConfig" />
```

### `PoolingLdaptiveResourceCRLFetcher`
Fetches a CRL resource from a preconfigured attribute, in the event that the CRL resource is an LDAP instance. This component is able to use connection pooling.

```xml
<alias name="poolingLdaptiveResourceCRLFetcher" alias="x509CrlFetcher" />
<alias name="customLdapConnectionPool" alias="poolingLdaptiveConnectionPool" />
<alias name="customLdapSearchExecutor" alias="poolingLdaptiveResourceCRLSearchExecutor" />
<alias name="customLdapConnectionConfig" alias="poolingLdaptiveResourceCRLConnectionConfig" />
```

## X.509 Configuration
X.509 configuration requires substantial configuration outside the CAS Web application. The configuration of Web
server SSL components varies dramatically with software and is outside the scope of this document. We offer some
general advice for SSL configuration:

* Configuring SSL components for optional client certificate behavior generally provides better user experience.
Requiring client certificates prevents SSL negotiation in cases where the certificate is not present, which prevents
user-friendly server-side error messages.
* Accept certificates only from trusted issuers, generally those within your PKI.
* Specify all certificates in the certificate chain(s) of allowed issuers.

## X.509 Webflow Configuration

Replace all instances of the `generateLoginTicket` transition in other states with `startX509Authenticate`.
