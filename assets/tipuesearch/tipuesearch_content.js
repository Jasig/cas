---
# Content index for Tipue Search
# https://github.com/jekylltools/jekyll-tipue-search
# v1.0
---
{% assign index = "" | split: "" %}
{% assign excluded_files = site.tipue_search.exclude.files %}
{% assign excluded_taxonomies = "" | split: "" %}
{% for tag in site.tipue_search.exclude.tags %}
  {% assign excluded_taxonomies = excluded_taxonomies | push: tag | uniq %}
{% endfor %}
{% for category in site.tipue_search.exclude.categories %}
  {% assign excluded_taxonomies = excluded_taxonomies | push: category | uniq %}
{% endfor %}
{% for post in site.posts %}
  {% unless post.exclude_from_search == true or excluded_files contains post.path %}
    {% assign has_excluded_taxonomy = false %}
    {% for tag in post.tags %}
      {% if excluded_taxonomies contains tag %}
        {% assign has_excluded_taxonomy = true %}
      {% endif %}
    {% endfor %}
    {% for category in post.categories %}
      {% if excluded_taxonomies contains category %}
        {% assign has_excluded_taxonomy = true %}
      {% endif %}
    {% endfor %}
    {% unless has_excluded_taxonomy == true %}
      {% assign index = index | push: post | uniq %}
    {% endunless %}
  {% endunless %}
{% endfor %}
{% if site.tipue_search.include.pages == true %}
  {% for page in site.html_pages %}
    {% unless page.exclude_from_search == true or excluded_files contains page.path %}
      {% assign has_excluded_taxonomy = false %}
      {% for tag in page.tags %}
        {% if excluded_taxonomies contains tag %}
          {% assign has_excluded_taxonomy = true %}
        {% endif %}
      {% endfor %}
      {% for category in page.categories %}
        {% if excluded_taxonomies contains category %}
          {% assign has_excluded_taxonomy = true %}
        {% endif %}
      {% endfor %}
      {% unless has_excluded_taxonomy == true %}
        {% assign index = index | push: page | uniq %}
      {% endunless %}
    {% endunless %}
  {% endfor %}
{% endif %}
{% for collection in site.tipue_search.include.collections %}
  {% assign documents = site.documents | where:"collection",collection %}
  {% for document in documents %}
    {% unless document.exclude_from_search == true or excluded_files contains document.path %}
      {% assign has_excluded_taxonomy = false %}
      {% for tag in document.tags %}
        {% if excluded_taxonomies contains tag %}
          {% assign has_excluded_taxonomy = true %}
        {% endif %}
      {% endfor %}
      {% for category in document.categories %}
        {% if excluded_taxonomies contains category %}
          {% assign has_excluded_taxonomy = true %}
        {% endif %}
      {% endfor %}
      {% unless has_excluded_taxonomy == true %}
        {% assign index = index | push: document | uniq %}
      {% endunless %}
    {% endunless %}
  {% endfor %}
{% endfor %}
var tipuesearch = {"pages": [
{% for document in index %}
  {% assign taxonomies = "" | split: "" %}
  {% for tag in document.tags %}
    {% assign taxonomies = taxonomies | push: tag | uniq %}
  {% endfor %}
  {% for category in document.categories %}
    {% assign taxonomies = taxonomies | push: category | uniq %}
  {% endfor %}
  {
    "title": {{ document.title | smartify | strip_html | normalize_whitespace | jsonify }},
    "text": {{ document.content | strip_html | normalize_whitespace | jsonify }},
    "tags": {{ taxonomies | join: " " | normalize_whitespace | jsonify }},
    "url": {{ document.url | relative_url | jsonify }}
  }{% unless forloop.last %},{% endunless %}
{% endfor %}
]};
