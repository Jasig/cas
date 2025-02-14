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


## Overview

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Web Server Configuration

X.509 configuration requires substantial configuration outside the CAS Web application. The configuration of Web
server SSL components varies dramatically with software and is outside the scope of this document. We offer some
general advice for SSL configuration:

* Configuring SSL components for optional client certificate behavior generally provides better user experience.
Requiring client certificates prevents SSL negotiation in cases where the certificate is not present, which prevents
user-friendly server-side error messages.
* Accept certificates only from trusted issuers, generally those within your PKI.
* Specify all certificates in the certificate chain(s) of allowed issuers.
