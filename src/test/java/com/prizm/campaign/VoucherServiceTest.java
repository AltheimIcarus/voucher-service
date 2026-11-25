package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.model.Campaign;
import com.prizm.campaign.repository.CampaignRepository;
import com.prizm.campaign.service.VoucherService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CampaignRepository campaignRepository;

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

    @Test
    void existingCampaignUsesDefaultPerUserLimit() {
        Campaign campaign = campaignRepository.findById(1L).orElseThrow();
        assertEquals(2, campaign.getPerUserLimit());
    }

    @Test
    void campaignCanDefineCustomPerUserLimit() {
        Campaign campaign = campaignRepository.findById(2L).orElseThrow();
        campaign.setPerUserLimit(1);
        campaignRepository.saveAndFlush(campaign);

        assertEquals("OK",
                voucherService.redeem("MRDK-0001", "custom-limit-user").getResult());
        assertEquals("Per-user redemption limit reached",
                voucherService.redeem("MRDK-0002", "custom-limit-user").getMessage());
    }

    @Test
    void campaignRejectsNonPositivePerUserLimit() {
        Campaign campaign = campaignRepository.findById(2L).orElseThrow();
        campaign.setPerUserLimit(0);

        assertThrows(ConstraintViolationException.class,
                () -> campaignRepository.saveAndFlush(campaign));
    }
}
