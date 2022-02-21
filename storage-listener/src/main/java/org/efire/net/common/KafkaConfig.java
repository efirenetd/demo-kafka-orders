package org.efire.net.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
/*        factory.setErrorHandler((e, consumerRecord) -> {
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
        });*/

        return factory;

    }

    private ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"group-x");
/*        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,JsonDeserializer.class );*/
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        JsonDeserializer<Object> deser = new JsonDeserializer<>()
                .trustedPackages("*")
                .typeFunction(StorageUtils::promoOrDiscount);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                deser);
    }

/*    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties props) {
        var consumerProps = props.buildConsumerProperties();
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TYPE_MAPPINGS,
                "promotionMessage:"+ PromotionMessage.class.getName() + ",discountMessage:"+ DiscountMessage.class.getName());
        return new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(Object.class));
    }*/

    private RetryTemplate simpleRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(5000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy());
        return retryTemplate;
    }

    private RetryPolicy simpleRetryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);
        return simpleRetryPolicy;
    }
}
