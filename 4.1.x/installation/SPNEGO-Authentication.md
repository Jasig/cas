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

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-spnego</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}


###### `JCIFSSpnegoAuthenticationHandler`
The authentication handler that provides SPNEGO support in both Kerberos and NTLM flavors. NTLM is disabled by default.
Configuration properties:

* `principalWithDomainName` - True to include the domain name in the CAS principal ID, false otherwise.
* `NTLMallowed` - True to enable NTLM support, false otherwise. (Disabled by default.)


###### `JCIFSConfig`
Configuration helper for JCIFS and the Spring framework. Configuration properties:

* `jcifsServicePrincipal` - service principal name.
* `jcifsServicePassword` - service principal password.
* `kerberosDebug` - True to enable kerberos debugging, false otherwise.
* `kerberosRealm` - Kerberos realm name.
* `kerberosKdc` - Kerberos KDC address.
* `loginConf` - Path to the login.conf JAAS configuration file.



###### `SpnegoNegociateCredentialsAction`
CAS login Webflow action that begins the SPNEGO authenticaiton process. The action checks the `Authorization` request
header for a suitable value (`Negotiate` for Kerberos or `NTLM`). If the check is successful, flow continues to the
`SpnegoCredentialsAction` state; otherwise a 401 (not authorized) response is returned.


###### `SpnegoCredentialsAction`
Constructs CAS credentials from the encoded GSSAPI data in the `Authorization` request header. The standard CAS
authentication process proceeds as usual after this step: authentication is attempted with a suitable handler,
`JCIFSSpnegoAuthenticationHandler` in this case. The action also sets response headers accordingly based on whether
authentication succeeded or failed.


## SPNEGO Configuration


### Create SPN Account
Create an Active Directory account for the Service Principal Name (SPN) and record the username and password, which
will be used subsequently to configure the `JCIFSConfig` component.


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
URL, e.g. https://cas.example.com.


### CAS Component Configuration
Define two new action states in `login-webflow.xml` before the `viewLoginForm` state:

{% highlight xml %}
<action-state id="startAuthenticate">
  <evaluate expression="negociateSpnego" />
  <transition on="success" to="spnego" />
</action-state>
 
<action-state id="spnego">
  <evaluate expression="spnego" />
  <transition on="success" to="sendTicketGrantingTicket" />
  <transition on="error" to="viewLoginForm" />
</action-state>
{% endhighlight %}

Additionally, find action `generateLoginTicket` - replace `viewLoginForm` with `startAuthenticate`.

Add two bean definitions in `cas-servlet.xml`:

{% highlight xml %}
<bean id="negociateSpnego" class="org.jasig.cas.support.spnego.web.flow.SpnegoNegociateCredentialsAction" />
 
<bean id="spnego" class="org.jasig.cas.support.spnego.web.flow.SpnegoCredentialsAction"
      p:centralAuthenticationService-ref="centralAuthenticationService" />
{% endhighlight %}

Update `deployerConfigContext.xml` according to the following template:

{% highlight xml %}
<bean id="jcifsConfig"
      class="org.jasig.cas.support.spnego.authentication.handler.support.JCIFSConfig"
      p:jcifsServicePrincipal="HTTP/cas.example.com@EXAMPLE.COM"
      p:kerberosDebug="false"
      p:kerberosRealm="EXAMPLE.COM"
      p:kerberosKdc="172.10.1.10"
      p:loginConf="/path/to/login.conf" />

<bean id="spnegoAuthentication" class="jcifs.spnego.Authentication" />

<bean id="spnegoHandler"
      class="org.jasig.cas.support.spnego.authentication.handler.support.JCIFSSpnegoAuthenticationHandler"
      p:authentication-ref="spnegoAuthentication"
      p:principalWithDomainName="false"
      p:NTLMallowed="true" />

<bean id="spnegoPrincipalResolver"
      class="org.jasig.cas.support.spnego.authentication.principal.SpnegoPrincipalResolver" />

<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
  <constructor-arg>
    <map>
      <entry key-ref="spnegoHandler" value-ref="spnegoPrincipalResolver"/>
    </map>
  </constructor-arg>
  <property name="authenticationMetaDataPopulators">
    <list>
      <bean class="org.jasig.cas.authentication.SuccessfulHandlerMetaDataPopulator" />
    </list>
  </property>
</bean>
{% endhighlight %}

Provide a JAAS `login.conf` file in a location that agrees with the `loginConf` property of the `JCIFSConfig` bean
above.

    jcifs.spnego.initiate {
       com.sun.security.auth.module.Krb5LoginModule required storeKey=true useKeyTab=true keyTab="/home/cas/kerberos/myspnaccount.keytab";
    };
    jcifs.spnego.accept {
       com.sun.security.auth.module.Krb5LoginModule required storeKey=true useKeyTab=true keyTab="/home/cas/kerberos/myspnaccount.keytab";
    };

## Client Selection Strategy
CAS provides a set of components that attempt to activate the SPNEGO flow conditionally, in case deployers need a configurable way to decide whether SPNEGO should be applied to the current authentication/browser request. The components provided are webflow actions that return either `yes` or `no` to the webflow and allow you to reroute the webflow conditionally based the outcome, to either SPNEGO or the normal CAS login flow. 

The activation strategies are as follows:

### By Remote IP
Checks to see if the request's remote ip address matches a predefine pattern.

{% highlight xml %}
<bean id="baseSpnegoClientAction" 
      class="org.jasig.cas.support.spnego.web.flow.client.BaseSpnegoKnownClientSystemsFilterAction"
      c:ipsToCheckPattern="127.+"
      c:alternativeRemoteHostAttribute="alternateRemoteHeader" />
{% endhighlight %}

### By Hostname
Checks to see if the request's remote hostname matches a predefine pattern. This action supports all functionality provided by `BaseSpnegoKnownClientSystemsFilterAction`. 

{% highlight xml %}
<bean id="hostnameSpnegoClientAction" 
      class="org.jasig.cas.support.spnego.web.flow.client.HostNameSpnegoKnownClientSystemsFilterAction"
      c:hostNamePatternString="something.+" />
{% endhighlight %}

### By LDAP Attribute
Checks an LDAP instance for the remote hostname, to locate a pre-defined attribute whose mere existence would allow the webflow to resume to SPNEGO. This action supports all functionality provided by `BaseSpnegoKnownClientSystemsFilterAction`. 


{% highlight xml %}
<bean id="ldapSpnegoClientAction" 
      class="org.jasig.cas.support.spnego.web.flow.client.LdapSpnegoKnownClientSystemsFilterAction"
      c:connectionFactory-ref="connectionFactory"
      c:searchRequest-ref="searchRequest"
      c:spnegoAttributeName="spnegoAttribute" />
{% endhighlight %}

Sample search request and filer:

{% highlight xml %}
<bean id="searchRequest" class="org.ldaptive.SearchRequest"
      p:baseDn-ref="baseDn"
      p:searchFilter-ref="searchFilter"/>

<bean id="searchFilter" class="org.ldaptive.SearchFilter"
      c:filter="host={0}" />

<bean id="baseDn" class="java.lang.String">
    <constructor-arg type="java.lang.String" value="${ldap.baseDn}" />
</bean>
{% endhighlight %}

### Webflow Configuration

Insert the appropriate action before SPNEGO initiation, assigning a `yes` response route to SPNEGO, and a `no` response to route to viewing the login form.

{% highlight xml %}
<action-state id="eveluateClientRequest">
  <evaluate expression="hostnameSpnegoClientAction" />
  <transition on="yes" to="startAuthenticate" />
  <transition on="no" to="generateLoginTicket" />
</action-state>

...
{% endhighlight %}
