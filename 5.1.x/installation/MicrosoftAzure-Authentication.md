---
layout: default
title: CAS - Microsoft Azure Authentication
---

# Microsoft Azure Authentication

Azure Multi-Factor Authentication (MFA) is Microsoft's two-step verification solution. Azure MFA helps safeguard access to data and applications while meeting user demand for a simple sign-in process. It delivers strong authentication via a range of verification methods, including phone call, text message, etc.

To learn more about Microsoft Azure and its multifactor authentication features, [refer to Microsoft's documentation](https://docs.microsoft.com/en-us/azure/multi-factor-authentication/multi-factor-authentication).

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-azure</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The functionality of this feature depends on the availability of a phone number that is resolved as a pre-defined
attribute for the CAS principal. Also note that only a limited number of authentication modes are available to assist with verification
of credentials via Microsoft Azure. Such modes are activated via the CAS settings.

<div class="alert alert-warning"><strong>Secure Certificates</strong><p>Your Microsoft Azure subscription will provide you with a license and a client certificate. The client certificate is a unique private certificate that was generated especially for you. <strong>Do not share or lose this file</strong>. It’s your key to ensuring the security of your communications with the Azure multifactor authentication service.</p></div>

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#microsoft-azure).
