В приложении используется Java версии 22.
Этот микросервис является основным в паре из двух микросервисов (второй - https://github.com/sbnetaa/taskmanagerstatistics) и они не будут работать друг без друга. Оба микросервиса взаимодействуют через Kafka, поэтому для их работы нужно загрузить Docker образ confluentinc/cp-zookeeper и confluentinc/cp-kafka и поочередно создать и запустить контейнеры следующими командами:

```
docker network create kafka-network
```

```
docker run -d --name newzookeeper --network kafka-network -p 2181:2181 -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:latest
```
```
docker run -d --name newkafka --network kafka-network -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.0.102:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT -e KAFKA_ZOOKEEPER_CONNECT=172.18.0.2:2181 confluentinc/cp-kafka:latest
```
Где `KAFKA_ADVERTISED_LISTENERS=PLAINTEXT` и `KAFKA_ZOOKEEPER_CONNECT` скорее всего будут отличаться от тех, что указаны выше.
