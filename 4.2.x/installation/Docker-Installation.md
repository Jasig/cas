---
layout: default
title: CAS - Docker Installation
---

# Docker Installation
Upon every release of the CAS software, docker images are tagged and pushed
to the Apereo CAS repository on [Docker Hub](https://hub.docker.com/r/apereo/cas/).
Images can be pulled down via the following command:

```xml
docker pull apereo/cas:v[A.B.C]
```

...where `[A.B.C]` represents the image tag that is mapped to the CAS server version.

Additional instructions on how to use CAS docker images for deployment
and adoption will become available in the future. For now, [the following
guide](https://github.com/apereo/cas-webapp-docker)
may serve as a template on how to get started with a local deployment
process.
