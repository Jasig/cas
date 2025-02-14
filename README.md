
# CAS Documentation


## Requirements
Install the build dependencies and tools via: [https://help.github.com/articles/using-jekyll-with-pages/](https://help.github.com/articles/using-jekyll-with-pages/).

## Getting Started

1. `git clone git@github.com:Jasig/cas.git cas.site`
2. `git checkout gh-pages`
3. Change documentation as necessary
4. `git push --set-upstream origin gh-pages`

## Local Site Generation

```bash
./build[sh|bat]
```

To view the site locally:

```bash
bundle exec jekyll serve
```

Navigate to http://localhost:4000 to see the local site.

## Site Structure

The documentation site is composed of the following blocks:

- Each version of the documentation is moved to an appropriately named folder (i.e. `4.0.0`)
- Each version contains its own version of the sidebar TOC. While the TOC is designed to be included in the default
Jekyll layout, the site will load the appropriate version of the TOC on `apereo.github.io/cas`.
- The `current` folder contains the version of the documentation in development
- Developer-related documentation is hosted at the root under the `developer` directory
- The root `index.html` always points to the `current\index.html` page.
- Page titles are auto-calculated based on the first `h1` element on the page, followed by the version of the
documentation (i.e. `Service Management (x.y.z)`)
