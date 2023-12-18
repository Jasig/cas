const puppeteer = require("puppeteer");
const assert = require("assert");
const cas = require("../../cas.js");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.gotoLogin(page);
    await cas.assertVisibility(page, "#recaptchaV3Section");
    const grecaptcha = await page.$("#g-recaptcha-token");
    assert(await grecaptcha !== null);
    await browser.close();
})();
