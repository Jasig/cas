---
layout: default
title: CAS - Basic Authentication
---

# Basic Authentication
Verify and authenticate credentials using Basic Authentication.

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-basic</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To access a CAS-protected application using a command-line client such as `curl`, the following command may be used:

```xml
curl <APPLICATION-URL> -L -u <USER>:<PASSWORD>
```

Use `--insecure -v` flags to bypass certificate validation and receive additional logs from `curl`. 

If your APPLICATION-URL and CAS-SERVER-URL are not on the same host, curl will NOT send the Basic Authentication header to the CAS server when redirected. This behavior in curl can be overriden by passing the `--location-trusted` flag to curl.

From CURL man page:
```
      --location-trusted
              (HTTP/HTTPS) Like -L, --location, but will allow sending the name + password to all hosts that the site may redirect to. This may or may
              not  introduce a security breach if the site redirects you to a site to which you'll send your authentication info (which is plaintext in the case of HTTP Basic authentication).
```
