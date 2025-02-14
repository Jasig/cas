---
layout: default
title: CAS - GUA Authentication
category: Authentication
---

# Graphical User Authentication

Graphical user authentication, sometimes also known as 'login images' are a form of login verification (i.e. second factor) where a site presents the user with an image previously selected by the user at the time the account is created. It is an "account secret" tied to the username that should not be easily reproduced by a phishing campaign attempting to impersonate a legitimate website.

In practice, CAS prompts the user for only their username and responds with a page displaying what should be the user's pre-selected image along with a password field to complete their authentication. The user in turn is to be trained to refuse submitting the rest of their login credentials to a site posing to be legitimate if CAS fails to present the correct image.

## Overview

Support is enabled by including the following module in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-gua</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#gua-authentication).

### Resource

Primarily useful for demo and testing purposes, this option allows CAS to load a global and static image resource
as the user identifier onto the login flow.

### LDAP

CAS may also be allowed to locate a binary image attribute for the user from LDAP. The binary attribute value is then loaded
as the user identifier onto the login flow.
