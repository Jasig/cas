---
layout: default
title: CAS - Installation Requirements
---

# Installation Requirements

Depending on choice of configuration components, there may be additional requirements such as LDAP directory,
database, and caching infrastructure. In most cases, however, requirements should be self evident to deployers who
choose components with clear hardware and software dependencies. In any case where additional requirements are
not obvious, the discussion of component configuration should mention system, software, hardware, and other
requirements.

## Java

CAS at its heart is a Java-based web application. Prior to deployment, you will need to have [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) `v1.8` installed.

## Servlet Containers

There is no officially supported servlet container for CAS, but [Apache Tomcat](http://tomcat.apache.org/) is the most
commonly used. Support for a particular servlet container depends on the expertise of community members.

See [this guide](../installation/Configuring-Servlet-Container.html) for more info.

## Build Tools

Maven or Gradle overlays are [provided](../installation/Maven-Overlay-Installation.html) to allow for a straightforward and flexible 
deployment solution. While it admittedly requires a high up-front cost in learning, it reaps numerous
benefits in the long run. 

<div class="alert alert-info"><strong>Do Less</strong><p>
You <b>DO NOT</b> need to have Maven or Gradle installed prior to the installation. They are provided to you automatically.
</p></div>

## Git (Optional)

While not strictly a requirement, it's HIGHLY recommended that you have [Git](https://git-scm.com/downloads) installed for your CAS deployment,
and manage all CAS artifacts, configuration files, build scripts and setting inside a source control repository.

## OS

No particular preference on the operating system, though Linux-based installs are typically more common than Windows.

## Internet Connectivity

Internet connectivity is generally required for the build phase of any Maven/Gradle based project, including the recommended WAR overlays used to install CAS. The build process resolves dependencies by searching online repositories containing artifacts (jar files in most cases) that are downloaded and installed locally.

## Hardware

Anecdotal community evidence seems to suggest that CAS deployments would perform well on a dual-core 3.00Ghz processor with 8GB of memory, at a minimum. Enough disk space (preferably SSD) is also needed to house CAS-generated logs, if logs are kept on the server itself.

Remember that the above requirements are simply *suggestions*. You may get by perfectly fine with more or less, depending on your deployment and request volume. Start with the bare minimum and be prepared to adjust and strengthen capacity on demand if needed.
