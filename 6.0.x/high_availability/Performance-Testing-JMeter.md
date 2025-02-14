---
layout: default
title: CAS - JMeter Performance Testing
category: High Availability
---

# JMeter Performance Testing

Apache JMeter is a great performance testing tool that is used heavily within the Java community.

## Install JMeter

* Linux and Mac:
  * Download the JMeter binary.
    * [http://jmeter.apache.org/download_jmeter.cgi](http://jmeter.apache.org/download_jmeter.cgi)
  * Unzip apache-jmeter-*.tgz to your preferred location
  * Run `bin/jmeter.sh`
    * Note: Mac users can also use the popular HomeBrew package manager to install JMeter.
* Windows:
  * Here is a tutorial for windows setup. 
    * [http://toolsqa.com/jmeter/download-and-installation-jmeter/](http://toolsqa.com/jmeter/download-and-installation-jmeter/)

## Sample Test Scripts

Below you will find three generic runnable login scripts for the three most popular CAS implementation flavors. Please feel free to edit and use for your needs.

Although the scripts support different login methodologies, they do share some common traits.

## Common Settings Tabs

* _User Defined Variables_
  * _ThreadCount_ - Number of Threads (Kind of like Users).  Recommend starting with 100 users or so.
  * _Duration_ -  How long should the test run.  Usually, the more threads(users) the longer the duration should be
  * _RampUpPeriod_ - How long to take to ramp up to full set of thread count
* _Thread Group_ (or Tests):
  * _Loop Count_ - # of Loops, or more correctly the # of users to run through the test.  
    * Count will be associated to the total users that will run through the test
    * _Forever_ check box will loop through file and keep going till manually stopped or until reaches Duration 
    from "User Defined Variables" page
* _CSV Get Users/Passwords_:
  * Name and location of file containing test user credentials
  * Should be in the format of `User,Password`, with no spaces between “User", the “comma" and “Password”

**Scripts**

The scripts can be downloaded from [here](https://github.com/apereo/cas/raw/master/etc/loadtests/).
  
* **_CAS_CAS.jmx_**
  * Vanilla installation of CAS using standard CAS login process
  * No SP (Service Provider) is needed
  * User Defined Variables:
    * _IdPHost_ - URL of your CAS instance
    * _CasSP_ - SP (Service Provider) URL but does not have to be active
  * Test Fragments:
    * _GET - CAS Login Page_ -- Access login page for a typical CAS login
    * _POST - Login Credentials_ -- Post credentials from user file into CAS instance
    * _GET - User Info with Service Ticket_ -- Get user info with Service Ticket that CAS generated when user logged in
      * Under Assertion, may need to update expected user results
    * _GET - User Logout_ -- Logout user from CAS session via CAS logout
* **_CAS_Oauth.jmx_**
  * CAS supporting OAuth login process
  * An active SP is optional
  * Script reflects the most common way that OAuth is used, the Authorization Code method
  * _User Defined Variables_:
    * _IdPHost_ - URL of your CAS instance
    * _CasSP_ - SP (Service Provider) URL but does not have to be active
    * _SpClientId_ - The clientId of the SP within the CAS service file
    * _SpRedirectUri_ - Endpoint in SP that will be used to receive the "Authorization Code"
    * _SpState_ - CSRF token used
    * _SpClientName_ - The OAuth call type being used for authentication
    * _SpResponseType_ - The OAuth method being used, in this case "code", which stands for "Authorization Code"
    * _SpClientSecret_ - Secret phrase or word shared between the SP and CAS
  * _Test Fragments_:
    * _Verify Service Provider_ -- Verifies URL to SP is correct (Optional, can be disabled)
    * _Start CAS Login process_ -- Accessing CAS login page for OAuth with all parameters set
    * _1a-1d_ -- Post login credentials for user, followed by redirects to get code in Access Token
      * broken into several processes due to encoding issues when testing
    * _GET - User Profile with Access Token_ -- Call to CAS to get the user's info with Access Token
      * Under Assertion, may need to update expected user results
    * _GET - User Logout_ -- Logout user from CAS session via CAS logout
* **_CAS_SAML2.jmx_**
  * CAS support for SAML2 Login process
  * An active SP is required!
    * For this test used SimpleSAMLphp
  * _User Defined Variables_:
    * _CasSP_ - Domain of registered CAS SP using SAML
    * _ProviderId_ - SAML EntityID stated in metadata for SP
  * _Test Fragments_:
    * _Go To SP for CAS Login_ -- SP page protected by SAML2 that will redirect to CAS login endpoint
    * _POST - Login User_ -- Post credentials from user file into CAS SAML2 login
    * _POST - CAS Authorization to SP_ -- Send response from CAS to SP for processing and final request for user info
      * May need to updated Assertion for successful user information returned
    * _GET - User Logout_ -- Logout user from CAS session via CAS logout

## Run Test Scripts

Once you have saved the test scripts to your system. You can either run within the JMeter
GUI or via command line. It his highly recommended that the GUI be used for 
troubleshooting the scripts to work within your environment. Then, when you actually start
load testing, you do that via the command line.

To activate the JMeter GUI, from the command line type:

```bash
prompt$ /usr/local/bin/jmeter
```

This path should correspond to the location you chose to install Jmeter.

A simple example of a JMeter startup via command line:

```bash
prompt$ /usr/local/bin/jmeter -n -t your_script.jmx
```

`-n` run JMeter in non-GUI mode.
`-t` path to .jmx test file.

More examples can be found on the [Jmeter site](http://jmeter.apache.org/usermanual/get-started.html#non_gui).
