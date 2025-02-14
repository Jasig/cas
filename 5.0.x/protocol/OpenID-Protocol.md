---
layout: default
title: CAS - OpenID Protocol
---

# OpenID Protocol

OpenID is an open, decentralized, free framework for user-centric digital identity. Users represent 
themselves using URIs. For more information see the [http://www.openid.net](http://www.openid.net).

CAS supports both the "dumb" and "smart" modes of the OpenID protocol. Dumb mode acts in a similar fashion 
to the existing CAS protocol. The smart mode differs in that it establishes an association between the client and 
the openId provider (OP) at the beginning. Thanks to that association and the key exchange done during association, 
information exchanged between the client and the provider are signed and verified using this key. There is no need 
for the final request (which is equivalent in CAS protocol to the ticket validation).

OpenID identifiers are URIs. The default mechanism in CAS support is an uri ending with the actual user login 
(ie. `http://my.cas.server/openid/myusername` where the actual user login id is `myusername`).
This is not recommended and you should think of a more elaborated way of providing URIs to your users.

<div class="alert alert-info"><strong>Pay Attention!</strong><p>OpenID protocol is <strong>NOT</strong> the same thing
as the OpenId Connect protocol whose details are <a href="OIDC-Protocol.html">documented here</a>.</p></div>

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-openid-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## OpenID v2.0

To define the CAS server as an OpenID provider v2.0, add the `yadis.xml` file at the root of your CAS deployment. For example,
if your deployment is available at `https://sso.example.org/cas`, then the `yadis.xml` file must be available at 
`https://sso.example.org/cas/yadis.xml`. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xrds:XRDS xmlns:xrds="xri://$xrds" xmlns="xri://$xrd*($v*2.0)"
           xmlns:openid="http://openid.net/xmlns/1.0">
<XRD>
    <Service priority="1">
        <Type>http://specs.openid.net/auth/2.0/signon</Type>
        <URI>https://sso.example.org/cas/login</URI>
    </Service>
</XRD>
</xrds:XRDS>
```

# OpenID Provider Delegation

Using the OpenID protocol, the CAS server can also be configured 
to [delegate the authentication](../integration/Delegate-Authentication.html) to an OpenID provider.
