const puppeteer = require("puppeteer");
const cas = require("../../cas.js");
const assert = require("assert");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.gotoLoginWithAuthnMethod(page, undefined, "mfa-duo");
    await cas.updateDuoSecurityUserStatus("duocode");
    await cas.loginWith(page, "duocode", "Mellon");
    await page.waitForTimeout(4000);
    const bypassCodes = await cas.fetchDuoSecurityBypassCodes("duocode");
    await cas.loginDuoSecurityBypassCode(page, "duocode", bypassCodes);
    await page.waitForTimeout(4000);
    await cas.screenshot(page);
    await cas.assertInnerText(page, "#content div h2", "Log In Successful");
    await cas.assertCookie(page);
    await cas.gotoLogout(page);

    await cas.gotoLogin(page, "https://localhost:9859/anything/postservice");
    await cas.loginWith(page, "duocode", "Mellon");
    await page.waitForTimeout(4000);
    await cas.loginDuoSecurityBypassCode(page, "duocode", bypassCodes.slice(1));
    await page.waitForTimeout(4000);
    await cas.screenshot(page);
    await cas.logPage(page);
    let content = await cas.textContent(page, "body");
    let payload = JSON.parse(content);
    assert(payload.form.ticket !== undefined);
    assert(payload.method === "POST");
    await cas.gotoLogout(page);
    
    await cas.gotoLogin(page, "https://localhost:9859/anything/postmethod", 8443, false, "POST");
    await cas.loginWith(page, "duocode", "Mellon");
    await page.waitForTimeout(4000);
    await cas.loginDuoSecurityBypassCode(page, "duocode", bypassCodes.slice(2));
    await page.waitForTimeout(4000);
    await cas.screenshot(page);
    await cas.logPage(page);
    content = await cas.textContent(page, "body");
    payload = JSON.parse(content);
    assert(payload.form.ticket !== undefined);
    assert(payload.method === "POST");
    await cas.gotoLogout(page);
    
    await browser.close();
})();
