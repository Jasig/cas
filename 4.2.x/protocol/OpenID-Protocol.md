---
layout: default
title: CAS - OpenID Protocol
---

# OpenID Protocol
OpenID is an open, decentralized, free framework for user-centric digital identity. Users represent themselves using URIs. For more information see the [http://www.openid.net](http://www.openid.net).

CAS supports both the "dumb" and "smart" modes of the OpenID protocol. Dumb mode acts in a similar fashion to the existing CAS protocol. The smart mode differs in that it establishes an association between the client and the openId provider (OP) at the begining. Thanks to that association and the key exchange done during association, information exchanged between the client and the provider are signed and verified using this key. There is no need for the final request (which is equivalent in CAS protocol to the ticket validation).

OpenID identifiers are URIs. The default mechanism in CAS support is an uri ending with the actual user login (ie. `http://my.cas.server/openid/myusername` where the actual user login id is `myusername`). This is not recommended and you should think of a more elaborated way of providing URIs to your users.

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-openid-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## OpenID v2.0 support

By default, the CAS server is defined as an OpenID provider v1.0. This definition is held in the `user.jsp` file (in the `WEB-INF/view/jsp/protocol/openid` directory):

```xml
<html>
<head>
    <link rel="openid.server" href="${openid_server}"/>
</head>
</html>
```

To define the CAS server as an OpenID provider v2.0, the exposed endpoint must be changed accordingly. To do that, the first thing is to replace the content of the `user.jsp` file by a new file pointing to the appropriate Yadis definition:

```xml
<html>
<head>
    <meta http-equiv="X-XRDS-Location" content="http://mycasserver/yadispath/yadis.xml" />
</head>
</html>
```

And to add this Yadis definition on some publicly accessible url (in the above example, it is `htp://mycasserver/yadispath/yadis.xml`) as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xrds:XRDS xmlns:xrds="xri://$xrds" xmlns="xri://$xrd*($v*2.0)"
           xmlns:openid="http://openid.net/xmlns/1.0">
<XRD>
    <Service priority="1">
        <Type>http://specs.openid.net/auth/2.0/signon</Type>
        <URI>http://mycasserver/login</URI>
    </Service>
</XRD>
</xrds:XRDS>
```

This XML content defines the CAS server available on `http://mycasserver/login` (to be changed for your server) as an OpenID provider v2.0 because of the type of service (`http://specs.openid.net/auth/2.0/signon`).

***

# Delegate To an OpenID Provider

Using the OpenID protocol, the CAS server can also be configured to [delegate the authentication](../integration/Delegate-Authentication.html) to an OpenID provider.
