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
directly at the servlet container and when using `Apache/mod_jk`; for other architectures it may be necessary to do
additional work.

CAS can be configured to extract an X509 certificate from a header created by a proxy running in front of CAS.
## Overview

Certificates are exchanged as part of the SSL (also called TLS) initialization that occurs when any browser connects to an `https` website.
A certain number of public CA certificates are preinstalled in each browser. It is assumed that:

- Your organization is already able to generate and distribute certificates that a user can install in their browser
- Somewhere in that certificate there is a field that contains the Principal name or can be easily mapped to the Principal name that CAS can use.

The remaining problem is to make sure that the browsers, servers and Java are all prepared to support these institutional certificates and, ideally,
that these institutional certificates will be the only ones exchanged when a browser connects to CAS.

## Flow

When a browser connects to CAS over an https: URL, the server identifies itself by sending its own certificate. The browser must already have installed a certificate identifying and trusting the CA that issued the CAS Server certificate. If the browser is not already prepared to trust the CAS server, then an error message pops up saying the server is not trusted.

After the Server sends the certificate that identifies itself, it then can then send a list of names of Certificate Authorities from which it is willing to accept certificates. Ideally, this list will include only one name; the name of the internal institutional CA that issues internal intranet-only certificates that internally contain a field with the CAS Principal name.

A user may install any number of certificates into the browser from any number of CA's. If only one of these certificates comes from a CA named in the list of acceptable CA's sent by the server, then most browsers will automatically send that one certificate without asking, and some can be configured in to not ask when there is only one possible choice. This presents a user experience where CAS becomes transparent to the user after some initial setup and the login happens automatically. However, if the server hosting CAS sends more than one CA name in the list and that matches more than one certificate on the browser, then the user will get prompted to choose a Certificate from the list. A user interaction defeats much of the purpose of certificates in CAS.

Note that CAS does not control this exchange. It is handled by the underlying server. You may not have the control to require the server to vend only one CA name when a browser visits CAS. So if you want to use X.509 certificates in CAS, you should consider this requirement when choosing the hosting environment. The ideal situation is to select a server that can identify itself with a public certificate issued by something like VeriSign or InCommon but then require the client certificate only be issued by the internal corporate/campus authority.

When CAS gets control, a user certificate may have been presented by the browser and be stored in the request. The CAS X.509 authentication machinery examines that certificate and verifies that it was issued by the trusted institutional authority. Then CAS searches through the fields of the certificate to identify one or more fields that can be turned into the principal identifier that the applications expect.

While an institution can have one certificate authority that issues certificates to employees, clients, machines, services, and devices, it is more common for the institution to have a single "root" certificate authority that in its entire existence only issues a handful of certificates. Each of these certificates identifies a secondary Certificate Authority that issues a particular category of certificates (to students, staff, servers, etc.). It is possible to configure CAS to trust the root Authority and, implicitly, all the secondary authorities that it creates. This, however, makes CAS only as secure as the least reliable secondary Certificate Authority created by the institution. At some point in the future, some manager will buy a product that requires a new class of certificates. He will ask to create a Certificate Authority that vends these certificates to the machines running this new product. He will then turn administration of this mess over to a junior programmer or consultant. If CAS trusts any certificate issued by any Authority created by the root, it will trust a fraudulent certificate forged by someone who has acquired control of what was intended to be a special purpose, isolated CA. Therefore, it is better to configure CAS to only accept certificates from the one secondary CA specifically expected to issue credentials to individuals, instead of trusting the institutional root CA.

## Configuration

X.509 support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-x509-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

The X.509 handler technically performs additional checks _after_ the real SSL client authentication process performed
by the Web server terminating the SSL connection. Since an SSL peer may be configured to accept a wide range of
certificates, the CAS X.509 handler provides a number of properties that place additional restrictions on
acceptable client certificates.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#x509-authentication).

## Web Server Configuration

X.509 configuration requires substantial configuration outside the CAS Web application. The configuration of Web
server SSL components varies dramatically with software and is outside the scope of this document. We offer some
general advice for SSL configuration:

* Configuring SSL components for optional client certificate behavior generally provides better user experience.
Requiring client certificates prevents SSL negotiation in cases where the certificate is not present, which prevents
user-friendly server-side error messages.
* Accept certificates only from trusted issuers, generally those within your PKI.
* Specify all certificates in the certificate chain(s) of allowed issuers.

### Apache Tomcat

Anything said here extends the [Apache Tomcat reference for SSL](https://tomcat.apache.org/tomcat-8.0-doc/ssl-howto.html).

The Tomcat server is configured in `$CATALINA_HOME/conf/server.xml` with one or more `<Connector> elements`. Each of these elements defines one port number on which Tomcat will listen for requests. Connectors that support SSL are configured with one or two files that represent a collection of X.509 certificates.

- The `keystoreFile` is a collection of X.509 certificates one of which Tomcat will use to identify itself to Browsers. This certificate contains the DNS name of the server on which Tomcat is running which the HTTP client will have used as the server name part of the URL. It is possible to use a file that contains multiple certificates (in which case Tomcat will use the certificate stored under the alias "Tomcat" or, if that is not found, will use the first certificate it finds that also has an associated private key). However, to assure that no mistakes are made it is sensible practice to use a file that has only the one host certificate, plus of course its private key and chain of parent Certificate Authorities.

- The `truststoreFile` is a collection of X.509 certificates representing Certificate Authorities from which Tomcat is willing to accept user certificates. Since the `keystoreFile` contains the CA that issued the certificate identifying the server, the `truststoreFile` and `keystoreFile` could be the same in a CAS configuration where the URL (actually the port) that uses X.509 authentication is not the well know widely recognized URL for interactive (userid/password form) login, and therefore the only CA that it trusts is the institutional internal CA.

One strategy if you are planning to support both X.509 and userid/password validation through the same port is to put a public (VeriSign, Thawte) certificate for this server in the `keystoreFile`, but then put only the institutional internal CA certificate in the `truststoreFile`. Logically and in all the documentation, the Certificate Authority that issues the certificate to the server which the browser trusts is completely and logically independent of the Certificate Authority that issues the certificate to the user which the server then trusts. Java keeps them separate, Tomcat keeps them separate, and browsers should not be confused if, during SSL negotiation, the server requests a user certificate from a CA other than the one that issued the server's own identifying certificate. In this configuration, the Server issues a public certificate every browser will accept and the browser is strongly urged to send only a private institutional certificate that can be mapped to a Principal name.

<div class="alert alert-info"><strong>Almost There</strong><p>If you previously configured CAS without X.509 authentication, then you probably have the `keystoreFile` already configured and
loaded with a certificate identifying this server. All you need to add is the `truststoreFile` part.</p></div>

The configured connector will look something like:

```xml
<!-- Define a SSL HTTP/1.1 Connector on port 443 -->
<!-- if you do not specify a truststoreFile, then the default java "cacerts" truststore will be used-->
<Connector port="443"
    maxHttpHeaderSize="8192"
    maxThreads="150"
    minSpareThreads="25"
    maxSpareThreads="75"
    enableLookups="false"
    disableUploadTimeout="true"
    acceptCount="100"
    scheme="https"
    secure="true"
    clientAuth="want"
    sslProtocol="TLS"
    keystoreFile="/path/to/keystore.jks"
    keystorePass="secret"
    truststoreFile="/path/to/myTrustStore.jks"
    truststorePass="secret" />
```

The `clientAuth="want"` tells Tomcat to request that the browser provide a user certificate if one is available. If you want to force the use of user certificates, replace `"want"` with `"true"`.
If you specify `"want"` and the browser does not have a certificate, then CAS may forward the request to the login form.


The keystore can be in `JKS` or `PKCS12` format when using Tomcat. When using both `PKCS12` and JKS keystore types then you should specify the type of each keystore by using the `keystoreType` and `truststoreType` attributes.

You may import the certificate of the institutional Certificate Authority (the one that issues User certificates) using the command:

```bash
# Create a blank keystore to start from scratch if needed
# keytool -genkey -keyalg RSA -alias "selfsigned" -keystore myTrustStore.jks -storepass "secret" -validity 360
# keytool -delete -alias "selfsigned" -keystore myTrustStore.jks

keytool -import -alias myAlias -keystore /path/to/myTrustStore.jks -file certificateForInstitutionalCA.crt
```
