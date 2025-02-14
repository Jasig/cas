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

<div class="alert alert-warning"><strong>Remember</strong><p>SAML2 service provider integrations listed here simply attempt to automate CAS configuration based on known and documented integration guidelines and recipes provided by the service provider owned by the vendor. These recipes can change and break CAS over time and needless to say, they need to be properly and thoroughly tested as the project itself does not have a subscription to each application to test for correctness. YMMV. If you find an issue with an automated integration strategy here, please <strong>speak up</strong>.</p></div>

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-saml-sp-integrations</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The following SAML SP integrations, as samples, are provided by CAS:

<div class="img-cloud">
<a href="http://www.incommon.org/federation/metadata.html"><img src="https://cloud.githubusercontent.com/assets/1205228/22052578/47d6f570-dd60-11e6-8f03-02cb99d6106d.gif" height="48" width="140"></a> 

<a href="https://support.zoom.us/hc/en-us/articles/201363003-Getting-Started-with-SSO"><img src="https://cloud.githubusercontent.com/assets/1205228/22052568/475f36d4-dd60-11e6-84f4-a88eb33f3f0a.jpg" height="30" width="130"></a> 

<a href="https://www.dropbox.com/guide/admin/security/configure-single-sign-on"><img src="https://cloud.githubusercontent.com/assets/1205228/22052575/47ba8930-dd60-11e6-8cb1-9334066d5f0f.png" height="48" width="140"></a> 

<a href="https://blog.samanage.com/company/saml-single-sign-on-support-samanage/"><img src="https://cloud.githubusercontent.com/assets/1205228/22052581/47e7bcfc-dd60-11e6-85e9-c09926736e5e.png" height="48" width="140"></a> 

<a href="https://help.salesforce.com/HTViewHelpDoc?id=sso_saml.htm"><img src="https://cloud.githubusercontent.com/assets/1205228/22052579/47da9d74-dd60-11e6-8eac-d66b67e2ebf7.png" height="60" width="140"></a>

<a href="https://community.box.com/t5/For-Admins/Single-Sign-On-SSO-with-Box-For-Administrators/ta-p/1263"><img src="https://cloud.githubusercontent.com/assets/1205228/22052572/47ace302-dd60-11e6-9842-4eda5a9ab5cf.png" height="48" width="140"></a> 

<a href="http://wiki.servicenow.com/index.php?title=SAML_2.0_Web_Browser_SSO_Profile"><img src="https://cloud.githubusercontent.com/assets/1205228/22052583/4805e2c2-dd60-11e6-8150-80aaa4bbab0e.png" height="20" width="140"></a> 

<a href="http://www.workday.com/"><img src="https://cloud.githubusercontent.com/assets/1205228/22052587/4816e04a-dd60-11e6-9ceb-6ccec1290e19.png" height="48" width="140"></a> 

<a href="http://www.testshib.org"><img src="https://cloud.githubusercontent.com/assets/1205228/22052582/4805064a-dd60-11e6-87aa-746deacca597.jpg" height="48" width="140"></a> 

<a href="https://help.webex.com/docs/DOC-1067"><img src="https://cloud.githubusercontent.com/assets/1205228/22052574/47af8206-dd60-11e6-95d0-5827e0d88d73.jpg" height="48" width="140"></a>

<a href="https://www.collegeboard.org/powerfaids/net-partner"><img src="https://cloud.githubusercontent.com/assets/1205228/22052570/47abea6a-dd60-11e6-85a0-387c3c2ce8e7.png" height="48" width="140"></a> 

<a href="https://msdn.microsoft.com/en-us/library/azure/dn641269.aspx"><img src="https://cloud.githubusercontent.com/assets/1205228/22053102/d64da904-dd63-11e6-8a68-977526634b9d.png" height="48" width="140"></a> 

<a href="https://asana.com/guide/help/premium/authentication#gl-saml"><img src="https://cloud.githubusercontent.com/assets/1205228/22052569/478b8c16-dd60-11e6-82f4-e292243ff076.png" height="48" width="140"></a> 

<a href="https://onlinehelp.tableau.com/current/server/en-us/saml_requ.htm"><img src="https://cloud.githubusercontent.com/assets/1205228/22052586/480cffc6-dd60-11e6-939c-1ceb34f3186d.png" height="48" width="140"></a> 

<a href="https://help.evernote.com/hc/en-us/articles/209005217-How-to-configure-SSO-for-your-business"><img src="https://cloud.githubusercontent.com/assets/1205228/22052577/47d6b6e6-dd60-11e6-810e-dd875bf25d17.png" height="48" width="140"></a> 

<a href="http://www.ellucian.com/Software/Colleague-WebAdvisor/"><img src="https://cloud.githubusercontent.com/assets/1205228/23185912/5c3f2f50-f89a-11e6-8450-6da44a1a9d9d.png" height="48" width="140"></a>

<a href="https://docs.openathens.net/display/public/MD/SAML+interoperability+requirements/"><img src="https://cloud.githubusercontent.com/assets/1205228/24070833/ffb5f63a-0bd9-11e7-8bda-28301c37188b.png" height="48" width="140"></a>

<a href="http://server.arcgis.com/en/portal/latest/administer/linux/configuring-a-saml-compliant-identity-provider-with-your-portal.htm"><img src="https://cloud.githubusercontent.com/assets/1205228/24108414/c3851e14-0da2-11e7-97d7-086a93d6873d.png" height="48" width="130"></a>

<a href="https://helpx.adobe.com/enterprise/kb/configure_shibboleth_idp_for_use_with_Adobe_SSO.html"><img src="https://cloud.githubusercontent.com/assets/1205228/24562072/ac5964ec-165e-11e7-9986-92108c30eb9b.png" height="48" width="90"></a>

<a href="https://www.academicworks.com/why-academicworks/user-authentication/">
<img src="https://cloud.githubusercontent.com/assets/1205228/24624808/3c5909b6-18c2-11e7-9922-52ee604aff55.png" height="48" width="240"></a>

<a href="https://www.infinitecampus.com/">
<img src="https://cloud.githubusercontent.com/assets/1205228/24698286/4c4d8740-1a05-11e7-844e-54d328e64e2f.png" height="48" width="120"></a>

<a href="http://kb.securingthehuman.org/Other-Resources/55708668/Single-Sign-On-Technical-Overview-Guide.htm">
<img src="https://cloud.githubusercontent.com/assets/1205228/24699366/fbfbcf5a-1a08-11e7-9664-f37d6e50a5a3.png" height="48" width="120"></a>

<a href="https://get.slack.help/hc/en-us/articles/205168057">
<img src="https://cloud.githubusercontent.com/assets/1205228/24858382/5b15c512-1e01-11e7-87ac-f0b091cd7885.png" height="48" width="120"></a>

<a href="https://support.zendesk.com/hc/en-us/articles/203663676-Using-SAML-for-single-sign-on-Professional-and-Enterprise-">
<img src="https://cloud.githubusercontent.com/assets/1205228/24858342/34441006-1e01-11e7-9209-9b78081de4db.png" height="48" width="120"></a>

<a href="https://www.gartner.com/">
<img src="https://cloud.githubusercontent.com/assets/1205228/25349422/a29a98f6-28d6-11e7-9d10-e286d0080cbe.png" height="48" width="120"></a>

<a href="https://www.cherwell.com/">
<img src="https://user-images.githubusercontent.com/1205228/30205883-84174ebc-949f-11e7-9afc-a66c2ab19f59.png" height="48" width="120"></a>

<a href="https://www.bynder.com">
<img src="https://user-images.githubusercontent.com/1205228/30205852-69921a5e-949f-11e7-8326-ba4c00fceba4.png" height="48" width="120"></a>

<a href="https://www.everbridge.com/">
<img src="https://user-images.githubusercontent.com/1205228/30205910-a560ec90-949f-11e7-8485-e3a833f8109b.png" height="48" width="120"></a>

<a href="https://sserca.fau.edu/">
<img src="https://user-images.githubusercontent.com/1205228/30221936-5e90af04-94da-11e7-8046-483fc26a1c01.png" height="48" width="220"></a>

<a href="https://newrelic.com/new">
<img src="https://user-images.githubusercontent.com/1205228/30247067-541cef96-9620-11e7-88d7-c3749ba55ecf.png" height="48" width="90"></a>

<a href="https://www.egnyte.com/">
<img src="https://user-images.githubusercontent.com/1205228/30247063-218f3962-9620-11e7-9ba4-54f7112fee13.png" height="48" width="120"></a>

<a href="https://www.yuja.com/">
<img src="https://user-images.githubusercontent.com/1205228/30271142-6dd0b58c-9704-11e7-9138-0b86d5799403.png" height="48" width="100"></a>

<a href="https://www.symplicity.com/">
<img src="https://user-images.githubusercontent.com/1205228/30271318-192b48fc-9705-11e7-9c18-3be401a39e84.png" height="48" width="200"></a>

<a href="Google-Apps-Integration.html">
<img src="https://cloud.githubusercontent.com/assets/1205228/25385497/18e09c84-2979-11e7-94c1-5ad430b3d768.png" height="48" width="120"></a>

<a href="http://www.accruent.com/">
<img src="https://user-images.githubusercontent.com/1205228/30735450-f9a12792-9f8b-11e7-941d-7ab4ac7628b9.png" height="48" width="120"></a>

</div>

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#saml-sps).

<div class="alert alert-info"><strong>Configure Once, Run Everywhere</strong><p>If you have developed a recipe for integrating
with a SAML service provider, consider contributing that recipe to the project so its configuration
can be automated once and for all to use. Let the change become a feature of the project, rather than something you alone have to maintain.</p></div>

## Google Apps

The Google Apps SAML integration is also provided by CAS natively [based on this guide](Google-Apps-Integration.html).
