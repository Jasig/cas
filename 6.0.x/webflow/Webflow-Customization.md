---
layout: default
title: CAS - Webflow Customization
category: Webflow Management
---

# Webflow Customization

CAS uses [Spring Webflow](http://projects.spring.io/spring-webflow) to do *script* processing of login and logout protocols.
Spring Web Flow builds on Spring MVC and allows implementing the "flows" of a web application. A flow encapsulates a sequence
of steps that guide a user through the execution of some business task. It spans multiple HTTP requests, has state, deals with
 transactional data, is reusable, and may be dynamic and long-running in nature. Each flow may contain among many other settings the following major elements:

- Actions: components that describe an executable task and return back a result
- Transitions: Routing the flow from one state to another; Transitions may be global to the entire flow.
- Views: Components that describe the presentation layer displayed back to the client
- Decisions: Components that conditionally route to other areas of flow and can make logical decisions

Spring Web Flow presents CAS with a pluggable architecture where custom actions, views and decisions may be injected into the
flow to account for additional use cases and processes. Note that <strong>to customize the webflow, one must possess a reasonable level of understanding of the webflow's internals and injection policies</strong>. The intention of this document is NOT to describe Spring Web Flow, but merely to demonstrate how the framework is used by CAS to carry out various aspects of the protocol and business logic execution.

## Webflow Session

See [this guide](Webflow-Customization-Sessions.html) for more info.

## Webflow Auto Configuration

Most CAS modules, when declared as a dependency, attempt to autoconfigure the CAS webflow to suit their needs.
This practically means that the CAS adopter would no longer have to manually massage the CAS webflow configuration,
and the module automatically takes care of all required changes. While this is the default behavior, it is possible that
you may want to manually handle all such changes. For doing so, you will need to disable the CAS autoconfiguration
of the webflow.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#spring-webflow).

<div class="alert alert-warning"><strong>Achtung, liebe Leser!</strong><p>Only attempt to 
modify the Spring webflow configuration files by hand when/if absolutely necessary and the
change is rather minimal or decorative. Extensive modifications of the webflow, if not done carefully
may serverely complicate your deployment and future upgrades. If reasonable, consider contributing or
suggesting the change to the project and have it be maintained directly.</p></div>

CAS by default is configured to hot reload changes to the Spring webflow configuration.

### Extending Webflow

If you want to learn how to modify and extend the CAS authentication flows, [please see this guide](Webflow-Customization-Extensions.html).

### Webflow Decorations

Learn how to fetch and display data dynamically from external data sources and endpoints and pass those along to the webflow by [reviewing this guide](Webflow-Customization-Extensions.html).

## Required Service for Authentication

By default, CAS will present a generic success page if the initial authentication request does not identify
the target application. In some cases, the ability to login to CAS without logging
in to a particular service may be considered a misfeature because in practice, too few users and institutions
are prepared to understand, brand, and support what is at best a fringe use case of logging in to CAS for the
sake of establishing an SSO session without logging in to any CAS-reliant service.

As such, CAS optionally allows adopters to not bother to prompt for credentials when no target application is presented
and instead presents a message when users visit CAS directly without specifying a service.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#global-sso-behavior).

## Acceptable Usage Policy

CAS presents the ability to allow the user to accept the usage policy before moving on to the application.
See [this guide](Webflow-Customization-AUP.html) for more info.

## Customizing Errors

See [this guide](Webflow-Customization-Exceptions.html) for more info.

## Custom Settings

All webflow components and CAS views have access to the entire bundle of CAS settings defined from a variety of configuration sources. This allows one to extend and modify any CAS view or webflow component using the variable `casProperties` to gain access to a specific setting. Remember that this syntax only allowed access to settings that are *owned* by CAS, noted by its very own prefix.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#custom-settings).

