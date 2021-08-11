const assert = require('assert');
const axios = require('axios');
const https = require('https');
const {spawn} = require('child_process');
const waitOn = require('wait-on');
const jwt = require('jsonwebtoken');
const colors = require('colors');
const fs = require("fs");

const BROWSER_OPTIONS = {
    ignoreHTTPSErrors: true,
    headless: process.env.CI === "true" || process.env.HEADLESS === "true",
    devtools: process.env.CI !== "true",
    defaultViewport: null,
    slowMo: process.env.CI === "true" ? 0 : 10,
    args: ['--start-maximized', "--window-size=1920,1080"]
};

exports.browserOptions = () => BROWSER_OPTIONS;
exports.browserOptions = (opt) => {
    return {
        ...BROWSER_OPTIONS,
        ...opt
    };
};

exports.removeDirectory = async(directory) => {
    console.log(colors.green(`Removing directory ${directory}`));
    fs.rmdir(directory, { recursive: true }, () => {});
}

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

exports.textContent = async (page, selector) => {
    let element = await page.$(selector);
    let text = await page.evaluate(element => element.textContent.trim(), element);
    console.log(`Text content for selector [${selector}] is: [${text}]`);
    return text;
}

exports.inputValue = async (page, selector) => {
    let element = await page.$(selector);
    let text = await page.evaluate(element => element.value, element);
    console.log(`Input value for selector [${selector}] is: [${text}]`);
    return text;
}

exports.loginWith = async (page, user, password,
                           usernameField = "#username",
                           passwordField = "#password") => {
    console.log(`Logging in with ${user} and ${password}`);
    await this.type(page, usernameField, user);
    await this.type(page, passwordField, password);
    await page.keyboard.press('Enter');
    await page.waitForNavigation();
}

exports.isVisible = async (page, selector) => {
    let element = await page.$(selector);
    console.log(`Checking visibility for ${selector}`);
    return (element != null && await element.boundingBox() != null);
}

exports.assertVisibility = async (page, selector) => {
    assert(await this.isVisible(page, selector));
}

exports.assertInvisibility = async (page, selector) => {
    let element = await page.$(selector);
    console.log(`Checking invisibility for ${selector}`);
    assert(element == null || await element.boundingBox() == null);
}

exports.assertTicketGrantingCookie = async (page) => {
    let tgc = (await page.cookies()).filter(value => value.name === "TGC");
    console.log(`Asserting ticket-granting cookie: ${tgc}`);
    assert(tgc.length !== 0);
}

exports.assertNoTicketGrantingCookie = async (page) => {
    let tgc = (await page.cookies()).filter(value => value.name === "TGC");
    console.log(`Asserting no ticket-granting cookie: ${tgc}`);
    assert(tgc.length === 0);
}

exports.submitForm = async (page, selector) => {
    console.log(`Submitting form ${selector}`);
    await page.$eval(selector, form => form.submit());
    await page.waitForTimeout(2500)
}

exports.type = async (page, selector, value) => {
    console.log(`Typing ${value} in field ${selector}`);
    await page.$eval(selector, el => el.value = '');
    await page.type(selector, value);
}

exports.newPage = async (browser) => {
    console.clear();
    let page = (await browser.pages())[0];
    if (page === undefined) {
        page = await browser.newPage();
    }
    await page.setDefaultNavigationTimeout(0);
    // await page.setRequestInterception(true);
    await page.bringToFront();
    return page;
}

exports.assertParameter = async (page, param) => {
    console.log(`Asserting parameter ${param} in URL: ${page.url()}`);
    let result = new URL(page.url());
    let value = result.searchParams.get(param);
    console.log(`Parameter ${param} with value ${value}`);
    assert(value != null);
    return value;
}

exports.assertMissingParameter = async (page, param) => {
    let result = new URL(page.url());
    assert(result.searchParams.has(param) === false);
}

exports.sleep = async (ms) => {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

exports.assertTicketParameter = async (page) => {
    console.log(`Page URL: ${page.url()}`);
    let result = new URL(page.url());
    assert(result.searchParams.has("ticket"))
    let ticket = result.searchParams.get("ticket");
    console.log(`Ticket: ${ticket}`);
    assert(ticket != null);
    return ticket;
}

exports.doRequest = async (url, method = "GET", headers = {}, statusCode = 200, requestBody = undefined) => {
    return new Promise((resolve, reject) => {
        let options = {
            method: method,
            rejectUnauthorized: false,
            headers: headers
        };
        console.log(`Contacting ${url} via ${method}`)
        const handler = (res) => {
            console.log(`Response status code: ${res.statusCode}`)
            if (statusCode > 0) {
                assert(res.statusCode === statusCode);
            }
            res.setEncoding("utf8");
            const body = [];
            res.on("data", chunk => body.push(chunk));
            res.on("end", () => resolve(body.join("")));
        };

        if (requestBody !== undefined) {
            let request = https.request(url, options, res => {
                handler(res);
            }).on("error", reject);
            request.write(requestBody);
        } else {
            https.get(url, options, res => {
                handler(res);
            }).on("error", reject);
        }
    });
}

exports.doGet = async (url, successHandler, failureHandler) => {
    const instance = axios.create({
        httpsAgent: new https.Agent({
            rejectUnauthorized: false
        })
    });
    await instance
        .get(url)
        .then(res => {
            console.log(res.data);
            successHandler(res);
        })
        .catch(error => {
            failureHandler(error);
        })
}

exports.doPost = async (url, params, headers, successHandler, failureHandler) => {
    const instance = axios.create({
        httpsAgent: new https.Agent({
            rejectUnauthorized: false
        })
    });
    let urlParams = params instanceof URLSearchParams ? params : new URLSearchParams(params);
    await instance
        .post(url, urlParams, {headers: headers})
        .then(res => {
            console.log(res.data);
            successHandler(res);
        })
        .catch(error => {
            failureHandler(error);
        })
}

exports.waitFor = async (url, successHandler, failureHandler) => {
    let opts = {
        resources: [url],
        delay: 1000,
        interval: 2000,
        timeout: 120000
    };
    await waitOn(opts)
        .then(function () {
            successHandler("good")
        })
        .catch(function (err) {
            failureHandler(err);
        });
}

exports.launchSamlSp = async (idpMetadataPath, samlSpDir, samlOpts) => {
    let args = ['-q', '-x', 'test', '--no-daemon',
        '-DidpMetadataType=idpMetadataFile',
        `-DidpMetadata=${idpMetadataPath}`,
        `-Dsp.sslKeystorePath=${process.env.CAS_KEYSTORE}`];
    args = args.concat(samlOpts);
    console.log(`Launching SAML2 SP in ${samlSpDir} with ${args}`);
    const exec = spawn('./gradlew', args, {cwd: samlSpDir});

    exec.stdout.on('data', (data) => {
        console.log(data.toString());
    });
    exec.stderr.on('data', (data) => {
        console.error(data.toString());
    });
    exec.on('exit', (code) => {
        console.log(`Child process exited with code ${code}`);
    });
    return exec;
}

exports.assertInnerTextStartsWith = async (page, selector, value) => {
    const header = await this.innerText(page, selector);
    assert(header.startsWith(value));
}

exports.assertInnerTextContains = async (page, selector, value) => {
    const header = await this.innerText(page, selector);
    assert(header.includes(value));
}

exports.assertInnerText = async (page, selector, value) => {
    const header = await this.innerText(page, selector);
    assert(header === value)
}

exports.assertPageTitle = async (page, value) => {
    const title = await page.title();
    console.log(`Page Title: ${title}`);
    assert(title === value)
}

exports.decodeJwt = async (token, complete = false) => {
    console.log(`Decoding token ${token}`);
    let decoded = jwt.decode(token, {complete: complete});
    if (complete) {
        console.log(`Decoded token header: ${colors.green(decoded.header)}`);
        console.log("Decoded token payload:");
        console.log(colors.green(decoded.payload));
    } else {
        console.log("Decoded token payload:");
        console.log(colors.green(decoded));
    }
    return decoded;
}

exports.uploadSamlMetadata = async(page, metadata) => {
    await page.goto("https://samltest.id/upload.php");
    await page.waitForTimeout(1000)
    const fileElement = await page.$("input[type=file]");
    console.log(`Metadata file: ${metadata}`);
    await fileElement.uploadFile(metadata);
    await page.waitForTimeout(1000)
    await this.click(page, "input[name='submit']")
    await page.waitForNavigation();
    await page.waitForTimeout(2000)
}
