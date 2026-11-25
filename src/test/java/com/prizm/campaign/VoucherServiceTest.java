package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Test
    void redeemActiveVoucherSucceeds() {
        RedeemResponse res = voucherService.redeem("RAYA-0001", "user-1");
        assertEquals("OK", res.getResult());
    }

    @Test
    void redeemAlreadyRedeemedVoucherFails() {
        RedeemResponse res = voucherService.redeem("RAYA-0004", "user-2");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemUnknownCodeFails() {
        RedeemResponse res = voucherService.redeem("NOPE-9999", "user-3");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemRejectsUserAtPerUserLimit() {
        assertEquals("OK", voucherService.redeem("RAYA-0002", "limit-user").getResult());
        assertEquals("OK", voucherService.redeem("RAYA-0006", "limit-user").getResult());

        RedeemResponse res = voucherService.redeem("RAYA-0003", "limit-user");
        assertEquals("FAILED", res.getResult());
        assertEquals("Per-user redemption limit reached", res.getMessage());
    }

    @Test
    void seededRedemptionsCountTowardPerUserLimit() {
        RedeemResponse res = voucherService.redeem("RAYA-0001", "user-99");
        assertEquals("FAILED", res.getResult());
        assertEquals("Per-user redemption limit reached", res.getMessage());
    }
}
