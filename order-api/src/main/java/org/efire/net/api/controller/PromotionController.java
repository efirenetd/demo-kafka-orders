package org.efire.net.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.efire.net.api.dto.DiscountRequest;
import org.efire.net.api.dto.PromoRequest;
import org.efire.net.service.PromotionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/promotions")
@Api(tags = "Promotions API", value = "PromotionsAPI")
public class PromotionController {

    private PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping(value = "/promo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Apply a Promo")
    public ResponseEntity<String> applyPromo(@RequestBody PromoRequest promoRequest) {
        var response = promotionService.addPromotion(promoRequest.getPromoCode());
        if (response.equals(Boolean.FALSE)) {
            return ResponseEntity.badRequest().body("Promo not posted.");
        }
        return ResponseEntity.ok("Promo successfully posted.");
    }

    @PostMapping(value = "/discount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Apply a Discount")
    public ResponseEntity<String> applyDiscount(@RequestBody DiscountRequest discountRequest) {
        var response = promotionService.addDiscount(discountRequest);
        if (response.equals(Boolean.FALSE)) {
            return ResponseEntity.badRequest().body("Discount not posted.");
        }
        return ResponseEntity.ok("Discount successfully posted.");
    }
}
