const cas = require("../../cas.js");
const assert = require("assert");

(async () => {
    const browser = await cas.newBrowser(cas.browserOptions());
    const page = await cas.newPage(browser);

    await cas.goto(page, "https://localhost:8444/cas/login");
    await cas.sleep(2000);

    await cas.assertVisibility(page, "#loginProviders");
    await cas.assertVisibility(page, "li #SAML");

    await cas.log("Choosing SAML2 identity provider for login...");
    await cas.click(page, "li #SAML");
    await cas.waitForNavigation(page);
    await cas.sleep(2000);

    await cas.loginWith(page);
    await cas.sleep(2000);
    const url = await page.url();
    assert(url === "https://localhost:8444/cas/login?client_name=SAML");
    await cas.assertPageTitle(page, "CAS - Central Authentication Service Log In Successful");
    await cas.assertInnerText(page, "#content div h2", "Log In Successful");

    await cas.goto(page, "https://localhost:8443/cas/logout");
    await cas.sleep(4000);

    await cas.goto(page, "https://localhost:8444/cas/login");
    await cas.sleep(2000);
    await cas.assertPageTitle(page, "CAS - Central Authentication Service Login");

    await browser.close();
})();
