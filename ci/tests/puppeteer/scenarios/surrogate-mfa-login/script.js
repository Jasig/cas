const puppeteer = require('puppeteer');
const assert = require('assert');
const url = require('url');

(async () => {
    const browser = await puppeteer.launch({
        ignoreHTTPSErrors: true,
        headless: true,
        devtools: true
    });
    const page = await browser.newPage();
    await page.goto("https://localhost:8443/cas/login");

    await page.type('#username', "user3+casuser");
    await page.type('#password', "Mellon");
    await page.keyboard.press('Enter');
    await page.waitForNavigation();
    
    // await page.waitForTimeout(2000)

    let element = await page.$('#token');
    assert(await element.boundingBox() != null);

    await browser.close();
})();
