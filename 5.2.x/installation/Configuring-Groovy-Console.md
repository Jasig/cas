---
layout: default
title: CAS - Groovy Shell
---

# CAS Groovy Shell

This is a [Groovy remote shell embedded inside the CAS server](http://bit.ly/1P68woD)
that could be used by deployers to interact with the CAS API at runtime,
to query the runtime state of the software and execute custom Groovy scripts. The console is aware of the CAS application
context and is also able to load custom groovy scripts which
may want to peek inside the CAS configuration, invoke its API and perhaps report back various bits of configuration about CAS.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>
Functionality provided by this module is deprecated and will be removed in future CAS versions, 
as support for CRaSH (the framework handling shell operations) is removed from Spring Boot v2. Consider using the <a href="Configuring-Commandline-Shell.html">CAS Shell</a> instead as a more viable replacement.</p></div>

Support is provided via the following dependency:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-remote-shell</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

CAS supports an integrated Java shell called [CRaSH](http://www.crashub.org/).
By default the remote shell will listen for connections
on port 2000. The default user is `user` and the
default password will be randomly generated and displayed in the log output if one is not configured.

```bash
ssh -p 2000 user@localhost

Using default password for shell access: ec03326c-4cf4-49ee-b745-7bb255c1dd7e
```


Type `help` for a list of commands once inside the shell.
CAS provides `metrics`, `beans`, `autoconfig` and `endpoint` commands.

### Custom Groovy Scripts

The shell by default will compile and load all groovy scripts that are found at the specified location below.
Scripts are loaded by their class name and added to the shell. Here is an example groovy script that, when invoked, will
return the CAS version and ticket/service registry type names:

```groovy
package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

class cas {

    @Usage("Output the current version of the CAS server")
    @Command
    def main(InvocationContext context) {

        def beans = context.attributes['spring.beanfactory']
        def environment = context.attributes['spring.environment']

        def ticketRegistry = beans.getBean("ticketRegistry")
        def serviceRegistry = beans.getBean("serviceRegistryDao")

        return "CAS version: " + org.apereo.cas.util.CasVersion.getVersion() +
                "\nTicket registry instance: " + ticketRegistry.getClass().getSimpleName() +
                "\nService registry instance: " + serviceRegistry.getClass().getSimpleName()
    }
}
```


### Settings

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#groovy-shell).
