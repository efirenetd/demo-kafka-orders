package org.efire.net.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.efire.net.broker.message.PromotionMessage;
import org.springframework.context.annotation.Bean;

//@Configuration
public class UppercaseStream {

    @Bean
    public KStream<String, String> topology(StreamsBuilder builder) {

        var sourceStream = builder.stream("t.commodity.promotion"
                , Consumed.with(Serdes.String(), Serdes.String()));
        var promotionUpperCaseStream = sourceStream.mapValues(this::convertToUpperCase);
        //send message to topic
        promotionUpperCaseStream.to("t.commodity.promotion-uppercase");

        //Used to log the data stream. DO NOT IMPLEMENT TO PRODUCTION
        sourceStream.print(Printed.<String, String>toSysOut().withLabel("== Original Stream"));
        promotionUpperCaseStream.print(Printed.<String, String>toSysOut().withLabel("== Uppercase Stream"));

        return sourceStream;
    }

    private String convertToUpperCase(String message) {
        var mapper = new ObjectMapper();
        try {
            var origPromotionMessage = mapper.readValue(message, PromotionMessage.class);
            var convertedPromotionMessage = new PromotionMessage(origPromotionMessage.getPromotionCode().toUpperCase());
            return mapper.writeValueAsString(convertedPromotionMessage);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("error converting");
        }
    }
}
