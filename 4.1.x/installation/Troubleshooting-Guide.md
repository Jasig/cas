---
layout: default
title: CAS - Troubleshooting Guide
---

# Troubleshooting Guide

## Authentication

### Login Form Clearing Credentials on Submission
You may encounter an issue where upon submission of credentials on the login form, the screen clears the input data and asks the user again to repopulate the form. The CAS Server log may also indicate the received login ticket is invalid and therefore unable to accept the authentication request.

The CAS server itself initiates a web session upon authentication requests that by default is configured to last for 5 minutes. The state of the session is managed at server-side and thus, once the session expires any authentication activity on the client side such as submission of the login form is disregarded until a new session a regenerated. Another possible cause may be related to a clustered CAS environment where CAS nodes are protected by a load balancer whose "timeout" is configured less than the default session expiration timeout of CAS. Thus, once the authentication form is submitted and the request is routed to the load balancer, it is seen as a new request and is redirected back to a CAS node for authentication. 

To remedy the problem, the  suggestion is to configure the default CAS session timeout to be an appropriate value that matches the time a given user is expected to stay on the login screen. Similarly, the same change may be applied to the load balancer to treat authentication requests within a longer time span. In `web.xml`, adjust the `session-timeout` attribute to extend the session expiration time. 

### Application Not Authorized to Use CAS
You may encounter this error, when the requesting application/service url cannot be found in your CAS service registry. When an authentication request is submitted to the CAS `login` endpoint, the destination application is indicated as a url parameter which will be checked against the CAS service registry to determine if the application is allowed to use CAS. If the url is not found, this message will be displayed back. Since service definitions in the registry have the ability to be defined by a url pattern, it is entirely possible that the pattern in the registry for the service definition is misconfigured and does not produce a successful match for the requested application url.

Please [review this guide](Service-Management.html) to better understand the CAS service registry.

### Invalid/Expired CAS Tickets
You may experience `INVAILD_TICKET` related errors when attempting to use a CAS ticet whose expiration policy dictates that the ticket has expired. The CAS log should further explain in more detail if the ticket is considered expired, but for diagnostic purposes, you may want to adjust the [ticket expiration policy configuration](Configuring-Ticket-Expiration-Policy.html) to remove and troubleshoot this error.

Furthermore, if the ticket itself cannot be located in the CAS ticket registry the ticket is also considered invalid. You will need to observe the ticket used and compare it with the value that exists in the ticket registry to ensure that the ticket id provided is valid.  

### Out of Heap Memory Error
{% highlight bash %}
java.lang.OutOfMemoryError: GC overhead limit exceeded
        at java.util.Arrays.copyOfRange(Arrays.java:3658)
        at java.lang.StringBuffer.toString(StringBuffer.java:671)
        at 
{% endhighlight %}

You may encounter this error, when in all likelihood, a cache-based ticket registry such as EhCache is used whose eviction policy is not correctly configured. Objects and tickets are cached inside the registry storage back-end tend to linger around longer than they should or the eviction policy is not doing a good enough job to clean unused tickets that may be marked as expired by CAS. 

To troubleshoot, you can configure the JVM to perform a heap dump prior to exiting, which you should set up immediately so you have some additional information if/when it happens next time. The follow system properties should do the trick:

{% highlight bash %}
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="/path/to/jvm-dump.hprof" 
{% endhighlight %}

Also ensure that your container is configured to have enough memory available. For Apache Tomcat, the following setting as an environment variable may be configured:

{% highlight bash %}
CATALINA_OPTS=-Xms1000m -Xmx2000m
{% endhighlight %}

You will want to profile your server with something like [JVisualVM](http://visualvm.java.net/) which should be [bundled with the JDK](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jvisualvm.html).  This will help you see what is actually going on with your memory.

You might also consider taking periodic heap dumps using the JMap tool or [YourKit Java profiler](http://www.yourkit.com/java/profiler/) and analyzing offline using some analysis tool. 

Finally, review the eviction policy of your ticket registry and ensure the values that determine object lifetime are appropriate for your environment. 

## SSL

### PKIX Path Building Failed

{% highlight bash %}

Sep 28, 2009 4:13:26 PM org.jasig.cas.client.validation.AbstractCasProtocolUrlBasedTicketValidator retrieveResponseFromServer
SEVERE: javax.net.ssl.SSLHandshakeException:
sun.security.validator.ValidatorException: PKIX path building failed:
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
javax.net.ssl.SSLHandshakeException:
sun.security.validator.ValidatorException: PKIX path building failed:
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
      at com.sun.net.ssl.internal.ssl.Alerts.getSSLException(Unknown Source)
      at com.sun.net.ssl.internal.ssl.SSLSocketImpl.fatal(Unknown Source)
      at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Unknown Source)
      at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Unknown Source)
      at com.sun.net.ssl.internal.ssl.ClientHandshaker.serverCertificate(Unknown Source)

{% endhighlight %}

PKIX path building errors are the most common SSL errors. The problem here is that the CAS client does not trust the certificate presented by the CAS server; most often this occurs because of using a *self-signed certificate* on the CAS server. To resolve this error, import the CAS server certificate into the system truststore of the CAS client. If the certificate is issued by your own PKI, it is better to import the root certificate of your PKI into the CAS client truststore. 

By default the Java system truststore is at `$JAVA_HOME/jre/lib/security/cacerts`. The certificate to be imported **MUST** be a DER-encoded file. If the contents of the certificate file are binary, it's likely DER-encoded; if the file begins with the text `---BEGIN CERTIFICATE---`, it is PEM-encoded and needs to be converted to DER encoding. 

{% highlight bash %}
keytool -import -keystore $JAVA_HOME/jre/lib/security/cacerts -file tmp/cert.der -alias certName
{% endhighlight %}

If you have multiple java editions installed on your machine, make sure that the app / web server is pointing to the correct JDK/JRE version (The one to which the certificate has been exported correctly) One common mistake that occurs while generating self-validated certificates is that the `JAVA_HOME` might be different than that used by the server.


### No subject alternative names present

{% highlight bash %}
javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No subject alternative names present
{% endhighlight %}

This is a hostname/SSL certificate CN mismatch. This commonly happens when a self-signed certificate issued to localhost is placed on a machine that is accessed by IP address. It should be noted that generating a certificate with an IP address for a common name, e.g. CN=192.168.1.1,OU=Middleware,dc=vt,dc=edu, will not work in most cases where the client making the connection is Java.


### HTTPS hostname wrong
{% highlight bash %}
java.lang.RuntimeException: java.io.IOException: HTTPS hostname wrong:  should be <eiger.iad.vt.edu>
    org.jasig.cas.client.validation.Saml11TicketValidator.retrieveResponseFromServer(Saml11TicketValidator.java:203)
    org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator.validate(AbstractUrlBasedTicketValidator.java:185)
    org.jasig.cas.client.validation.AbstractTicketValidationFilter.doFilter
{% endhighlight %}

The above error occurs most commonly when the CAS client ticket validator attempts to contact the CAS server and is presented a certificate whose CN does not match the fully-qualified host name of the CAS server. There are a few common root causes of this mismatch:

- CAS client misconfiguration
- Complex multi-tier server environment (e.g. clustered CAS server)
- Host name too broad for scope of wildcard certificate

It is also worth checking that the certificate your CAS server is using for SSL encryption matches the one the client is checking against. 


### Wildcard Certificates
Java support for wildcard certificates is limited to hosts strictly in the same domain as the wildcard. For example, a certificate with `CN=.vt.edu` matches hosts **`a.vt.edu`** and **`b.vt.edu`**, but *not* **`a.b.vt.edu`**.


###unrecognized_name Error
{% highlight bash %}
javax.net.ssl.SSLProtocolException: handshake alert: unrecognized_name
{% endhighlight %}

The above error occurs mainly in Oracle JDK 7 CAS Server installations. In JDK7, SNI (Server Name Indication) is enabled by default. When the HTTPD Server does not send the correct Server Name back, the JDK HTTP Connection refuses to connect and the exception stated above is thrown.

You must ensure your HTTPD Server is sending back the correct hostname. E.g. in Apache HTTPD, you must set the ServerAlias in the SSL vhost:

{% highlight bash %}
ServerName your.ssl-server.name
ServerAlias your.ssl-server.name
{% endhighlight %}

Alternatively, you can disable the SNI detection in JDK7, by adding this flag to the Java options of your CAS Servers' application server configuration:
{% highlight bash %}
-Djsse.enableSNIExtension=false
{% endhighlight %}


### When All Else Fails
If you have read, understood, and tried all the troubleshooting tips on this page and continue to have problems, please perform an SSL trace and attach it to a posting to the `cas-user@lists.jasig.org` mailing list. An SSL trace is written to STDOUT when the following system property is set, `javax.net.debug=ssl`. An example follows of how to do this in the Tomcat servlet container.

Sample `setenv.sh` Tomcat Script follows:

{% highlight bash %}
# Uncomment the next 4 lines for custom SSL keystore
# used by all deployed applications
# KEYSTORE="$HOME/path/to/custom.keystore"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.keyStore=$KEYSTORE"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.keyStoreType=BKS"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.keyStorePassword=changeit"
 
# Uncomment the next 4 lines to allow custom SSL trust store
# used by all deployed applications
# TRUSTSTORE="$HOME/path/to/custom.truststore"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.trustStore=$TRUSTSTORE"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.trustStoreType=BKS"
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.ssl.trustStorePassword=changeit"
 
# Uncomment the next line to print SSL debug trace in catalina.out
# CATALINA_OPTS=$CATALINA_OPTS" -Djavax.net.debug=ssl"
 
export CATALINA_OPTS
{% endhighlight %}

## Review logs
CAS server logs are the best resource for determining the root cause of the problem, provided you have configured the appropriate log levels. Specifically you want to make sure `DEBUG` levels are turned on the `org.jasig` package in the log configuration:

{% highlight xml %}
<logger name="org.jasig" additivity="true">
        <level value="DEBUG" />
        <appender-ref ref="cas" />
</logger>
{% endhighlight %}

When changes are applied, restart the server environment and observe the log files to get a better understanding of CAS behavior. For more info, please [review the this guide](Logging.html) on how to configure logs with CAS.


