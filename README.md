#Create 4 spring boot projects
  > order-api
  > pattern-listener
  > reward-listener
  > storage-listener
  > order-kafka-stream

#CLI command
  * Generate order-api
  ```
  curl https:start.spring.iostarter.tgz  \
    -d artifactId=order-api \
    -d groupId=org.efire.net \
    -d dependencies=web,actuator,jpa,h2,kafka,lombok,devtools \
    -d language=java \
    -d type=maven-project \
    -d baseDir=order-api \
    -d bootVersion=2.4.4.RELEASE \
    -d packageName=org.efire.net \
  | tar -xzvf -
  ```
  * reward-listener
  ```
  curl https:start.spring.iostarter.tgz  \
    -d artifactId=reward-listener \
    -d groupId=org.efire.net \
    -d dependencies=kafka,devtools \
    -d language=java \
    -d type=maven-project \
    -d baseDir=reward-listener \
    -d bootVersion=2.4.4.RELEASE \
    -d packageName=org.efire.net \
  | tar -xzvf -
  ```
  * pattern-listener
  ```
  curl https:start.spring.iostarter.tgz  \
    -d artifactId=pattern-listener \
    -d groupId=org.efire.net \
    -d dependencies=kafka,devtools \
    -d language=java \
    -d type=maven-project \
    -d baseDir=pattern-listener \
    -d bootVersion=2.4.4.RELEASE \
    -d packageName=org.efire.net \
  | tar -xzvf -
  ```
  * storage-listener
  ```
  curl https:start.spring.iostarter.tgz  \
    -d artifactId=storage-listener \
    -d groupId=org.efire.net \
    -d dependencies=kafka,devtools \
    -d language=java \
    -d type=maven-project \
    -d baseDir=storage-listener \
    -d bootVersion=2.4.4.RELEASE \
    -d packageName=org.efire.net \
  | tar -xzvf -
  ```
  * order-kafka-stream
  ```
  curl https:start.spring.iostarter.tgz  \
    -d artifactId=order-kafka-stream \
    -d groupId=org.efire.net \
    -d dependencies=kafka,devtools \
    -d language=java \
    -d name=order-kafka-stream \
    -d type=maven-project \
    -d baseDir=order-kafka-stream \
    -d bootVersion=2.4.4.RELEASE \
    -d packageName=org.efire.net \
  | tar -xzvf -
  ```
#Create Kafka Topic
```
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic t.commodity.order --partitions 1 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic t.commodity.order-reply --partitions 1 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic t.commodity.promotion --partitions 1 --replication-factor 1
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.promotion-uppercase
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-one
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.reward-one
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.storage-one
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-two.plastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-two.notplastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.reward-two
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.storage-two

kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-four.plastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-four.notplastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.reward-four
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.storage-four

kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-five.plastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.pattern-five.notplastic
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.reward-five
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.storage-five
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic t.commodity.fraud-five

```

#Kakfa Console to test consuming from t.commodity.order
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.order --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.promotion --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.order-masked --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.pattern-one --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.reward-one --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.storage-one --from-beginning

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.reward-five --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic t.commodity.storage-five --from-beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true value.deserializer=org.apache.kafka.common.serialization.LongDeserializer --topic t.commodity.fraud-five --from-beginning
```