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

import static org.efire.net.broker.message.OrderRewardMessage.isCheap;
import static org.efire.net.broker.message.OrderRewardMessage.isLargeQuantity;

//@Configuration
public class CommodityOrderStreamTwo {

    private static final int IS_PLASTIC = 0;
    private static final int NOT_PLASTIC = 1;

    @Bean
    public KStream<String, OrderMessage> commodityStream(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<>(OrderMessage.class);
        var patternJsonSerde = new JsonSerde<>(OrderPatternMessage.class);
        var rewardJsonSerde = new JsonSerde<>(OrderRewardMessage.class);
        var maskedOrderStream = builder
                .stream("t.commodity.order", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(OrderMessage::maskedCreditCardNumber);

        //1st Sink
        var patternStream = maskedOrderStream
                .mapValues(OrderPatternMessage::build)
                .branch(OrderPatternMessage.isPlastic(), (k, v) -> true);
        patternStream[IS_PLASTIC].to("t.commodity.pattern-two.plastic",
                Produced.with(Serdes.String(), patternJsonSerde));
        patternStream[NOT_PLASTIC].to("t.commodity.pattern-two.notplastic",
                Produced.with(Serdes.String(), patternJsonSerde));

        //2nd Sink
        var rewardStream = maskedOrderStream
                .filter(isLargeQuantity())
                .filterNot(isCheap())
                .mapValues(OrderRewardMessage::mapToOrderRewardMessage);
        rewardStream.to("t.commodity.reward-two", Produced.with(Serdes.String(), rewardJsonSerde));

        //3rd Sink
        maskedOrderStream.selectKey(OrderMessage.generateStorageKey())
                .to("t.commodity.storage-two", Produced.with(Serdes.String(), jsonSerde));

        return maskedOrderStream;
    }
}
