---
layout: default
title: CAS - OAuth Protocol
---

# OAuth Protocol
You can configure the CAS server with:

* [OAuth client support](../integration/Delegate-Authentication.html), which means authentication can be delegated through a link on the login page to a CAS, OpenID or OAuth provider. 
* [OAuthn server support](../installation/OAuth-OpenId-Authentication.html), which means you will be able to communicate with your CAS server through the [OAuth 2.0 protocol](http://oauth.net/2/), using the *Authorization Code* grant type.

# CAS OAuth Server Support
Three new urls will be available:

* **/oauth2.0/authorize**  
It's the url to call to authorize the user: the CAS login page will be displayed and the user will authenticate. After successful authentication, the user will be redirected to the OAuth *callback url* with a code. Input GET parameters required: *client_id* and *redirect_uri*.

* **/oauth2.0/accessToken**  
It's the url to call to exchange the code for an access token. Input GET parameters required: *client_id*, *redirect_uri*, *client_secret* and *code*.

* **/oauth2.0/profile**  
It's the url to call to get the profile of the authorized user. Input GET parameter required: *access_token*. The response is in JSON format with all attributes of the user.

# Delegate to an OAuth Provider

Using the OAuth protocol, the CAS server can also be configured to [delegate the authentication](../integration/Delegate-Authentication.html) to an OAuth provider (like Facebook, Twitter, Google, Yahoo...)

