---
layout: default
title: CAS - SPNEGO Authentication
---

# SPNEGO Authentication

[SPNEGO](http://en.wikipedia.org/wiki/SPNEGO) is an authentication technology that is primarily used to provide
transparent CAS authentication to browsers running on Windows running under Active Directory domain credentials.
There are three actors involved: the client, the CAS server, and the Active Directory Domain Controller/KDC.

1. Client sends CAS:               HTTP `GET` to CAS for cas protected page
2. CAS responds:                   HTTP `401` - Access Denied `WWW-Authenticate: Negotiate`
3. Client sends ticket request:    Kerberos(`KRB_TGS_REQ`) Requesting ticket for `HTTP/cas.example.com@REALM`
4. Kerberos KDC responds:          Kerberos(`KRB_TGS_REP`) Granting ticket for `HTTP/cas.example.com@REALM`
5. Client sends CAS:               HTTP `GET` Authorization: Negotiate w/SPNEGO Token
6. CAS responds:                   HTTP `200` - OK `WWW-Authenticate` w/SPNEGO response + requested page.

The above interaction occurs only for the first request, when there is no CAS SSO session.
Once CAS grants a ticket-granting ticket, the SPNEGO process will not happen again until the CAS
ticket expires.

## Requirements

* Client is logged in to a Windows Active Directory domain.
* Supported browser.
* CAS is running MIT kerberos against the AD domain controller.

<div class="alert alert-info"><strong>JCE Requirement</strong><p>It's safe to make sure you have the proper JCE bundle installed in your Java environment that is used by CAS, specially if you need to consume encrypted payloads issued by ADFS. Be sure to pick the right version of the JCE for your Java version. Java versions can be detected via the <code>java -version</code> command.</p></div>

<div class="alert alert-info"><strong>Large Kerberos Tickets</strong><p>If organization users have large kerberos tickets, likely cause by being a member of a large number of groups, the Tomcat connector will need to have the <code>maxHttpHeaderSize</code> value increased from the default amount to allow the ticket to be passed to the CAS Server application.</p></div>
 
## Components

SPNEGO support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-spnego-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

You may also need to declare the following Maven repository in
your CAS overlay to be able to resolve dependencies:

```xml
<repositories>
    ...
    <repository>
        <id>iam-releases</id>
        <url>https://dl.bintray.com/uniconiam/maven</url>
    </repository>
    ...
</repositories>
```

## Configuration

The following steps are required to turn on the SPNEGO functionality.

### Create SPN Account

Create an Active Directory account for the Service Principal Name (SPN) and record the username. Password will be overwritten by the next step.

### Create Keytab File

The keytab file enables a trust link between the CAS server and the Key Distribution Center (KDC); an Active Directory
domain controller serves the role of KDC in this context.
The [`ktpass` tool](http://technet.microsoft.com/en-us/library/cc753771.aspx) is used to generate the keytab file,
which contains a cryptographic key. Be sure to execute the command from an Active Directory domain controller as
administrator (a member of domain administrators will not be able to use `ktpass` successfully).

Example:

```bash
C:\Users\administrator.DOMAIN>ktpass /out myspnaccount.keytab /princ HTTP/cas.example.com@REALM /pass * /mapuser domain-account@YOUR.REALM /ptype KRB5_NT_PRINCIPAL /crypto RC4-HMAC-NT
Targeting domain controller: DC.YOUR.REALM
Successfully mapped HTTP/cas.example.com to domaine-account.
Type the password for HTTP/cas.example.com:
Type the password again to confirm:
Password succesfully set!
Key created.
Output keytab to myspnaccount.keytab:
Keytab version: 0x502
keysize 69 HTTP/cas.example.com@REALM ptype 1 (KRB5_NT_PRINCIPAL) vno 3 etype 0x17 (RC4-HMAC) keylength 16
(0x00112233445566778899aabbccddeeff)
```

Using `ktpass` requires Active Directory admin permissions. If that is not an option, you may be able to use `ktab.exe` from `%JAVA_HOME%\bin\ktab.exe` that 
is provided by the JDK:

```bash
%JAVA_HOME%\bin\ktab.exe -a service_xxx -n 0 -k cas.keytab
``` 

`-k` specifies key tab output file name and `-n 0` specifies the KNVO number if available and found for the user account. This value may match the `msDS-KeyVersionNumber` on the user account.

Also note that the keytab file must be regenerated after password changes, if any.

### Test SPN Account

Install and configure MIT Kerberos V on the CAS server host(s). The following sample `krb5.conf` file may be used
as a reference.

```ini
[logging]
 default = FILE:/var/log/krb5libs.log
 kdc = FILE:/var/log/krb5kdc.log
 admin_server = FILE:/var/log/kadmind.log

[libdefaults]
 ticket_lifetime = 24000
 default_realm = YOUR.REALM.HERE
 default_keytab_name = /home/cas/kerberos/myspnaccount.keytab
 dns_lookup_realm = false
 dns_lookup_kdc = false
 default_tkt_enctypes = rc4-hmac
 default_tgs_enctypes = rc4-hmac

[realms]
 YOUR.REALM.HERE = {
  kdc = your.kdc.your.realm.here:88
 }

[domain_realm]
 .your.realm.here = YOUR.REALM.HERE
 your.realm.here = YOUR.REALM.HERE
```

It is important to note that `myspnaccount.keytab` is declared as default keytab, otherwise CAS may not be able to
find it and will raise an exception similar to

```bash
KrbException: Invalid argument (400) - Cannot find key of appropriate type to decrypt AP REP -RC4 with HMAC`
```

Then verify that your are able to **read** the keytab file:

```bash
klist -k
Keytab name: FILE:/home/cas/kerberos/myspnaccount.keytab
KVNO Principal
---- --------------------------------------------------------------------------
   3 HTTP/cas.example.com@REALM
```

Then verify that your are able to **use** the keytab file:

```bash
kinit -k HTTP/cas.example.com@REALM
klist
Ticket cache: FILE:/tmp/krb5cc_999
Default principal: HTTP/cas.example.com@REALM

Valid starting       Expires              Service principal
12/08/2016 10:52:00  12/08/2016 20:52:00  krbtgt/REALM@REALM
	renew until 12/08/2016 20:52:00
```

### Browser Configuration

* Internet Explorer - Enable `Integrated Windows Authentication` and add the CAS server URL to the `Local Intranet`
zone.
* Firefox - Set the `network.negotiate-auth.trusted-uris` configuration parameter in `about:config` to the CAS server
URL, e.g. `https://cas.example.com`.

### Authentication Configuration

Make sure you have at least specified the JCIFS Service Principal in the CAS configuration.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#spnego-authentication).
To see the relevant list of CAS properties that deal with NTLM authentication,
please [review this guide](Configuration-Properties.html#ntlm-authentication).

You may provide a JAAS `login.conf` file:

```bash
jcifs.spnego.initiate {
   com.sun.security.auth.module.Krb5LoginModule required storeKey=true useKeyTab=true keyTab="/home/cas/kerberos/myspnaccount.keytab";
};
jcifs.spnego.accept {
   com.sun.security.auth.module.Krb5LoginModule required storeKey=true useKeyTab=true keyTab="/home/cas/kerberos/myspnaccount.keytab";
};
```

## Client Selection Strategy

CAS provides a set of components that attempt to activate the SPNEGO flow conditionally,
in case deployers need a configurable way to decide whether SPNEGO should be applied to the
current authentication/browser request. The state that is available to the webflow
is `evaluateClientRequest` which will attempt to start SPNEGO authentication
or resume normally, depending on the client action strategy chosen below.

### By Remote IP

Checks to see if the request's remote ip address matches a predefine pattern.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#spnego-authentication).

### By Hostname

Checks to see if the request's remote hostname matches a predefine pattern.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#spnego-authentication).

### By LDAP Attribute

Checks an LDAP instance for the remote hostname, to locate a pre-defined attribute whose mere existence
would allow the webflow to resume to SPNEGO.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#spnego-authentication).

## Logging

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="jcifs.spnego" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```

## Troubleshooting

- Failure unspecified at GSS-API level

```bash
Caused by: GSSException: Failure unspecified at GSS-API level (Mechanism level: Invalid argument (400) - Cannot find key of appropriate type to decrypt AP REP - AES256 CTS mode with HMAC SHA1-96)
        at sun.security.jgss.krb5.Krb5Context.acceptSecContext(Unknown Source)
        at sun.security.jgss.GSSContextImpl.acceptSecContext(Unknown Source)
        at sun.security.jgss.GSSContextImpl.acceptSecContext(Unknown Source)
        ... 280 more
Caused by: KrbException: Invalid argument (400) - Cannot find key of appropriate type to decrypt 
AP REP - AES256 CTS mode with HMAC SHA1-96
        at sun.security.krb5.KrbApReq.authenticate(Unknown Source)
        at sun.security.krb5.KrbApReq.<init>(Unknown Source)
        at sun.security.jgss.krb5.InitSecContextToken.<init>(Unknown Source)
        ... 283 more
```

It's very likely that you have the wrong path to `.keytab` file. The KVNO in the keytab file must be identical with the KVNO stored in the Active Directory. Active Directory is raising the KVNO with every execution of ktpass, as part of its `msDS-KeyVersionNumber`.

Other possible causes include:

1. The service principal in the in the CAS configuration is not identical with that from the keytab. (param `/princ` from ktpass)
2. There is no key for the `enctype` sent with the ticket by Active Directory. (param `/crypto` from `ktpass` and set in the `krb5.conf/permitted_enctypes+default_tkt_enctypes`).
3. The KVNO from the ticket is different than the KVNO in the keytab (param `/kvno` from `ktpass`).
