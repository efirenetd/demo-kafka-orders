package org.efire.net.broker;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.efire.net.broker.message.OrderMessage;
import org.efire.net.broker.message.OrderPatternMessage;
import org.efire.net.broker.message.OrderRewardMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

//@Configuration
public class CommodityOrderStreamOne {

    @Bean
    public KStream<String, OrderMessage> commodityStream(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<>(OrderMessage.class);
        var patternJsonSerde = new JsonSerde<>(OrderPatternMessage.class);
        var rewardJsonSerde = new JsonSerde<>(OrderRewardMessage.class);
        var maskedOrderStream = builder
                .stream("t.commodity.order", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(OrderMessage::maskedCreditCardNumber);

        //1st Sink
        maskedOrderStream.mapValues(OrderPatternMessage::build)
                .to("t.commodity.pattern-one", Produced.with(Serdes.String(), patternJsonSerde));

        //2nd Sink
        var rewardStream = maskedOrderStream
                .filter(OrderRewardMessage.isLargeQuantity())
                .mapValues(OrderRewardMessage::mapToOrderRewardMessage);
        rewardStream.to("t.commodity.reward-one", Produced.with(Serdes.String(), rewardJsonSerde));

        //3rd Sink
        maskedOrderStream.to("t.commodity.storage-one", Produced.with(Serdes.String(), jsonSerde));

        return maskedOrderStream;
    }
}
