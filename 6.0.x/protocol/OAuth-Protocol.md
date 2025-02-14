---
layout: default
title: CAS - OAuth Protocol
category: Protocols
---

# OAuth Protocol

You can configure the CAS server with:

* [OAuth client support](../integration/Delegate-Authentication.html), which means authentication can be delegated 
through a link on the login page to a CAS, OpenID or OAuth provider. 
* [OAuth server support](../installation/OAuth-OpenId-Authentication.html), which means you will be able to 
communicate with your CAS server through the [OAuth 2.0 protocol](http://oauth.net/2/).

## UMA

User-Managed Access (UMA) is a lightweight access control protocol that defines a centralized workflow to allow an entity (user or corporation) 
to manage access to their resources. UMA extends the OAuth protocol and gives resource owners granular management of their protected resources 
by creating authorization policies on a centralized authorization server, such as CAS. The authorization server grants delegated consent to a 
requesting party on behalf of the resource owner to authorize who and what can get access to their data and for how long.

To learn more about UMA support in CAS, [please see this guide](OAuth-UMA-Protocol.html).
