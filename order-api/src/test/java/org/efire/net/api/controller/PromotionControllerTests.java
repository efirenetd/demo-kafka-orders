package org.efire.net.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.efire.net.api.dto.DiscountRequest;
import org.efire.net.service.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(PromotionController.class)
public class PromotionControllerTests {

    public static final String PROMO_123 = "PROMO123";
    public static final String PROMO_SUCCESSFULLY_POSTED = "Promo successfully posted.";
    public static final String PROMO_NOT_POSTED = "Promo not posted.";
    public static final String DISC_ABC = "DISCABC";
    public static final String DISC_SUCCESSFULLY_POSTED = "Discount successfully posted.";
    public static final String DISC_NOT_POSTED = "Discount not posted.";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PromotionService promotionService;
    private String jsonPromoRequest;
    private String jsonDiscountRequest;

    @BeforeEach
    void setUp() {
        jsonPromoRequest =  "{\"promoCode\": \""+ PROMO_123 + "\"}";
        jsonDiscountRequest = "{\"discountCode\": \""+ DISC_ABC + "\",\"percentValue\": 20 }";
    }

    @Test
    void shouldBeAbleToApplyPromo() throws Exception {
        given(this.promotionService.addPromotion(PROMO_123)).willReturn(Boolean.TRUE);

        var mvcResult = mockMvc.perform(post("/v1/api/promotions/promo")
                .content(jsonPromoRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(PROMO_SUCCESSFULLY_POSTED);
    }

    @Test
    void shouldNotApplyPromo() throws Exception {
        given(this.promotionService.addPromotion(PROMO_123)).willReturn(Boolean.FALSE);

        var mvcResult = mockMvc.perform(post("/v1/api/promotions/promo")
                .content(jsonPromoRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(PROMO_NOT_POSTED);
    }

    @Test
    void shouldAbleToApplyDiscount() throws Exception {
        var objectMapper = new ObjectMapper();
        var discountRequest = objectMapper.readValue(jsonDiscountRequest, DiscountRequest.class);

        given(this.promotionService.addDiscount(discountRequest)).willReturn(Boolean.TRUE);

        var mvcResult = mockMvc.perform(post("/v1/api/promotions/discount")
                .content(jsonDiscountRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(DISC_SUCCESSFULLY_POSTED);
    }

    @Test
    void shouldNotApplyDiscount() throws Exception {
        given(this.promotionService.addDiscount(any())).willReturn(Boolean.FALSE);

        var mvcResult = mockMvc.perform(post("/v1/api/promotions/discount")
                .content(jsonDiscountRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(DISC_NOT_POSTED);
    }
}
