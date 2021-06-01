const puppeteer = require('puppeteer');
const assert = require('assert');
const cas = require('../../cas.js');

(async () => {

    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await browser.newPage();
    await page.setDefaultNavigationTimeout(0);
    await page.goto("https://localhost:8443/cas/login?authn_method=mfa-gauth");
    await cas.loginWith(page, "casuser", "Mellon");
    await page.waitForTimeout(1000)

    console.log("Using scratch code to login...");
    await page.type('#token', "83766843");
    await cas.submitForm(page, "#fm1");

    await cas.innerText(page, '#deviceName');
    await page.type('#deviceName', "My Trusted Device");
    
    await cas.assertInvisibility(page, '#expiration')

    await cas.assertVisibility(page, '#timeUnit')

    await cas.submitForm(page, "#registerform");

    const header = await cas.innerText(page, '#content div h2');

    assert(header === "Log In Successful")

    await browser.close();
})();
