const assert = require('assert');
const cas = require('../../cas.js');

(async () => {

    let params = "client_id=client&";
    params += "client_secret=secret&";
    params += "grant_type=client_credentials&";
    params += "scope=openid%20MyCustomScope%20email";

    let url = `https://localhost:8443/cas/oidc/token?${params}`;
    console.log(`Calling ${url}`);

    await cas.doPost(url, "", {
        'Content-Type': "application/json"
    }, async res => {

        console.log(res.data);
        assert(res.data.access_token !== null);

        console.log("Decoding JWT access token...");
        await cas.decodeJwt(res.data.access_token);

        console.log("Decoding JWT ID token...");
        let decoded = await cas.decodeJwt(res.data.id_token);

        assert(res.data.id_token !== null);
        assert(res.data.refresh_token !== null);
        assert(res.data.token_type !== null);
        assert(res.data.scope === 'MyCustomScope openid');
        
        assert(decoded.sub === "client");
        assert(decoded["cn"] === undefined);
        assert(decoded.name === "CAS");
        assert(decoded["client_id"] === "client");
        assert(decoded["preferred_username"] === "client");
        assert(decoded["gender"] === "Female");
        assert(decoded["given-name"] === undefined)
    }, error => {
        throw `Operation failed: ${error}`;
    });
})();
