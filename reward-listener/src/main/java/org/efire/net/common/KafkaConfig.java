package org.efire.net.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.efire.net.broker.message.OrderMessage;
import org.efire.net.broker.message.OrderReplyMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderMessage> kafkaListenerContainerFactory(KafkaTemplate<String, OrderReplyMessage> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, OrderMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.setReplyTemplate(kafkaTemplate);
        factory.setErrorHandler((e, consumerRecord) -> {
            log.info("Error in Consumer is \n {} \n and record was \n {}", e.getMessage(), consumerRecord);
        });

        factory.setRetryTemplate(simpleRetryTemplate());
        factory.setRecoveryCallback(retryContext -> {
            if (retryContext.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
                log.info("Recoverable logic here...");
            } else {
                log.info("Non-recoverable logic here...");
                throw new RuntimeException(retryContext.getLastThrowable().getMessage());
            }
            return null;
        });

        return factory;

    }

    private ConsumerFactory<String, OrderMessage> consumerFactory() {
        var config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(GROUP_ID_CONFIG, "group.reward.order");
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(OrderMessage.class));
    }

    private RetryTemplate simpleRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1400);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy());
        return retryTemplate;
    }


    private RetryPolicy simpleRetryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);

//        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
//        retryableExceptions.put(IllegalArgumentException.class, false);
//        retryableExceptions.put(RecoverableDataAccessException.class, true);
//        retryableExceptions.put(LibraryEventException.class, false);
//        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, retryableExceptions, true);

        return simpleRetryPolicy;
    }
}
