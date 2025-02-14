---
layout: default
title: CAS - Protocol Overview
category: Protocols
---

# Protocols Overview

The following protocols are supported and provided by CAS:

*   [CAS](CAS-Protocol.html)
*   [OpenID](OpenID-Protocol.html)
*   [OAuth](OAuth-Protocol.html)
*   [OpenID Connect](OIDC-Protocol.html)
*   [WS Federation](WS-Federation-Protocol.html)
*   [SAML1](SAML-Protocol.html)
*   [SAML2](../installation/Configuring-SAML2-Authentication.html)
*   [REST Protocol](REST-Protocol.html)

## Design

CAS presents itself as a multilingual platform supporting protocols such as CAS, SAML2, OAuth2 and OpenID Connect, etc. Support and functionality for each of these protocols continually improves per every iteration and release of the software thanks to excellent community feedback and adoption. While almost all such protocols are similar in nature and intention, they all have their own specific bindings, parameters, payload and security requirements. This section provides a quick introduction on how existing protocols are supported in CAS.

It all starts with something rather trivial: The Bridge.

### The Bridge

The bridge *design pattern* is an approach where an intermediary sits between the client and the server, translating requests back and forth. It simply acts as a link between the two sides allowing authentication requests from the client to be translated, massaged and transformed and then routed "invisibly" to CAS and then back.

This is a neat trick because the client does not care how the authentication request is processes once it's submitted. The *thing* that receives that request, acting as a bridge can do anything required to process that request and ultimately submitting some sort of response back to the client. The bridge also does not care what external authentication system handles and honors that request and how all that processing internally works. All the bridge cares about is, "I routed the request to X. As long as X gives me back the right stuff, I should be fine to resume".

So the bridge for the most part is the "control tower" of the operation. It speaks many languages and protocols, and just like any decent translator, it knows about the quirks and specifics of each language and as such is able to dynamically translate the technical lingo.

### Supported Protocols

If you understand the above strategy, then you would be glad to learn that *almost* all protocols supported by CAS operate with the same exact intentions. A given CAS deployment is equipped with embedded plugins/bridges/modules that know how to speak SAML2 and CAS, OAuth2 and CAS, or OpenID Connect and CAS or whatever. The right-hand side of that equation is always CAS when you consider, as an example, the following authentication flow with an OAuth2-enabled client application:

1. The CAS deployment has turned on the OAuth2 plugin.
2. An OAuth2 authorization request is submitted to the relevant CAS endpoint.
3. The OAuth2 plugin verifies the request and translates it to a CAS authentication request!
4. The authentication request is routed to the relevant CAS login endpoint.
5. User authenticates and CAS routes the flow back to the OAuth2 plugin, having issued a service ticket for the plugin.
6. The OAuth2 plugin attempts to validate that ticket to retrieve the necessary user profile and attributes.
7. The OAuth2 plugin then proceeds to issue the right OAuth2 response by translating and transforming the profile and validated assertions into what the client application may need.

<div class="alert alert-info"><strong>Note</strong><p>The above strategy applies exactly the same, if CAS decides to delegate the authentication to an external identity provider such as Facebook or a SAML2 identity provider.</p></div>

The right-hand side of the flow is always CAS, because the plugin always translates protocol requests into CAS requests. Another way of looking at it is that all protocol plugins and modules are themselves clients of the CAS server! They are issued service tickets and they proceed to validate them just like any other CAS-enabled client. Just like above, to the OAuth2-enabled client all such details are totally transparent and as long as “the right stuff” is produced back to the client, it shall not care.

There are some internal technical and architectural advantages to this approach. Namely:

The core of the CAS authentication engine, flow and components need not be modified at all. After all, we are just integrating yet another client even if it’s embedded directly in CAS itself. Because of that, support for that protocol can be very easily removed, if needed. After all, protocols come and go every day. Finally and just like any other CAS client, all features of the CAS server are readily available and translated to the relevant client removing the need to duplicate and re-create protocol-specific configuration as much as  possible. Things like access strategies, attribute release, username providers, etc.
