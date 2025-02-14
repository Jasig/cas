---
layout: default
title: CAS - Amazon Cloud Directory Authentication
---

# Amazon Cloud Directory Authentication

Amazon Cloud Directory is a highly available multi-tenant directory-based store in AWS. These directories scale automatically to hundreds of millions of objects as needed for applications. This lets operation's staff focus on developing and deploying applications that drive the business, not managing directory infrastructure.

To learn more, please [see this guide](http://docs.aws.amazon.com/directoryservice/latest/admin-guide/directory_amazon_cd.html).

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-cloud-directory-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#amazon-cloud-directory-authentication).
