const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await page.goto("https://localhost:8443/cas/login");
    await cas.loginWith(page, "locked", "locked");
    await cas.assertInnerText(page, "#content h2", "This account has been locked.")
    await browser.close();
})();
