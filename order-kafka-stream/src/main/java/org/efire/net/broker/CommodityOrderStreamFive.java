package org.efire.net.broker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.efire.net.broker.message.OrderMessage;
import org.efire.net.broker.message.OrderPatternMessage;
import org.efire.net.broker.message.OrderRewardMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import static org.efire.net.broker.message.OrderPatternMessage.isPlastic;
import static org.efire.net.broker.message.OrderRewardMessage.isCheap;
import static org.efire.net.broker.message.OrderRewardMessage.isLargeQuantity;

/**
 * This example demonstrate using 'peek' and simulate calling an API or other process.
 * Fraud detection:  4th Sink
 *  - If an order location starts with 'C', call fraud API to send the order message.
 *  - Also, send message with Location as Key (masked) and Total Amount (quantity * price)
 */
@Configuration
@Slf4j
public class CommodityOrderStreamFive {

    @Bean
    public KStream<String, OrderMessage> commodityStream(StreamsBuilder builder) {
        var jsonSerde = new JsonSerde<>(OrderMessage.class);
        var patternJsonSerde = new JsonSerde<>(OrderPatternMessage.class);
        var rewardJsonSerde = new JsonSerde<>(OrderRewardMessage.class);
        var maskedOrderStream = builder
                .stream("t.commodity.order", Consumed.with(Serdes.String(), jsonSerde))
                .mapValues(OrderMessage::maskedCreditCardNumber);

        maskedOrderStream.print(Printed.<String, OrderMessage>toSysOut().withLabel("Mask Order Stream"));

        var patternStream = new KafkaStreamBrancher<String, OrderPatternMessage>()
                .branch(isPlastic(), kstream -> kstream.to("t.commodity.pattern-five.plastic",
                        Produced.with(Serdes.String(), patternJsonSerde)))
                .defaultBranch(kstream -> kstream.to("t.commodity.pattern-five.notplastic",
                        Produced.with(Serdes.String(), patternJsonSerde)))
                .onTopOf(maskedOrderStream.mapValues(OrderPatternMessage::build));

        //2nd Sink
        var rewardStream = maskedOrderStream
                .filter(isLargeQuantity())
                .filterNot(isCheap())
                .map(OrderRewardMessage.mapToOrderRewardChangeKey());
        rewardStream.to("t.commodity.reward-five", Produced.with(Serdes.String(), rewardJsonSerde));

        //3rd Sink
        maskedOrderStream.selectKey(OrderMessage.generateStorageKey())
                .to("t.commodity.storage-five", Produced.with(Serdes.String(), jsonSerde));

        //4th Sink
        var fraudStream = maskedOrderStream
                .filter((k, v) -> v.getLocation().toLowerCase().startsWith("c"))
                .peek((k, v) -> callAPI(v));

        fraudStream.print(Printed.<String, OrderMessage>toSysOut().withLabel("Fraud Order Stream"));

        fraudStream
                .map((k,v) -> KeyValue.pair(maskLocation(v), v.getQuantity() * v.getPrice()))
                .to("t.commodity.fraud-five", Produced.with(Serdes.String(), Serdes.Double()));

        return maskedOrderStream;
    }

    private String maskLocation(OrderMessage v) {
        return v.getLocation().toUpperCase().charAt(0)+"****";
    }

    private void callAPI(OrderMessage v) {
        log.info("Report as Fraud: "+ v);
    }
}
