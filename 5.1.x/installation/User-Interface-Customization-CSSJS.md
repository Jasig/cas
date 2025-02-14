---
layout: default
title: CAS - User Interface Customization
---

# CSS

The default styles are all contained in two single files located in `src/main/resources/static/css/cas.css` and `src/main/resources/static/css/admin.css`. This location is set in `cas-theme-default.properties`.
If you would like to create your own `css/custom.css file`, for example, you will need to update `standard.custom.css.file` key in that file.

```bash
standard.custom.css.file=/css/cas.css
admin.custom.css.file=/css/admin.css
cas.javascript.file=/js/cas.js
```

## Responsive Design

CSS media queries bring responsive design features to CAS which would allow adopter to focus on one theme for all appropriate devices and platforms. These queries are defined in the same `cas.css` file.

# Javascript

If you need to add some JavaScript, feel free to append `src/main/resources/static/js/cas.js`.

You can also create your own `custom.js` file, for example, and call it from within `bottom.html` like so:

```html
<script type="text/javascript" src="/js/custom.js"></script>
```

If you are developing themes per service, each theme also has the ability to specify a custom `cas.js` file under the `cas.javascript.file` setting.

The following Javascript libraries are utilized by CAS automatically:

* JQuery
* JQuery UI
* JQuery Cookie
* Bootstrap

## Asynchronous Script Loading
CAS will attempt load the aforementioned script libraries asynchronously so as to not block the page rendering functionality.
The loading of script files is handled by the [`head.js` library](http://headjs.com) and is the responsibility of `cas.js` file.

The only script that is loaded synchronously is the `head.js` library itself.

Because scripts, and specially JQuery are loaded asynchronously, any custom Javascript that is placed inside the page
that relies on these libraries may not immediately function on page load. CAS provides a callback function that allows
adopters to be notified when script loading has completed and this would be a safe time to execute/load other Javascript-related
functions that depend on JQuery inside the actual page.

```javascript
function jqueryReady() {
    //Custom Javascript tasks can be carried out now via JQuery...
}
```

## Checking CAPSLOCK
CAS will display a brief warning when the CAPSLOCK key is turned on during the typing of the credential password. This check is enforced by the `cas.js` file.

## Browser Cookie Support
For CAS to honor a single sign-on session, the browser MUST support and accept cookies. CAS will notify the
user if the browser has turned off its support for cookies. This behavior is controlled via the `cas.js` file.

## Preserving Anchor Fragments
Anchors/fragments may be lost across redirects as the server-side handler of the form post ignores the client-side anchor, unless appended to the form POST url.
This is needed if you want a CAS-authenticated application to be able to use anchors/fragments when bookmarking.

### Changes to `cas.js`
```javascript
/**
 * Prepares the login form for submission by appending any URI
 * fragment (hash) to the form action in order to propagate it
 * through the re-direct (i.e. store it client side).
 * @param form The login form object.
 * @returns true to allow the form to be submitted.
 */
function prepareSubmit(form) {
    // Extract the fragment from the browser's current location.
    var hash = decodeURIComponent(self.document.location.hash);

    // The fragment value may not contain a leading # symbol
    if (hash && hash.indexOf("#") === -1) {
        hash = "#" + hash;
    }

    // Append the fragment to the current action so that it persists to the redirected URL.
    form.action = form.action + hash;
    return true;
}
```


### Changes to Login Form

```html
<form method="post" id="fm1" th:object="${credential}">
        onsubmit="return prepareSubmit(this);">
```
