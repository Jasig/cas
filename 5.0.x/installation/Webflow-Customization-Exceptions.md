---
layout: default
title: CAS - Web Flow Customization
---

# Webflow Errors Customization

By default CAS is configured to recognize and handle 10 exceptions that are subclasses of `GeneralSecurityException` or
`PreventedException` in the web flow during authentication transactions processing. Handling is defined by a component called
`AuthenticationExceptionHandler` that has a pre-configured map of these default exceptions which is able to deduce from it a next webflow
event when CAS server catches and passes one of these exceptions to it. The event returned determines the 
next transition to a state and in most cases that state is the login view. The trick here is that each of those 
exceptions have the specific message bundle mapping in `messages.properties` So that a specific message could be presented to end users on
 the login form. Any un-recognized or un-mapped exceptions of type
`GeneralSecurityException`, `PreventedException` or `AbstractTicketException` presented to `AuthenticationExceptionHandler` 
results in  the `UNKNOWN` mapping with a generic `Invalid credentials.` message.

Suppose that there is a need for a custom authentication handler that throws a custom `GeneralSecurityException` with a very specific
message in this case presented to UI (suppose that the exception type thrown is `com.mycompany.MyAuthenticationException`). To achieve
this customization, one would need to do the following:

Define custom message mapping in `messages.properties` (or the variant for the locale in question):

```properties
authenticationFailure.MyAuthenticationException=Authentication has failed, but it did it my way!
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
