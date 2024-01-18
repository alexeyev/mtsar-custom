#!/usr/bin/env bash

cp docker-compose.yml.example docker-compose.yml
docker-compose build
docker-compose up -d postgres
docker-compose run --rm app /mtsar/mtsar.sh db migrate
docker-compose up --no-recreate -d app