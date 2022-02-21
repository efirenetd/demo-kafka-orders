package org.efire.net.broker;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.efire.net.broker.message.PromotionMessage;
import org.efire.net.serde.PromotionJsonSerde;
import org.springframework.context.annotation.Bean;

//@Configuration
public class UppercaseCustomJsonSerdeStream {

    @Bean
    public KStream<String, PromotionMessage> topology(StreamsBuilder builder) {
        var jsonSerde = new PromotionJsonSerde();

        var convertedPromotionMessage = builder
                .stream("t.commodity.promotion", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(this::convertPromoCodeToUppercase);

        //send to topic
        convertedPromotionMessage.to("t.commodity.promotion-uppercase", Produced.with(Serdes.String(), jsonSerde));

        //Used to log the data stream. DO NOT IMPLEMENT IN PRODUCTION
        convertedPromotionMessage.print(Printed.<String, PromotionMessage>toSysOut().withLabel("== Uppercase PromoCode Stream using Custom Serde == "));

        return convertedPromotionMessage;
    }

    private PromotionMessage convertPromoCodeToUppercase(PromotionMessage promotionMessage) {
        return new PromotionMessage(promotionMessage.getPromotionCode().toUpperCase());
    }
}
