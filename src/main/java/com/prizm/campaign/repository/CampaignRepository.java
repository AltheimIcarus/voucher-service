package com.prizm.campaign.repository;

import com.prizm.campaign.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByClientCode(String clientCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Campaign> findWithLockById(Long id);
}
