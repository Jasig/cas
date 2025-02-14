---
layout: default
title: CAS - Service Management
---

# Service Management

The CAS service management facility allows CAS server administrators to declare and configure which services
(CAS clients) may make use of CAS in which ways. The core component of the service management facility is the
service registry that stores one or more registered services
containing metadata that drives a number of CAS behaviors:

* Authorized services - Control which services may participate in a CAS SSO session.
* Forced authentication - Provides administrative control for forced authentication.
* Attribute release - Provide user details to services for authorization and personalization.
* Proxy control - Further restrict authorized services by granting/denying proxy authentication capability.
* Theme control - Define alternate CAS themes to be used for particular services.

## Service Management Webapp

The service management webapp is a Web application that may be deployed along side CAS that provides a GUI
to manage service registry data. The Service Management web application is available for demo at [https://jasigcasmgmt.herokuapp.com/cas-services](https://jasigcasmgmt.herokuapp.com/cas-services)

The Services Management web application is a standalone application that helps one manage service registrations and
entries via a customizable user interface. The management web application *MUST* share the same registry configuration as
the CAS server itself so the entire system can load the same services data. To learn more about the management webapp,
[please see this guide](Installing-ServicesMgmt-Webapp.html).

## Registered Services

Registered services present the following metadata:

| Field                             | Description
|-----------------------------------|---------------------------------------------------------------------------------
| `id`                              | Required unique identifier. In most cases this is managed automatically by the `ServiceRegistryDao`. This **MUST** be a valid numeric value.
| `name`                            | Required name (255 characters or less).
| `description`                     | Optional free-text description of the service. (255 characters or less)
| `logo`                              | Optional path to an image file that is the logo for this service. The image will be displayed on the login page along with the service description and name.  
| `serviceId`                       | Required [Ant pattern](http://ant.apache.org/manual/dirtasks.html#patterns) or [regular expression](http://docs.oracle.com/javase/tutorial/essential/regex/) describing a logical service. A logical service defines one or more URLs where a service or services are located. The definition of the url pattern must be **done carefully** because it can open security breaches. For example, using Ant pattern, if you define the following service : `http://example.*/myService` to match `http://example.com/myService` and `http://example.fr/myService`, it's a bad idea as it can be tricked by `http://example.hostattacker.com/myService`. The best way to proceed is to define the more precise url patterns.
| `theme`                           | Optional [Spring theme](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-themeresolver) that may be used to customize the CAS UI when the service requests a ticket. See [this guide](User-Interface-Customization.html) for more details.
| `proxyPolicy`                     | Determines whether the service is able to proxy authentication, not whether the service accepts proxy authentication.
| `evaluationOrder`                 | Required value that determines relative order of evaluation of registered services. This flag is particularly important in cases where two service URL expressions cover the same services; evaluation order determines which registration is evaluated first.
| `requiredHandlers`                | Set of authentication handler names that must successfully authenticate credentials in order to access the service. If defined, only the selected required handlers are chosen to respond to authentication requests from this registered service.
| `attributeReleasePolicy`          | The policy that describes the set of attributes allows to be released to the application, as well as any other filtering logic needed to weed some out. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `logoutType`                      | Defines how this service should be treated once the logout protocol is initiated. Acceptable values are `LogoutType.BACK_CHANNEL`, `LogoutType.FRONT_CHANNEL` or `LogoutType.NONE`. See [this guide](Logout-Single-Signout.html) for more details on logout.
| `usernameAttributeProvider`       | The provider configuration which dictates what value as the "username" should be sent back to the application. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `accessStrategy`                  | The strategy configuration that outlines and access rules for this service. It describes whether the service is allowed, authorized to participate in SSO, or can be granted access from the CAS perspective based on a particular attribute-defined role, aka RBAC. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `publicKey`                  		| The public key associated with this service that is used to authorize the request by encrypting certain elements and attributes in the CAS validation protocol response, such as [the PGT](Configuring-Proxy-Authentication.html) or [the credential](../integration/ClearPass.html). See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `logoutUrl`                  		| URL endpoint for this service to receive logout requests. See [this guide](Logout-Single-Signout.html) for more details
| `properties`                  	| Extra metadata associated with this service in form of key/value pairs. This is used to inject custom fields into the service definition, to be used later by extension modules to define additional behavior on a per-service basis. [See this guide](Configuring-Service-Custom-Properties.html) for more info please.
| `multifactorPolicy`           	| The policy that describes the configuration required for this service authentication, typically for [multifactor authentication](Configuring-Multifactor-Authentication.html).


### Service Access Strategy

[See this guide](Configuring-Service-Access-Strategy.html) for more info please.

### Proxy Authentication Policy

[See this guide](Configuring-Service-Proxy-Policy.html) for more info please.

### Service Custom Properties

[See this guide](Configuring-Service-Custom-Properties.html) for more info please.

## Persisting Services

The following options may be used to store services in CAS.

### Memory

[See this guide](InMemory-Service-Management.html) for more info please.

### JSON

[See this guide](JSON-Service-Management.html) for more info please.

### YAML

[See this guide](YAML-Service-Management.html) for more info please.

### Mongo

[See this guide](Mongo-Service-Management.html) for more info please.

### LDAP

[See this guide](LDAP-Service-Management.html) for more info please.

### JPA

[See this guide](JPA-Service-Management.html) for more info please.

### Couchbase

[See this guide](Couchbase-Service-Management.html) for more info please.
