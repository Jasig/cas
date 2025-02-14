---
layout: default
title: CAS - Webflow Decorations
category: Webflow Management
---

# Webflow Decorations

There are times where you may need to modify the CAS login webflow to include additional data, typically fetched from outside resources and endpoints that may also be considered sensitive and may require credentials for access. Examples include displaying announcements on the CAS login screen or other types of dynamic data. You can certainly [extend the webflow](Webflow-Customization-Extensions.html) to include additional states and actions into the login flow to call upon endpoints and data sources to fetch data. An easier option be to let CAS decorate the login webflow automatically by reaching out to your REST endpoints, etc, taking care of the internal webflow configuration. Such decorators specifically get called upon as CAS begins to render the login view while reserving the right to decorate additional parts of the webflow in the future.

Note that decorators only inject data into the webflow context where that data later on becomes available to the CAS login view, and more. Once the data is available, you still have the responsibility of using that data to properly display it in the appropriate view and style it correctly.

## Groovy Decorators

Groovy login decorators allow one to inject data into the webflow context by using an external Groovy script that may take on the following form:

```groovy
def run(Object[] args) {
     def requestContext = args[0]
     def applicationContext = args[1]
     def logger = args[2]
     
     logger.info("Decorating the webflow...")
     requestContext.flowScope.put("decoration", ...)
 }
``` 

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `requestContext`      | The Spring Webflow `RequestContext` that carries various types of scopes as data containers.
| `applicationContext`  | The Spring application context.
| `logger`              | Logger object used to issue log messages where needed.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#spring-webflow-login-decorations).

## REST Decorators

RESTful login decorators allow one to inject data into the webflow context by reaching out to an external REST API. If the endpoint responds back with a `200` status code, CAS would parse the response body into a JSON object and will stuff the result into the webflow's `flowScope` container under the key `decoration`. Please remember that data stuff into the webflow **MUST** be serializable and if you intend to pass along complex objects types and fancy data structures, you need to make sure they can safely and ultimately transform into a simple `byte[]`.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#spring-webflow-login-decorations).
