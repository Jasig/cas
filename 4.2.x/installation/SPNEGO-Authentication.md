---
layout: default
title: CAS - SPNEGO Authentication
---

# SPNEGO Authentication
[SPNEGO](http://en.wikipedia.org/wiki/SPNEGO) is an authentication technology that is primarily used to provide
transparent CAS authentication to browsers running on Windows running under Active Directory domain credentials.
There are three actors involved: the client, the CAS server, and the Active Directory Domain Controller/KDC.

    1. Client sends CAS:               HTTP GET to CAS  for cas protected page
    2. CAS responds:                   HTTP 401 - Access Denied WWW-Authenticate: Negotiate
    3. Client sends ticket request:    Kerberos(KRB_TGS_REQ) Requesting ticket for HTTP/cas.example.com@REALM
    4. Kerberos KDC responds:          Kerberos(KRB_TGS_REP) Granting ticket for HTTP/cas.example.com@REALM
    5. Client sends CAS:               HTTP GET Authorization: Negotiate w/SPNEGO Token
    6. CAS responds:                   HTTP 200 - OK WWW-Authenticate w/SPNEGO response + requested page.

The above interaction occurs only for the first request, when there is no CAS ticket-granting ticket associated with
the user session. Once CAS grants a ticket-granting ticket, the SPNEGO process will not happen again until the CAS
ticket expires.


## SPNEGO Requirements
* Client is logged in to a Windows Active Directory domain.
* Supported browser and JDK.
* CAS is running MIT kerberos against the AD domain controller.


## SPNEGO Components
SPNEGO support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-spnego-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## SPNEGO Configuration

### Create SPN Account
Create an Active Directory account for the Service Principal Name (SPN) and record the username and password.

### Create Keytab File
The keytab file enables a trust link between the CAS server and the Key Distribution Center (KDC); an Active Directory
domain controller serves the role of KDC in this context.
The [`ktpass` tool](http://technet.microsoft.com/en-us/library/cc753771.aspx) is used to generate the keytab file,
which contains a cryptographic key. Be sure to execute the command from an Active Directory domain controller as
administrator.


### Test SPN Account
Install and configure MIT Kerberos V on the CAS server host(s). The following sample `krb5.conf` file may be used
as a reference.

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

Then verify that your are able to read the keytab file:

    klist -k

Then verify that your are able to read the keytab file:

    kinit a_user_in_the_realm@YOUR.REALM.HERE
    klist


### Browser Configuration
* Internet Explorer - Enable _Integrated Windows Authentication_ and add the CAS server URL to the _Local Intranet_
zone.
* Firefox - Set the `network.negotiate-auth.trusted-uris` configuration parameter in `about:config` to the CAS server
URL, e.g. `https://cas.example.com`.

### Webflow Configuration
Replace transition instances of `viewLoginForm` with `startSpnegoAuthenticate`, if any.

### Authentication Configuration

Provide a JAAS `login.conf` file:

    com.sun.security.jgss.krb5.initiate {
       com.sun.security.auth.module.Krb5LoginModule
        required
        storeKey=true
        useKeyTab=true
        keyTab="/home/cas/kerberos/myspnaccount.keytab"
        principal="HTTP/cas.your.realm.here@YOUR.REALM.HERE";
    };
    com.sun.security.jgss.krb5.accept {
       com.sun.security.auth.module.Krb5LoginModule
        required
        storeKey=true
        useKeyTab=true
        keyTab="/home/cas/kerberos/myspnaccount.keytab"
        principal="HTTP/cas.your.realm.here@YOUR.REALM.HERE";
    };

Report your JAAS file to JAVA_OPTS environment variable

    -Djava.security.auth.login.config=/path/to/jaas/file
    

You may use the following configuration in `cas.properties`:

```properties
# cas.spnego.ldap.attribute=spnegoattribute
# cas.spnego.ldap.filter=host={0}
# cas.spnego.ldap.basedn=
# cas.spnego.hostname.pattern=.+
# cas.spnego.ip.pattern=
# cas.spnego.alt.remote.host.attribute
# cas.spengo.use.principal.domain=false
# cas.spnego.ntlm.allowed=true
# cas.spnego.kerb.debug=false
# cas.spnego.kerb.realm=EXAMPLE.COM
# cas.spnego.kerb.kdc=172.10.1.10
# cas.spnego.login.conf.file=/path/to/login
# cas.spnego.jcifs.domain=
# cas.spnego.jcifs.domaincontroller=
# cas.spnego.jcifs.netbios.cache.policy:600
# cas.spnego.jcifs.netbios.wins=
# cas.spnego.jcifs.password=
# cas.spnego.jcifs.service.password=
# cas.spnego.jcifs.socket.timeout:300000
# cas.spnego.jcifs.username=
# cas.spnego.kerb.conf=
# cas.spnego.ntlm=false
# cas.spnego.supportedBrowsers=MSIE,Trident,Firefox,AppleWebKit
# cas.spnego.mixed.mode.authn=false
# cas.spnego.send.401.authn.failure=false
# cas.spnego.principal.resolver.transform=NONE
# cas.spnego.service.principal=HTTP/cas.example.com@EXAMPLE.COM
# cas.spnego.hostname.client.action.strategy=hostnameSpnegoClientAction
```

## Client Selection Strategy
CAS provides a set of components that attempt to activate the SPNEGO flow conditionally,
in case deployers need a configurable way to decide whether SPNEGO should be applied to the
current authentication/browser request. The state that is available to the webflow
is `evaluateClientRequest` which will attempt to start SPNEGO authentication
or resume normally, depending on the client action strategy chosen below.

### By Remote IP
Checks to see if the request's remote ip address matches a predefine pattern.

```properties
...
# cas.spnego.hostname.client.action.strategy=baseSpnegoClientAction
...
```


### By Hostname
Checks to see if the request's remote hostname matches a predefine pattern.

```properties
...
# cas.spnego.hostname.client.action.strategy=hostnameSpnegoClientAction
...
```


### By LDAP Attribute
Checks an LDAP instance for the remote hostname, to locate a pre-defined attribute whose mere existence
would allow the webflow to resume to SPNEGO.

```properties
...
# cas.spnego.hostname.client.action.strategy=ldapSpnegoClientAction
...
```

...and the LDAP configuration:

```xml
<bean id="spnegoCLientActionConnectionFactory" class="..." />

<bean id="spnegoClientActionSearchRequest" class="org.ldaptive.SearchRequest"
      p:baseDn-ref="baseDn"
      p:searchFilter-ref="searchFilter"/>

<bean id="searchFilter" class="org.ldaptive.SearchFilter"
      c:filter="${cas.spnego.ldap.filter:host={0}}" />

<bean id="baseDn" class="java.lang.String">
    <constructor-arg type="java.lang.String" value="${cas.spnego.ldap.basedn:}" />
</bean>
```
