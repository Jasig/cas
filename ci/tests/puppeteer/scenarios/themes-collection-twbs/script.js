const puppeteer = require('puppeteer');
const assert = require('assert');

(async () => {
    const browser = await puppeteer.launch({
        ignoreHTTPSErrors: true
    });
    const page = await browser.newPage();
    await page.goto("https://localhost:8443/cas/login");

    await page.type('#username', "casuser");
    await page.type('#password', "Mellon");
    await page.keyboard.press('Enter');
    await page.waitForNavigation();

    const tgc = (await page.cookies()).filter(value => value.name === "TGC")
    assert(tgc.length !== 0);

    const title = await page.title();
    console.log(title)
    assert(title === "CAS Boostrap Theme")

    const header = await page.$eval('#content div h2', el => el.innerText.trim())
    console.log(header)
    assert(header === "Log In Successful")

    await page.goto("https://localhost:8443/cas/logout");

    const headerlogout = await page.$eval('#content div h2', el => el.innerText.trim())
    console.log(headerlogout)
    assert(headerlogout === "Logout successful")


    await browser.close();
})();
