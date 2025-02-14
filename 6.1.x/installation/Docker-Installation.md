---
layout: default
title: CAS - Docker Installation
category: Installation
---

# Docker Installation

Upon every release of the CAS software, docker images are tagged and pushed
to the Apereo CAS repository on [Docker Hub](https://hub.docker.com/r/apereo/cas/).
Images can be pulled down via the following command:

```bash
docker pull apereo/cas:v[A.B.C]
```

...where `[A.B.C]` represents the image tag that is mapped to the CAS server version.

## Overview

A dockerized CAS deployment simply is an existing [CAS overlay project](WAR-Overlay-Installation.html) that is wrapped by Docker.
The overlay project already includes an embedded container to handle the deployment of CAS.
The overlay project also includes an embedded build tool so that builds and deployments of CAS 
would not require a separate step to download and configure choices. 

The docker images that are hosted on Docker Hub are *mostly* meant to be used
as quickstarts and demos. You may also be able to use them as
base images to add your customizations into the image. The image
is built out of an existing [CAS overlay](WAR-Overlay-Installation.html).
