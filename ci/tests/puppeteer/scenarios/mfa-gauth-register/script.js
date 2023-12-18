const puppeteer = require("puppeteer");
const assert = require("assert");
const cas = require("../../cas.js");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/login?authn_method=mfa-gauth&locale=en");
    await cas.loginWith(page);

<<<<<<< HEAD
    await cas.assertInnerTextStartsWith(page, "#login h2", "Your account is not registered");
    await cas.assertVisibility(page, 'img#imageQRCode');
    await cas.assertVisibility(page, '#seckeypanel pre');
    await cas.assertVisibility(page, '#scratchcodes');
    assert(5 === (await page.$$('#scratchcodes div.mdc-chip')).length);
=======
    await cas.assertInnerTextStartsWith(page, "#login h4", "Your account is not registered");
    await cas.assertVisibility(page, "img#imageQRCode");
    await cas.assertVisibility(page, "#seckeypanel pre");
    await cas.assertVisibility(page, "#scratchcodes");
    assert((await page.$$("#scratchcodes div.mdc-chip")).length === 5);
>>>>>>> master

    const confirm = await page.$("#confirm");
    await confirm.click();
    await cas.assertVisibility(page, "#confirm-reg-dialog #notif-dialog-title");
    await cas.assertVisibility(page, "#token");
    await cas.assertVisibility(page, "#accountName");
    await browser.close();
})();
