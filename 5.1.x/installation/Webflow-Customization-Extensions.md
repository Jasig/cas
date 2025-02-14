---
layout: default
title: CAS - Web Flow Extensions
---

# Extending CAS Webflow

The objective of this guide is to better describe how CAS utilizes Spring Webflow to accommodate various authentication flows. Please remember that this is **NOT** to teach one how Spring Webflow itself works internally. If you want to learn more about Spring Webflow and understand the internals of actions, states, decisions and scopes please [see this guide](http://projects.spring.io/spring-webflow/).

CAS by default operates on the following core webflow configuration files:

| Flow                | Location
|---------------------|-----------------------------------------------
| `login`             | `src/main/resources/webflow/login-webflow.xml`
| `logout`            | `src/main/resources/webflow/logout-webflow.xml`

The above flow configuration files present a minimal structure for what CAS needs at its core to handle login and logout flows. It is important to note that at runtime many other actions and states are injected into either of these flows dynamically depending on the CAS configuration and presence of feature modules. Also note that each feature module itself may dynamically present other opiniated subflow configuration files that are automagically picked up at runtime.

So in truth, what you see above is not necessarily all of what you may get.

<div class="alert alert-warning"><strong>Live Happily</strong><p>It is best to <strong>AVOID</strong> overlaying/modifying flow configuration files by hand manually. The flow configuration files are not considered public APIs, are not compiled and in most cases are no candidates for backward-compatibility. CAS attempts to automate all webflow changes dynamically where appropriate. Staying away from manual changes will only make your future upgrades easier. Only do so in very advanced cases and be SURE to know what you are doing!</p></div>

## Modifying Webflow

In modest trivial cases, you may be able to simply [overlay and modify](Maven-Overlay-Installation.html) the core flow configuration files to add or override the desired behavior. Again, think very carefully before introducing those changes into your deployment environment. Avoid making ad-hoc changes to the Webflow as much as possible and consider how the change you have in mind might be more suitable as a direct contribution to the CAS project itself so you can just take advantage of its configuration and *NOT* its maintenance.

To learn how to introduce new actions and state into a Spring Webflow, please [see this guide](http://projects.spring.io/spring-webflow/).

<div class="alert alert-info"><strong>Speak Up</strong><p>If you find something that is broken where the webflow auto-configuration strategy fails to deliver as advertised, discuss that with the project community and submit a patch that corrects the bug or adds the desired behavior as a modest enhancement. Avoid one-off changes and make the change where the change belongs.</p></div>

In more advanced cases where you may need to take a deep dive and alter core CAS behavior conditionally, you would need to take advantage of the CAS APIs to deliver changes. Using the CAS APIs directly does present the following advantages at some cost:

- Changes are all scoped to Java (Groovy, Kotlin, Clojure, etc).
- You have the full power of Java to dynamically and conditionally augment the Spring Webflow.
- Your changes are all self-contained.
- Changes are now part of the CAS APIs and they will be compiled. Breaking changes on upgrades, if any, should be noticed immediately at build time.

### Design

Design your dynamic webflow configuration agent that alters the webflow using the following form:

```java
public class SomethingWebflowConfigurer extends AbstractCasWebflowConfigurer {
    @Override
    protected void doInitialize() throws Exception {
        final Flow flow = super.getLoginFlow();
        // Magic happens; Call 'super' to see what you have access to and alter the flow.
    }
}
```

### Register

You will then need to register your newly-designed component into the CAS application runtime:

```java
package org.example.something;

@Configuration("somethingConfiguration")
public class SomethingConfiguration {

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @ConditionalOnMissingBean(name = "somethingWebflowConfigurer")
    @Bean
    public CasWebflowConfigurer somethingWebflowConfigurer() {
        final SomethingWebflowConfigurer w = new SomethingWebflowConfigurer();
        w.setLoginFlowDefinitionRegistry(this.loginFlowDefinitionRegistry);
        w.setFlowBuilderServices(this.flowBuilderServices);
        ...
        return w;
    }
}
```

Configuration classes need to be registered with CAS inside a `src/main/resources/META-INF/spring.factories` file:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.example.something.SomethingConfiguration
```

See [this guide](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html) for more info.

