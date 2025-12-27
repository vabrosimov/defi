# Defi (скринер defi-пулов)

## Установка зависимостей

### Установить docker buildx

``` shell
brew install docker-buildx &&
mkdir -p ~/.docker/cli-plugins &&
ln -sfn $(which docker-buildx) ~/.docker/cli-plugins/docker-buildx
```

## Деплой приложения

1. Установка docker локально и его запуск:
    1. `brew install colima docker docker-compose`
    2. `colima start`
2. Запуск скрипта `build.sh` для сборки публикации образа в Docker Hub

## Запуск приложения

1. `docker-compose up` - приложение запустится локально с двумя контейнерами: приложение и postgres с persistent volume

## Публикация в репозиторий Nexus

### Запуск nexus3 локально
1. Создать volume
``` shell
docker volume create nexus-data
```
2. Запустить образ nexus3 на порту 8081
``` shell
docker run -d --name nexus -p 8081:8081 -v nexus-data:/nexus-data sonatype/nexus3`
```
3. Nexus будет доступен по адресу `http://localhost:8081`
4. Admin пароль взять от сюда 
``` shell 
docker exec -it nexus cat /nexus-data/admin.password
```

### Прописать credentials
1. Создать и заполнить файл `~/.gradle/gradle.properties`
```
NEXUS_USER=
NEXUS_PASSWORD=
```

### Публикация
1. `./gradlew publish`