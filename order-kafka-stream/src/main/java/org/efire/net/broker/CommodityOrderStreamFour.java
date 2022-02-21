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
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import static org.efire.net.broker.message.OrderPatternMessage.isPlastic;
import static org.efire.net.broker.message.OrderRewardMessage.isCheap;
import static org.efire.net.broker.message.OrderRewardMessage.isLargeQuantity;

//@Configuration
public class CommodityOrderStreamFour {

    @Bean
    public KStream<String, OrderMessage> commodityStream(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<>(OrderMessage.class);
        var patternJsonSerde = new JsonSerde<>(OrderPatternMessage.class);
        var rewardJsonSerde = new JsonSerde<>(OrderRewardMessage.class);
        var maskedOrderStream = builder
                .stream("t.commodity.order", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(OrderMessage::maskedCreditCardNumber);

        //1st Sink - *** This demonstrate another approach using KafkaStreamBrancher.
        // We eliminate the magic number(0,1) like we did in previous sample @CommodityOrderStreamTwo class
        // This is much readable

        var patternStream = new KafkaStreamBrancher<String, OrderPatternMessage>()
                .branch(isPlastic(), kstream -> kstream.to("t.commodity.pattern-four.plastic",
                        Produced.with(Serdes.String(), patternJsonSerde)))
                .defaultBranch(kstream -> kstream.to("t.commodity.pattern-four.notplastic",
                        Produced.with(Serdes.String(), patternJsonSerde)))
                .onTopOf(maskedOrderStream.mapValues(OrderPatternMessage::build));

        //2nd Sink
        var rewardStream = maskedOrderStream
                .filter(isLargeQuantity())
                .filterNot(isCheap())
                .map(OrderRewardMessage.mapToOrderRewardChangeKey());
        rewardStream.to("t.commodity.reward-four", Produced.with(Serdes.String(), rewardJsonSerde));

        //3rd Sink
        maskedOrderStream.selectKey(OrderMessage.generateStorageKey())
                .to("t.commodity.storage-four", Produced.with(Serdes.String(), jsonSerde));

        return maskedOrderStream;
    }
}
