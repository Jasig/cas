---
layout: default
title: CAS - Configuring SSO Session Cookie
---

# SSO Session Cookie
A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session. This cookie maintains login state for the client, and while it is valid, the client can present it to CAS in lieu of primary credentials. Services can opt out of single sign-on through the `renew` parameter. See the [CAS Protocol](../protocol/CAS-Protocol.html) for more info.

## Configuration
The generation of the ticket-granting cookie is controlled by the file `ticketGrantingTicketCookieGenerator.xml`


The cookie has the following properties:

1. It is marked as secure.
2. Depending on container support, the cookie would be marked as http-only automatically.
3. The cookie value is encrypted and signed via secret keys that need to be generated upon deployment.

## Cookie Value Encryption

The cookie value is linked to the active ticket-granting ticket, the remote IP address that initiated the request 
as well as the user agent that submitted the request. The final cookie value is then encrypted and signed.

The secret keys are defined in the `cas.properties` file. These keys **MUST** be regenerated per your specific environment. Each key
is a JSON Web Token with a defined length per the algorithm used for encryption and signing.

{% highlight properties %}
# CAS SSO Cookie Generation & Security
# See https://github.com/mitreid-connect/json-web-key-generator
#
# Do note that the following settings MUST be generated per deployment.
#
# Defaults at spring-configuration/ticketGrantingTicketCookieGenerator.xml
# The encryption secret key. By default, must be a octet string of size 256.
tgc.encryption.key=

# The signing secret key. By default, must be a octet string of size 512.
tgc.signing.key=

{% endhighlight %}


If keys are left undefined, on startup CAS will notice that no keys are defined and it will appropriately generate keys for you automatically. Your CAS logs will then show the following snippet:

```bash
WARN [org.jasig.cas.util.BaseStringCipherExecutor] - <Secret key for encryption is not defined. CAS will attempt to auto-generate the encryption key>
WARN [org.jasig.cas.util.BaseStringCipherExecutor] - <Generated encryption key ABC of size ... . The generated key MUST be added to CAS settings.>
WARN [org.jasig.cas.util.BaseStringCipherExecutor] - <Secret key for signing is not defined. CAS will attempt to auto-generate the signing key>
WARN [org.jasig.cas.util.BaseStringCipherExecutor] - <Generated signing key XYZ of size ... . The generated key MUST be added to CAS settings.>
```

You should then grab each generated key for encryption and signing, and put them inside your cas.properties file for each now-enabled setting.

If you wish you manually generate keys, you may [use the following tool](https://github.com/mitreid-connect/json-web-key-generator).

## Turning Off Cookie Value Encryption
if you wish to disable the signing and encryption of the cookie, in the
configuration xml file, use the following beans instead of those provided by default:

{% highlight xml %}
<bean id="cookieCipherExecutor" class="org.jasig.cas.util.NoOpCipherExecutor" />

<bean id="cookieValueManager" class="org.jasig.cas.web.support.NoOpCookieValueManager"/>

{% endhighlight %}

## Cookie Generation for Renewed Authentications

By default, forced authentication requests that challenge the user for credentials
either via the [`renew` request parameter](../protocol/CAS-Protocol.html)
or via [the service-specific setting](Service-Management.html) of
the CAS service registry will always generate the ticket-granting cookie
nonetheless. What this means is, logging in to a non-SSO-participating application
via CAS nonetheless creates a valid CAS single sign-on session that will be honored on a
subsequent attempt to authenticate to a SSO-participating application.

Plausibly, a CAS adopter may want this behavior to be different, such that logging in to a non-SSO-participating application
via CAS either does not create a CAS SSO session and the SSO session it creates is not honored for authenticating subsequently
to an SSO-participating application. This might better match user expectations.

The controlling of this behavior is done via the `cas.properties` file:

{% highlight properties %}
##
# Single Sign-On Session
#
# Indicates whether an SSO session should be created for renewed authentication requests.
# create.sso.renewed.authn=true
{% endhighlight %}


