const assert = require('assert');

const BROWSER_OPTIONS = {
    ignoreHTTPSErrors: true,
    headless: process.env.CI === "true",
    devtools: process.env.CI !== "true",
    defaultViewport: null,
    slowMo: process.env.CI === "true" ? 0 : 15,
    args: ['--start-maximized', "--window-size=1920,1080"]
};

exports.browserOptions = () => BROWSER_OPTIONS;
exports.browserOptions = (opt) => {
    return {
        ...BROWSER_OPTIONS,
        ...opt
    };
};

exports.click = async (page, button) => {
    await page.evaluate((button) => {
        document.querySelector(button).click();
    }, button);
}

exports.clickLast = async (page, button) => {
    await page.evaluate((button) => {
        let buttons = document.querySelectorAll(button);
        buttons[buttons.length - 1].click();
    }, button);
}

exports.innerText = async (page, selector) => {
    let text = await page.$eval(selector, el => el.innerText.trim());
    console.log(`Text for selector [${selector}] is: [${text}]`);
    return text;
}

exports.textContent = async(page, selector) => {
    let element = await page.$(selector);
    let text = await page.evaluate(element => element.textContent.trim(), element);
    console.log(`Text content for selector [${selector}] is: [${text}]`);
    return text;
}

exports.inputValue = async(page, selector) => {
    let element = await page.$(selector);
    let text = await page.evaluate(element => element.value, element);
    console.log(`Input value for selector [${selector}] is: [${text}]`);
    return text;
}

exports.loginWith = async(page, user, password) => {
    console.log(`Logging in with ${user} and ${password}`);
    await this.type(page, '#username', user);
    await this.type(page, '#password', password);
    await page.keyboard.press('Enter');
    await page.waitForNavigation();
}

exports.assertVisibility = async(page, selector) => {
    let element = await page.$(selector);
    console.log(`Checking visibility for ${selector}`);
    assert(element != null && await element.boundingBox() != null);
}

exports.assertInvisibility = async(page, selector) => {
    let element = await page.$(selector);
    console.log(`Checking invisibility for ${selector}`);
    assert(element == null || await element.boundingBox() == null);
}

exports.submitForm = async(page, selector) => {
    await page.$eval(selector, form => form.submit());
    await page.waitForTimeout(2500)
}

exports.type = async(page, selector, value) => {
    await page.$eval(selector, el => el.value = '');
    await page.type(selector, value);
}

exports.newPage = async(browser) => {
    const page = (await browser.pages())[0];
    await page.setDefaultNavigationTimeout(0);
    await page.bringToFront();
    return page;
}
