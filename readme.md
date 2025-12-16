# Defi (скринер defi-пулов)

## Деплой приложения
1. Установка docker локально и его запуск:
   1. brew install colima docker docker-compose
   2. colima start
2. Запуск скрипта build.sh для сборки публикации образа в Docker Hub

## Запуск приложения
1. docker-compose up - приложение запустится локально с двумя контейнерами: приложение и postgres с persistent volume

