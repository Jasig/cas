---
layout: default
title: CAS - Custom Multifactor Authentication
---

# Custom Multifactor Authentication

To create your own custom multifactor authentication provider, you will need to design components that primarily register a customized authentication flow into the CAS webflow engine under a unique identifier. Later on, you will also need to consider strategies by which your custom multifactor authentication provider [can be triggered](Configuring-Multifactor-Authentication-Triggers.html).

## Provider ID

Each multifactor provider is assigned a unique identifier that is typically mapped or made equal to the underlying webflow. The unique identifier can be any arbitrary string of your choosing, provided it's kept distinct and sensible as it, depending on use case, may be used in other systems and by other applications to act as a trigger.

For the purposes of this guide, let's choose `mfa-custom` as our provider id.

## Webflow XML Configuration

The flow configuration file needs to be placed inside a `src/main/resources/webflow/mfa-custom` directory, named as `mfa-custom.xml` whose outline is sampled below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<flow xmlns="http://www.springframework.org/schema/webflow"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/webflow http://www.springframework.org/schema/webflow/spring-webflow.xsd">

    <var name="credential" class="org.example.CustomCredential" />
    <on-start>
        <evaluate expression="initialFlowSetupAction" />
    </on-start>

    <action-state id="initializeLoginForm">
        <evaluate expression="initializeLoginAction" />
        <transition on="success" to="viewLoginForm"/>
    </action-state>

    <view-state id="viewLoginForm" view="..." model="credential">
        <binder>
            ...
        </binder>
        <on-entry>
            <set name="viewScope.principal" value="conversationScope.authentication.principal" />
        </on-entry>
        <transition on="submit" bind="true" validate="true" to="realSubmit"/>
    </view-state>

    <action-state id="realSubmit">
        <evaluate expression="finalAuthenticationWebflowAction" />
        <transition on="success" to="success" />
        <transition on="error" to="initializeLoginForm" />
    </action-state>

    <end-state id="success" />
</flow>
```

## Register Webflow Configuration

The custom provider itself is its own standalone webflow that is then registered with the primary authentication flow.

```java
public class CustomAuthenticatorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {
    public static final String MFA_EVENT_ID = "mfa-custom";
    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public CustomAuthenticatorWebflowConfigurer(FlowBuilderServices flowBuilderServices,
                                                FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                FlowDefinitionRegistry flowDefinitionRegistry) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, flowDefinitionRegistry);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
    }

    @Override
    protected void doInitialize() throws Exception {
        registerMultifactorProviderAuthenticationWebflow(getLoginFlow(),
                MFA_EVENT_ID, this.flowDefinitionRegistry);
    }
}
```

## Design Provider

Multifactor authentication providers in CAS are represented in forms of `MultifactorAuthenticationProvider` instances.
The outline of the provider is briefly displayed below and much of its behavior is removed in favor of defaults.

```java
public class CustomMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {
    private static final long serialVersionUID = 4789727148634156909L;
}
```

## Register Provider

The custom webflow configuration needs to be registered with CAS. The outline of the configuration registration is sampled and summarized below:

```java
package org.example.cas;

@Configuration("CustomAuthenticatorSubsystemConfiguration")
public class CustomAuthenticatorSubsystemConfiguration {
    ...
    @Bean
    public FlowDefinitionRegistry customFlowRegistry() {
        final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.setBasePath("classpath*:/webflow");
        builder.addFlowLocationPattern("/mfa-custom/*-webflow.xml");
        return builder.build();
    }

    @Bean
    public MultifactorAuthenticationProvider customAuthenticationProvider() {
        final CustomMultifactorAuthenticationProvider p = new CustomMultifactorAuthenticationProvider();
        p.setId("mfa-custom");
        return p;
    }

    @Bean
    public CasWebflowConfigurer customWebflowConfigurer() {
        return new CustomAuthenticatorWebflowConfigurer(
                flowBuilderServices,
                loginFlowDefinitionRegistry,
                customFlowRegistry());
    }
    ...
}
```

Do not forget to register the configuration class with CAS. [See this guide](Configuration-Management-Extensions.html) for better details.

## Triggers

The custom authentication webflow can be triggered using any of the [supported options](Configuring-Multifactor-Authentication-Triggers.html)
