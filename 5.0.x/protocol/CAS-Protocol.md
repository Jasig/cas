---
layout: default
title: CAS - CAS Protocol
---


# CAS protocol
The CAS protocol is a simple and powerful ticket-based protocol developed exclusively for CAS. A complete protocol specification may be found [here](CAS-Protocol-Specification.html).

It involves one or many clients and one server. Clients are embedded in *CASified* applications (called "CAS services") whereas the CAS server is a standalone component:

- The [CAS server](../installation/Configuring-Authentication-Components.html) is responsible for authenticating users and granting accesses to applications
- The [CAS clients](../integration/CAS-Clients.html) protect the CAS applications and retrieve the identity of the granted users from the CAS server.

The key concepts are:

- The TGT (Ticket Granting Ticket), stored in the CASTGC cookie, represents a SSO session for a user
- The ST (Service Ticket), transmitted as a GET parameter in urls, stands for the access granted by the CAS server to the *CASified* application for a specific user.


## Specification versions

The following specification versions are recognized and implemented by Apereo CAS. 

### 3.0.2
The current CAS protocol specification is `3.0.2`. The actual protocol specification is available at [CAS-Protocol-Specification](CAS-Protocol-Specification.html), which is hereby implemented by the Apereo CAS Server as the official reference implementation. It's mainly a capture of the most common enhancements built on top of the CAS protocol revision `2.0`. Among all features, the most noticeable update between versions `2.0` and `3.0` is the ability to return the authentication/user attributes through the new `/p3/serviceValidate` response, in addition to the `/serviceValidate` endpoint from CAS `2.0` protocol.

### 2.0
The version `2.0` protocol specification is available at [CAS-Protocol-Specification](CAS-Protocol-V2-Specification.html). 

## Web flow diagram

<a href="../images/cas_flow_diagram.png" target="_blank"><img src="../images/cas_flow_diagram.png" alt="CAS Web flow diagram" title="CAS Web flow diagram" /></a>


## Proxy web flow diagram
One of the most powerful feature of the CAS protocol is the ability for a CAS service to act as a proxy for another CAS service, transmitting the user identity.

<a href="../images/cas_proxy_flow_diagram.jpg" target="_blank"><img src="../images/cas_proxy_flow_diagram.jpg" alt="CAS Proxy web flow diagram" title="CAS Proxy web flow diagram" /></a>




## Other protocols
Even if the primary goal of the CAS server is to implement the CAS protocol, other protocols are also supported as extensions:

- [OpenID](../protocol/OpenID-Protocol.html)
- [OAuth](../protocol/OAuth-Protocol.html)
- [SAML](../protocol/SAML-Protocol.html)

***



# Delegated Authentication
Using the CAS protocol, the CAS server can also be configured to [delegate the authentication](../integration/Delegate-Authentication.html) to another CAS server.

