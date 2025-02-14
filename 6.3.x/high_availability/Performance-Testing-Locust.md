---
layout: default
title: CAS - High Availability Performance Testing
category: High Availability
---

# Locust Performance Testing

[Locust](http://locust.io/) is an easy-to-use, distributed, user load testing tool. It is intended for load-testing web sites (or other systems) and figuring out how many concurrent users a system can handle. [See this guide](http://docs.locust.io/en/latest/what-is-locust.html) for more info.

### Setup

A fundamental feature of Locust is that you describe all your tests in Python code. No need for clunky UIs or bloated XML, just plain code. For this to work, you will need to [download Python](https://www.python.org/downloads/). Next download the Locust test suite [from here](https://github.com/apereo/cas/raw/master/etc/loadtests) and [configure a virtual environment](https://virtualenv.pypa.io/en/stable/) to install modules:

```bash
pip3 install virtualenv
virtualenv mylocustenv/
pip3 install -r requirements.txt
```

Install Locust via the following:

```bash
pip3 install locustio
```

Create a `credentials.csv` file that contains `username,password` entries used for load tests.

```bash
echo casuser,Mellon > cas/credentials.csv
```

Run the script as such:

```bash
locust -f cas/casLocust.py --host=https://cas.example.org
...
[2017-05-02 16:31:49,742] test/INFO/locust.main: Starting web monitor at *:8089
[2017-05-02 16:31:49,744] test/INFO/locust.main: Starting Locust 0.8a2
```

Navigate to [http://localhost:8089](http://localhost:8089) and proceed with starting the test swarm.

For additional options, use:

```bash
locust --help
```
