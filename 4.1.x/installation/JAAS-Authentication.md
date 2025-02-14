---
layout: default
title: CAS - JAAS Authentication
---

# JAAS Authentication
[JAAS](http://docs.oracle.com/javase/6/docs/technotes/guides/security/jaas/JAASRefGuide.html) is a Java standard
authentication and authorization API. JAAS is configured via externalized plain text configuration file.
Using JAAS with CAS allows modification of the authentication process without having to rebuild and redeploy CAS
and allows for PAM-style multi-module "stacked" authentication.


## JAAS Components
JAAS components are provided in the CAS core module and require no additional dependencies to use.


###### `JaasAuthenticationHandler`
The JAAS handler delegates to the built-in JAAS subsystem to perform authentication according to the
directives in the JAAS config file. The handler only exposes a single configuration property:

* `realm` - JAAS realm name. (Defaults to _CAS_.)

The following configuration excerpt demonstrates how to configure the JAAS handler in `deployerConfigContext.xml`:

{% highlight xml %}
<bean class="org.jasig.cas.authentication.handler.support.JaasAuthenticationHandler"
      p:realm="CustomCasRealm" />
{% endhighlight %}


## JAAS Configuration File
The default JAAS configuration file is located at `$JRE_HOME/lib/security/java.security`. It's important to note
that JAAS configuration applies to the entire JVM. The path to the JAAS configuration file in effect may be altered
by setting the `java.security.auth.login.config` system property to an alternate file path.
A sample JAAS configuration file is provided for reference.

    /**
      * Login Configuration for JAAS. First try Kerberos, then LDAP, then AD
      * Note that a valid krb5.conf must be supplied to the JVM for Kerberos auth
      * -Djava.security.krb5.conf=/etc/krb5.conf
      */
    CAS {
      com.ibm.security.auth.module.Krb5LoginModule sufficient
        debug=FALSE;
        edu.uconn.netid.jaas.LDAPLoginModule sufficient
        java.naming.provider.url="ldap://ldap.my.org:389/dc=my,dc=org"
        java.naming.security.principal="uid=cas,dc=my,dc=org"
        java.naming.security.credentials="password"
        Attribute="uid"
        startTLS="true";
      edu.uconn.netid.jaas.LDAPLoginModule sufficient
        java.naming.provider.url="ldaps://ad.my.org:636/dc=ad,dc=my,dc=org"
        java.naming.security.principal="cas@ad.my.org"
        java.naming.security.credentials="password"
        Attribute="sAMAccountName";
    };
