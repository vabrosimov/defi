#!/usr/bin/env bash

# Строгий режим bash (падает при любых ошибках)
set -euo pipefail

# Собрать build
./gradlew clean bootJar

# Логин в docker hub
read -p "Docker Hub username: " USERNAME </dev/tty
read -s -p "Docker Hub password: " PASSWORD </dev/tty
echo

echo "${PASSWORD}" | docker login -u "${USERNAME}" --password-stdin

# Собираем Docker-образ
docker build -t "${USERNAME}/defi:latest" .

# Пушим образ
docker push "${USERNAME}/defi:latest"
