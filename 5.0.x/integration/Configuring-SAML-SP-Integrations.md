---
layout: default
title: CAS - SAML SP Integrations
---

# SAML SP Integrations

CAS provides built-in integration support for a number of SAML2 service providers. Configuring these service providers
is simply about declaring the relevant properties in the CAS configuration as well as the configuration module below. Each integration,
when configured appropriately, will register the service provider with the CAS service registry as a SAML SP and will follow
a recipe (that is documented by the SP publicly) to configure attribute release policies, name ids and entity IDs. If you need to,
you can review the registration record inside the CAS service registry to adjust options.

**NOTE:** In the event that special attributes and/or name ids are required for the integration, you are required
to ensure all such [attributes are properly resolved](Attribute-Resolution.html) and are available to the CAS principal. 

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-saml-sp-integrations</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The following SAML SP integrations are provided by CAS:

* [InCommon](http://www.incommon.org/federation/metadata.html)
* [Dropbox](https://www.dropbox.com/guide/admin/security/configure-single-sign-on)
* [SAManage](https://blog.samanage.com/company/saml-single-sign-on-support-samanage/)
* [Salesforce](https://help.salesforce.com/HTViewHelpDoc?id=sso_saml.htm)
* [Box](https://community.box.com/t5/For-Admins/Single-Sign-On-SSO-with-Box-For-Administrators/ta-p/1263)
* [ServiceNow](http://wiki.servicenow.com/index.php?title=SAML_2.0_Web_Browser_SSO_Profile)
* [Workday](http://www.workday.com/)
* [TestShib](http://www.testshib.org)
* [Webex](https://help.webex.com/docs/DOC-1067)
* [PowerFAIDS Net Partner](https://www.collegeboard.org/powerfaids/net-partner)
* [Office365](https://msdn.microsoft.com/en-us/library/azure/dn641269.aspx)

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

<div class="alert alert-info"><strong>Configure Once, Run Everywhere</strong><p>If you have developed a recipe for integrating
with a SAML service provider, consider contributing that recipe to the project so its configuration
can be automated once and for all to use. Let the change become a feature of the project, rather than something you alone have to maintain.</p></div>

## Google Apps

The Google Apps SAML integration is also provided by CAS natively [based on this guide](Google-Apps-Integration.html).
