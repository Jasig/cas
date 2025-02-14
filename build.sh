#!/bin/sh
#
# bundle exec jekyll build --safe
# bundle exec jekyll build
bundle install
bundle exec jekyll serve --incremental --watch --profile
