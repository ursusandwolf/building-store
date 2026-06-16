package com.buildstore.pricing.repository;

import com.buildstore.pricing.model.PriceList;
import com.buildstore.pricing.model.PriceListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
}
