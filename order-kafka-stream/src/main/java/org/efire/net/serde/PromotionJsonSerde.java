package org.efire.net.serde;

import org.efire.net.broker.message.PromotionMessage;

public class PromotionJsonSerde extends CustomJsonSerde<PromotionMessage> {

    public PromotionJsonSerde() {
        super(new CustomJsonSerializer<>(), new CustomJsonDeserializer<>(PromotionMessage.class));
    }
}
