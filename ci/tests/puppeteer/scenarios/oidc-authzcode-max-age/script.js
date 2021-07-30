const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const https = require('https');
const assert = require('assert');
const axios = require('axios');
const jwt = require('jsonwebtoken');

async function fetchIdToken(page, maxAge, successHandler) {
    const redirectUrl = "https://github.com/apereo/cas";
    let url = "https://localhost:8443/cas/oidc/authorize?"
        + "response_type=code&client_id=client&scope=openid%20email%20profile&"
        + "redirect_uri=" + redirectUrl
        + "&nonce=3d3a7457f9ad3&state=1735fd6c43c14";
    if (maxAge !== undefined && maxAge > 0) {
        url += "&max_age=" + maxAge;
    }

    console.log("Navigating to " + url);
    await page.goto(url);
    await cas.loginWith(page, "casuser", "Mellon");

    if (await cas.isVisible(page, "#allow")) {
        await cas.click(page, "#allow");
        await page.waitForNavigation();
    }

    console.log("Page url " + page.url())
    let result = new URL(page.url());
    assert(result.searchParams.has("code"));
    let code = result.searchParams.get("code");
    console.log("OAuth code " + code);

    const instance = axios.create({
        httpsAgent: new https.Agent({
            rejectUnauthorized: false
        })
    });

    let accessTokenParams = "client_id=client&";
    accessTokenParams += "client_secret=secret&";
    accessTokenParams += "grant_type=authorization_code&";
    accessTokenParams += "redirect_uri=" + redirectUrl;

    let accessTokenUrl = 'https://localhost:8443/cas/oidc/token?' + accessTokenParams + "&code=" + code;
    console.log("Calling " + accessTokenUrl);

    let accessToken = null;
    await instance
        .post(accessTokenUrl, new URLSearchParams(), {
            headers: {
                'Content-Type': "application/json"
            }
        })
        .then(res => {
            console.log(res.data);
            assert(res.data.access_token !== null);

            accessToken = res.data.access_token;
            console.log("Received access token " + accessToken);

            console.log("Decoding ID token...");
            let decoded = jwt.decode(res.data.id_token);
            console.log(decoded);
            successHandler(decoded);
        })
        .catch(error => {
            throw 'Operation failed to obtain access token: ' + error;
        })
}

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);

    let time1 = null;
    let time2 = null;

    await fetchIdToken(page, -1, function(idToken) {
        time1 = idToken.auth_time
    });
    await page.waitForTimeout(2000)
    await fetchIdToken(page, 1, function(idToken) {
        time2 = idToken.auth_time;
    });

    console.log("Initial attempt; ID token auth_time: " + time1)
    console.log("Second attempt with max_age=1; ID token auth_time: " + time2)
    assert(time1 !== time2);
    
    await browser.close();
})();
