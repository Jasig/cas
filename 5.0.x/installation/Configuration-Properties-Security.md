---
layout: default
title: CAS - Securing Configuration Properties
---

# Configuration Security

This document describes how to retrieve and secure CAS configuration and properties. 

## Spring Cloud

Securing CAS settings and decrypting them is entirely handled by the [Spring Cloud](https://github.com/spring-cloud/spring-cloud-config) project.

The CAS configuration server exposes `/encrypt` and `/decrypt` endpoints to support encrypting and decrypting values.
Both endpoints accept a `POST` payload; you can use `/encrypt` to secure and encrypt settings and place them inside your CAS configuration. 
CAS will auto-decrypt at the appropriate moment.

To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html).

<div class="alert alert-warning"><strong>JCE Requirements</strong><p>to use the encryption and decryption 
features you need the full-strength "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files" 
installed in your JVM version (if it’s not there by default).</p></div>

To encrypt a given setting, use:

```bash
curl https://sso.example.org/cas/status/configserver/encrypt -d sensitiveValue
```

Then, copy the encrypted setting into your CAS configuration using the method specified below.

<div class="alert alert-warning"><strong>URL Encoding</strong><p>Be careful with <code>curl</code>.
You may have to use <code>--data-urlencode</code> or set an explicit <code>Content-Type: text/plain</code>
to account for special characters such as <code>+</code>.</p></div>

If you wish to manually encrypt and decrypt settings to ensure the functionality is sane, use:

```bash
export ENCRYPTED=`curl https://sso.example.org/cas/status/configserver/encrypt -d sensitiveValue | python -c 'import sys,urllib;print urllib.quote(sys.stdin.read().strip())'`
echo $ENCRYPTED
curl https://sso.exampple.org/cas/status/configserver/decrypt -d $ENCRYPTED | python -c 'import sys,urllib;print urllib.quote(sys.stdin.read().strip())'
```

Properties that are prefixed with `{cipher}` are automatically decrypted by the CAS configuration server at runtime, such as:

```yml
cas
    something
        sensitive: '{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ'
```

Or:

```properties
# Note that there are no quotes around the value!
cas.something.sensitive={cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ
```

## Vault

You can also store sensitive settings inside [Vault](https://www.vaultproject.io/). 
Vault can store your existing secrets, or it can dynamically generate new secrets 
to control access to third-party resources or provide time-limited credentials for your infrastructure. 
To lean more about Vault and its installation process, please visit the project website.

<div class="alert alert-warning"><strong>Tread Lightly!</strong><p>Note that this module is <strong>EXPERIMENTAL</strong>.</p></div>

Once vault is accessible and configured inside CAS, support is provided via the following dependency:
                                                    
```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-core-configuration-vault</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html).

With CAS, secrets are picked up at startup of the application server. CAS uses the data and settings
from the application name (i.e. `cas`) and active profiles to determine contexts paths in 
which secrets should be stored and later fetched.

These context paths typically are:

```bash
/secret/{application}/{profile}
/secret/{application}
```

As an example, you may write the following CAS setting to Vault:

```bash
vault write secret/cas/native <setting-name>=<value>
```

CAS will execute the equivalent of the following command to read settings later when needed:

```bash
vault read secret/cas/native
```

All settings and secrets that are stored inside Vault may be reloaded at any given time. 
To lean more about CAS allows you to reload configuration changes, please [review this guide](Configuration-Management-Reload.html).
To lean more about how configuration is managed and profiled by CAS, please [review this guide](Configuration-Management.html).

### Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
<AsyncLogger name="org.springframework.cloud.vault" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
```

