package com.buildstore.order.repository;

import com.buildstore.order.model.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
}
