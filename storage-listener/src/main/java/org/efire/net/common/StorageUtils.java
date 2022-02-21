package org.efire.net.common;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.kafka.common.header.Headers;
import org.efire.net.broker.message.DiscountMessage;
import org.efire.net.broker.message.PromotionMessage;

public class StorageUtils {
    private StorageUtils() {
    }

    static JavaType promoType = TypeFactory.defaultInstance().constructType(PromotionMessage.class);
    static JavaType discountType = TypeFactory.defaultInstance().constructType(DiscountMessage.class);

    public static final JavaType promoOrDiscount(byte[] data, Headers headers) {
        var jsonString = new String(data);
        if (jsonString.contains("\"promotionCode\"")) {
            return promoType;
        }
        else {
            return discountType;
        }
    }
}
