package com.buildstore.accounting.repository;

import com.buildstore.accounting.model.AccountingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, UUID> {
    List<AccountingEntry> findByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);
}
