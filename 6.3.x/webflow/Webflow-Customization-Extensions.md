---
layout: default
title: CAS - Web Flow Extensions
category: Webflow Management
---

# Extending CAS Webflow

The objective of this guide is to better describe how CAS utilizes Spring Webflow to accommodate various authentication flows. Please remember that this is **NOT** to teach one how Spring Webflow itself works internally. If you want to learn more about Spring Webflow and understand the internals of actions, states, decisions and scopes please [see this guide](http://projects.spring.io/spring-webflow/).

CAS by default operates on the following core webflow configuration files:

| Flow                | Location
|---------------------|-----------------------------------------------
| `login`             | `src/main/resources/webflow/login-webflow.xml`
| `logout`            | `src/main/resources/webflow/logout-webflow.xml`

The above flow configuration files present a minimal structure for what CAS needs at its core to handle login and logout flows. It is important to note that at runtime many other actions and states are injected into either of these flows dynamically depending on the CAS configuration and presence of feature modules. Also note that each feature module itself may dynamically present other opinionated subflow configuration files that are automagically picked up at runtime.

So in truth, what you see above is not necessarily all of what you may get.

<div class="alert alert-warning"><strong>Live Happily</strong><p>It is best to <strong>AVOID</strong> overlaying/modifying flow configuration files by hand manually. The flow configuration files are not considered public APIs, are not compiled and in most cases are no candidates for backward-compatibility. CAS attempts to automate all webflow changes dynamically where appropriate. Staying away from manual changes will only make your future upgrades easier. Only do so in very advanced cases and be SURE to know what you are doing!</p></div>

## Modifying Webflow

In modest trivial cases, you may be able to [overlay and modify](../installation/WAR-Overlay-Installation.html) the core flow configuration files to add or override the desired behavior. Again, think very carefully before introducing those changes into your deployment environment. Avoid making ad-hoc changes to the webflow as much as possible and consider how the change you have in mind might be more suitable as a direct contribution to the CAS project itself so you can just take advantage of its configuration and *NOT* its maintenance.

To learn how to introduce new actions and state into a Spring Webflow, please [see this guide](http://projects.spring.io/spring-webflow/).

<div class="alert alert-info"><strong>Speak Up</strong><p>If you find something that is broken where the webflow auto-configuration strategy fails to deliver as advertised, discuss that with the project community and submit a patch that corrects the bug or adds the desired behavior as a modest enhancement. Avoid one-off changes and make the change where the change belongs.</p></div>

In more advanced cases where you may need to take a deep dive and alter core CAS behavior conditionally, you would need to take advantage of the CAS APIs to deliver changes. Using the CAS APIs directly does present the following advantages at some cost:

- Changes are all scoped to Java (Groovy, Kotlin, Clojure, etc).
- You have the full power of Java to dynamically and conditionally augment the Spring Webflow.
- Your changes are all self-contained.
- Changes are now part of the CAS APIs and they will be compiled. Breaking changes on upgrades, if any, should be noticed immediately at build time.

### Java

This is the most traditional yet most powerful method of dynamically altering the webflow internals. You will be asked to write components that auto-configure the webflow and inject themselves into the running CAS application context only to be executed at runtime.

At a minimum, your overlay will need to include the following modules:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-core-webflow</artifactId>
     <version>${cas.version}</version>
</dependency>
```

#### Design

Design your dynamic webflow configuration agent that alters the webflow using the following form:

```java
public class SomethingWebflowConfigurer extends AbstractCasWebflowConfigurer {
     public SomethingWebflowConfigurer(FlowBuilderServices flowBuilderServices,
                                       FlowDefinitionRegistry flowDefinitionRegistry,
                                       ApplicationContext applicationContext,
                                       CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
     }

    @Override
    protected void doInitialize() throws Exception {
        final Flow flow = super.getLoginFlow();
        // Magic happens; Call 'super' to see what you have access to and alter the flow.
    }
}
```

#### Register

You will then need to register your newly-designed component into the CAS application runtime:

```java
package org.example.something;

@Configuration("somethingConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class SomethingConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @ConditionalOnMissingBean(name = "somethingWebflowConfigurer")
    @Bean
    public CasWebflowConfigurer somethingWebflowConfigurer() {
        return new SomethingWebflowConfigurer(flowBuilderServices,
                    loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(somethingWebflowConfigurer());
    }
}
```

Configuration classes need to be registered with CAS inside a `src/main/resources/META-INF/spring.factories` file:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.example.something.SomethingConfiguration
```

See [this guide](https://https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features) for more info.

### Groovy

You may configure CAS to alter and auto-configure the webflow via a Groovy script. This is the less elaborate option where you have modest access to CAS APIs that allow you alter the webflow. However, configuration and scaffolding of the overlay and required dependencies is easier as all is provided by CAS at runtime.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#spring-webflow-groovy-auto-configuration).

<div class="alert alert-warning"><strong>Stop Coding</strong><p>Remember that APIs provided here, specifically executed as part of the Groovy script are considered implementations internal to CAS mostly. They may be added or removed with little hesitation which means changes may break your deployment and upgrades at runtime. Remember that unlike Java classes, scripts are not statically compiled when you build CAS and you only may observe failures when you do in fact turn on the server and deploy. Thus, choose this option with good reason and make sure you have thought changes through before stepping into code.</p></div>

A sample Groovy script follows that aims to locate the CAS login flow and a particular state pre-defined in the flow. If found, a custom action is inserted into the state to execute as soon as CAS enters that state in the flow. While this is a rather modest example, note that the script has the ability to add/remove actions, states, transitions, add/remove subflows, etc.

```groovy
import java.util.*

import org.apereo.cas.*
import org.apereo.cas.web.*
import org.apereo.cas.web.support.*
import org.apereo.cas.web.flow.*

import org.springframework.webflow.*
import org.springframework.webflow.engine.*
import org.springframework.webflow.execution.*

def Object run(final Object... args) {
    def webflow = args[0]
    def springApplicationContext = args[1]
    def logger = args[2]

    logger.info("Configuring webflow context...")

    def loginFlow = webflow.getLoginFlow()
    if (webflow.containsFlowState(loginFlow, CasWebflowConstants.STATE_ID_INIT_LOGIN_FORM)) {
        logger.info("Found state that initializes the login form")

        def state = webflow.getState(loginFlow, CasWebflowConstants.STATE_ID_INIT_LOGIN_FORM, ActionState.class)
        logger.info("The state id is {}", state.id)

        state.getEntryActionList().add({ requestContext ->
            def flowScope = requestContext.flowScope
            def httpRequest = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext)

            logger.info("Action executing as part of ${state.id}. Stuff happens...")
            return new Event(this, "success")
        })

        logger.info("Added action to ${state.id}'s entry action list")
    }

    return true
}
```

The parameters passed are as follows:

| Parameter               | Description
|-------------------------|------------------------------------------------------------------------------
| `webflow`                    | The object representing a facade on top of Spring Webflow APIs.
| `springApplicationContext`   | The Spring application context.
| `logger`                     | Logger object for issuing log messages such as `logger.info(...)`.
