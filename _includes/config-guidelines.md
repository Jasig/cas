<!-- fragment:keep -->

<p/>

#### Configuration Metadata

The collection of configuration properties listed in this section are automatically generated from the CAS source and components that contain the actual field
definitions, types, descriptions, modules, etc. This metadata may not always be 100% accurate, or could be lacking details and sufficient explanations.

#### Be Selective

This section is meant as a guide only. Do NOT copy/paste the entire collection of settings into your CAS configuration; rather pick only the properties that you
need. Do NOT enable settings unless you are certain of their purpose and do NOT copy settings into your configuration only to keep them as reference. All these
ideas lead to upgrade headaches, maintenance nightmares and premature aging.

#### YAGNI

Note that for nearly ALL use cases, declaring and configuring properties listed here is sufficient. You should NOT have to explicitly massage a CAS XML/Java/etc
configuration file to design an authentication handler, create attribute release policies, etc. CAS at runtime will auto-configure all required changes for you.
If you are unsure about the meaning of a given CAS setting, do NOT turn it on without hesitation. Review the codebase or better yet, ask questions to clarify
the intended behavior.

#### Naming Convention

Property names can be specified in very relaxed terms. For instance `cas.someProperty`, `cas.some-property`, `cas.some_property` are all valid names. While all
forms are accepted by CAS, there are certain components (in CAS and other frameworks used) whose activation at runtime is conditional on a property value, where
this property is required to have been specified in CAS configuration using kebab case. This is both true for properties that are owned by CAS as well as those
that might be presented to the system via an external library or framework such as Spring Boot, etc.

<div class="alert alert-info">:information_source: <strong>Note</strong> <p>
When possible, properties should be stored in lower-case kebab format, such as <code>cas.property-name=value</code>.
The only possible exception to this rule is when naming actuator endpoints; The name of the
actuator endpoints (i.e. <code>ssoSessions</code>) <strong>MUST</strong> remain in camelCase mode. 
</p></div>

Settings and properties that are controlled by the CAS platform directly always begin with the prefix `cas`. All other settings are controlled and provided
to CAS via other underlying frameworks and may have their own schemas and syntax. BE CAREFUL with
the distinction. Unrecognized properties are rejected by CAS and/or frameworks upon which CAS depends. This means if you somehow misspell a property definition
or fail to adhere to the dot-notation syntax and such, your setting is entirely refused by CAS and likely the feature it controls will never be activated in the
way you intend.

#### Validation

Configuration properties are automatically validated on CAS startup to report issues with configuration binding, specially if defined CAS settings cannot be
recognized or validated by the configuration schema. Additional validation processes are also handled
via <a href="{{basePath}}/{{version}}/configuration/Configuration-Metadata-Repository.html">Configuration Metadata</a> and property migrations applied automatically on
startup by Spring Boot and family.

#### Indexed Settings

CAS settings able to accept multiple values are typically documented with an index, such as `cas.some.setting[0]=value`. The index `[0]` is meant to be
incremented by the adopter to allow for distinct multiple configuration blocks.
