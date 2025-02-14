---
layout: default
title: CAS - Google Apps Integration
---

# Overview
Google Apps for Education (or any of the Google Apps) utilizes SAML 2.0 to provide an 
integration point for external authentication services. 

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-googleapps</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Generate Public/Private Keys

The first step is to generate DSA/RSA public and private keys. These are used to sign and read the Assertions. 
After keys are created, the public key needs to be registered with Google.

The keys will also need to be available to the CAS application (but not publicly available over the Internet)
via the classpath though any location accessible by the user running the web server 
instance and not served publicly to the Internet is acceptable.  Thus, inside `src/main/resources` is 
nice because it is scoped to the web application but not normally served. `/etc/cas/` 
is also fine as well and protects the key from being overwritten on deploy of a new CAS webapp version.

```bash
openssl genrsa -out private.key 1024
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
openssl pkcs8 -topk8 -inform PER -outform DER -nocrypt -in private.key -out private.p8
openssl req -new -x509 -key private.key -out x509.pem -days 365
```

The `x509.pem` file should be uploaded into Google Apps under Security/SSO.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

## Register Google Apps

Ensure that Google Apps is registered in your [service registry](../installation/Service-Management.html).

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "https://www.google.com/a/YourGoogleDomain/acs",
  "name" : "googleApps",
  "id" : 1000,
  "evaluationOrder" : 10
}
```

## Configure Username Attribute 

As an optional step, you can configure an alternate username to be send to Google in the SAML reply. This alternate user name
can be specified in the CAS service registry via [username attribute providers](../installation/Service-Management.html)
for the registered Google Apps service.

## Configure Google

You'll need to provide Google with the URL for your SAML-based SSO service, as well as the URL your users will 
be redirected to when they log out of a hosted Google application.
Use the following URLs when you are configuring for Google Apps:

* Sign-in page URL: `https://sso.school.edu/cas/login`
* Sign-out page URL: `https://sso.school.edu/cas/logout`
* Change password URL: `https://mgmt.password.edu/`

## Test

Attempt to access a Google-hosted application, such as Google Calendar 
with the url: `https://calendar.google.com/a/YourGoogleDomain`
