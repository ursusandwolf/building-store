package com.buildstore.pricing.repository;

import com.buildstore.pricing.model.PriceListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceListItem, Long> {

    Optional<PriceListItem> findByPriceListActiveTrueAndPriceListStartDateBeforeAndPriceListEndDateAfterOrPriceListEndDateIsNullAndProductId(
            Instant nowStart, Instant nowEnd, Long productId
    );
}
