#!/bin/bash
echo -e "Removing previous SAML metadata directory"
rm -Rf "${PWD}/ci/tests/puppeteer/scenarios/saml2-idp-sp-logout-post/saml-md"
