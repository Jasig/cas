---
layout: default
title: CAS - Service Management
---

# Service Management

The CAS service management facility allows CAS server administrators to declare and configure which services
(CAS clients) may make use of CAS in which ways. The core component of the service management facility is the
service registry that stores one or more registered services containing metadata that drives a number of CAS behaviors:

* Authorized services - Control which services may participate in a CAS SSO session.
* Forced authentication - Provides administrative control for forced authentication.
* Attribute release - Provide user details to services for authorization and personalization.
* Proxy control - Further restrict authorized services by granting/denying proxy authentication capability.
* Theme control - Define alternate CAS themes to be used for particular services.

The service management webapp is a Web application that may be deployed along side CAS that provides a GUI
to manage service registry data.

## Demo

The Service Management web application is available for demo at [https://jasigcasmgmt.herokuapp.com/cas-management](https://jasigcasmgmt.herokuapp.com/cas-management)


## Considerations

It is not required to use the service management facility explicitly. CAS ships with a default configuration that is
suitable for deployments that do not need or want to leverage the capabilities above. The default configuration allows
any service contacting CAS over https/imaps to use CAS.

It is perfectly acceptable to avoid the service management webapp Web application for managing registered service data.
In fact, configuration-driven methods (e.g. XML, JSON) may be preferable in environments where strict configuration
management controls are required.

## Registered Services

Registered services present the following metadata:

| Field                             | Description
|-----------------------------------+--------------------------------------------------------------------------------+
| `id`                              | Required unique identifier. In most cases this is managed automatically by the `ServiceRegistryDao`.
| `name`                            | Required name (255 characters or less).
| `description`                     | Optional free-text description of the service. (255 characters or less)
| `logo`                              | Optional path to an image file that is the logo for this service. The image will be displayed on the login page along with the service description and name.  
| `serviceId`                       | Required [Ant pattern](http://ant.apache.org/manual/dirtasks.html#patterns) or [regular expression](http://docs.oracle.com/javase/tutorial/essential/regex/) describing a logical service. A logical service defines one or more URLs where a service or services are located. The definition of the url pattern must be **done carefully** because it can open security breaches. For example, using Ant pattern, if you define the following service : `http://example.*/myService` to match `http://example.com/myService` and `http://example.fr/myService`, it's a bad idea as it can be tricked by `http://example.hostattacker.com/myService`. The best way to proceed is to define the more precise url patterns.
| `theme`                           | Optional [Spring theme](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-themeresolver) that may be used to customize the CAS UI when the service requests a ticket. See [this guide](User-Interface-Customization.html) for more details.
| `proxyPolicy`                     | Determines whether the service is able to proxy authentication, not whether the service accepts proxy authentication.
| `evaluationOrder`                 | Required value that determines relative order of evaluation of registered services. This flag is particularly important in cases where two service URL expressions cover the same services; evaluation order determines which registration is evaluated first.
| `requiredHandlers`                | Set of authentication handler names that must successfully authenticate credentials in order to access the service.
| `attributeReleasePolicy`          | The policy that describes the set of attributes allows to be released to the application, as well as any other filtering logic needed to weed some out. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `logoutType`                      | Defines how this service should be treated once the logout protocol is initiated. Acceptable values are `LogoutType.BACK_CHANNEL`, `LogoutType.FRONT_CHANNEL` or `LogoutType.NONE`. See [this guide](Logout-Single-Signout.html) for more details on logout.
| `usernameAttributeProvider`       | The provider configuration which dictates what value as the "username" should be sent back to the application. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `accessStrategy`                  | The strategy configuration that outlines and access rules for this service. It describes whether the service is allowed, authorized to participate in SSO, or can be granted access from the CAS perspective based on a particular attribute-defined role, aka RBAC. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `publicKey`                  		| The public key associated with this service that is used to authorize the request by encrypting certain elements and attributes in the CAS validation protocol response, such as [the PGT](Configuring-Proxy-Authentication.html) or [the credential](../integration/ClearPass.html). See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `logoutUrl`                  		| URL endpoint for this service to receive logout requests. See [this guide](Logout-Single-Signout.html) for more details
| `properties`                  		| Extra metadata associated with this service in form of key/value pairs. This is used to inject custom fields into the service definition, to be used later by extension modules to define additional behavior on a per-service basis.

### Service Access Strategy

[See this guide](Configuring-Service-Access-Strategy.html) for more info please.

### Proxy Authentication Policy

[See this guide](Configuring-Service-Proxy-Policy.html) for more info please.

### Service Custom Properties

[See this guide](Configuring-Service-Custom-Properties.html) for more info please.

## Persisting Services

### In-Memory

This DAO is an in-memory services management seeded from registration beans wired via Spring beans.

```xml
<bean id="serviceRegistryDao"
      class="org.jasig.cas.services.InMemoryServiceRegistryDaoImpl"
      p:registeredServices-ref="registeredServicesList" />

<util:list id="registeredServicesList">
    <bean class="org.jasig.cas.services.RegexRegisteredService"
          p:id="1"
          p:name="HTTPS and IMAPS services on example.com"
          p:serviceId="^(https|imaps)://([A-Za-z0-9_-]+\.)*example\.com/.*"
          p:evaluationOrder="0" />
</util:list>

```

This component is _NOT_ suitable for use with the service management webapp since it does not persist data.
On the other hand, it is perfectly acceptable for deployments where the XML configuration is authoritative for
service registry data and the UI will not be used.

### JSON

[See this guide](JSON-Service-Management.html) for more info please.

### MongoDb

[See this guide](Mongo-Service-Management.html) for more info please.

### Ldap

[See this guide](LDAP-Service-Management.html) for more info please.

### Jpa

[See this guide](JPA-Service-Management.html) for more info please.

## Service Management Webapp

The Services Management web application is a standalone application that helps one manage service registrations and entries via a customizable user interface. The management web application *MUST* share the same registry configuration as
the CAS server itself so the entire system can load the same services data. To learn more about the management webapp,
[please see this guide](Installing-ServicesMgmt-Webapp.html).
