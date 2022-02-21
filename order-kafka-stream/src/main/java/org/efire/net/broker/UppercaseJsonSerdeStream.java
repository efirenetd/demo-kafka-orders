package org.efire.net.broker;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.efire.net.broker.message.PromotionMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

//@Configuration
public class UppercaseJsonSerdeStream {

    @Bean
    public KStream<String, PromotionMessage> topology(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<PromotionMessage>();
        var convertedPromotionMessage = builder
                .stream("t.commodity.promotion", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(this::convertPromoCodeToUppercase);

        //send to topic
        convertedPromotionMessage.to("t.commodity.promotion-uppercase");

        //Used to log the data stream. DO NOT IMPLEMENT TO PRODUCTION
        convertedPromotionMessage.print(Printed.<String, PromotionMessage>toSysOut().withLabel("== Uppercase PromoCode Stream == "));

        return convertedPromotionMessage;
    }

    private PromotionMessage convertPromoCodeToUppercase(PromotionMessage promotionMessage) {
        return new PromotionMessage(promotionMessage.getPromotionCode().toUpperCase());
    }
}
