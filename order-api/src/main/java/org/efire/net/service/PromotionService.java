package org.efire.net.service;

import org.efire.net.api.dto.DiscountRequest;
import org.efire.net.broker.message.DiscountMessage;
import org.efire.net.broker.message.PromotionMessage;
import org.efire.net.broker.producer.DiscountProducer;
import org.efire.net.broker.producer.PromotionProducer;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    private PromotionProducer promotionProducer;
    private DiscountProducer discountProducer;

    public PromotionService(PromotionProducer promotionProducer, DiscountProducer discountProducer) {
        this.promotionProducer = promotionProducer;
        this.discountProducer = discountProducer;
    }

    public Boolean addPromotion(String code) {
        var promotionMessage = new PromotionMessage(code);
        var listenableFuture = promotionProducer.handleMessage(promotionMessage);

        if (listenableFuture.isDone()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean addDiscount(DiscountRequest discountRequest) {
        var discountMessage = new DiscountMessage(discountRequest.getDiscountCode(), discountRequest.getPercentValue());
        var listenableFuture = discountProducer.handleMessage(discountMessage);
        if (listenableFuture.isDone()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
