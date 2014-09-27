#!/usr/bin/env bash

(cd ui; npm install; bower install; tsd reinstall -so; grunt prod)
play dist
