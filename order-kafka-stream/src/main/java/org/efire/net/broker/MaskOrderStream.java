package org.efire.net.broker;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.efire.net.broker.message.OrderMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

//@Configuration
public class MaskOrderStream {

    @Bean
    KStream<String, OrderMessage> maskOrder(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<>(OrderMessage.class);

        var orderMessageKStream = builder.stream("t.commodity.order", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(OrderMessage::maskedCreditCardNumber);
        orderMessageKStream.to("t.commodity.order-masked", Produced.with(Serdes.String(), jsonSerde));
        //DO NOT use in PRODUCTION
        orderMessageKStream.print(Printed.<String, OrderMessage>toSysOut().withLabel("Masked Order Stream"));
        return orderMessageKStream;
    }
}
