---
layout: default
title: CAS - Microsoft Azure Authentication
---

# Microsoft Azure Authentication

Azure Multi-Factor Authentication (MFA) is Microsoft's two-step verification solution. Azure MFA helps safeguard access to data and applications while meeting user demand for a simple sign-in process. It delivers strong authentication via a range of verification methods, including phone call, text message, etc.

To learn more about Microsoft Azure and its multifactor authentication features, [refer to Microsoft's documentation](https://docs.microsoft.com/en-us/azure/multi-factor-authentication/multi-factor-authentication) and the [SDK documentation](https://docs.microsoft.com/en-us/azure/multi-factor-authentication/multi-factor-authentication-sdk).

<div class="alert alert-warning"><strong>Important</strong><p>The deprecation of the Azure Multi-Factor Authentication Software Development Kit (SDK) has been announced. This feature is no longer supported for new customers. Current customers can continue using the SDK until November 14, 2018. After that time, calls to the SDK will fail.</p></div>

Support is enabled by including the following module in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-azure</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The functionality of this feature depends on the availability of a phone number that is resolved as a pre-defined
attribute for the CAS principal. Also note that only a limited number of authentication modes are available to assist with verification of credentials via Microsoft Azure. Such modes are activated via the CAS settings.

<div class="alert alert-warning"><strong>Secure Certificates</strong><p>Your Microsoft Azure subscription will provide you with a license and a client certificate. The client certificate is a unique private certificate that was generated especially for you. <strong>Do not share or lose this file</strong>. It’s your key to ensuring the security of your communications with the Azure multifactor authentication service.</p></div>

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#microsoft-azure).

## Azure SDK

Note that Azure SDK artifacts are not published to a Maven repository. This means that you will need to download the necessary JAR files and include the in your build configuration. The SDK may be downloaded from [the CAS codebase](https://github.com/apereo/cas/blob/master/support/cas-server-support-azure/lib/PhoneFactorSDK-2.21.jar). Then, assuming the SDK is placed inside a `lib` directory of the [WAR overlay](Maven-Overlay-Installation.html) directory, it can be referenced in the Maven build configuration as such:

```xml
...
<dependencies>
    <dependency>
      <groupId>net.phonefactor.pfsdk</groupId>
      <artifactId>pfsdk</artifactId>
      <version>2.21</version>
      <scope>system</scope>
      <systemPath>${basedir}/lib/PhoneFactorSDK-2.21.jar</systemPath>
    </dependency>
</dependencies>
...
```

For Gradle, you would do something similar:

```gradle
compile files("${projectDir}/lib/PhoneFactorSDK-2.21.jar")
```
