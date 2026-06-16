package com.buildstore.supplier.service;

import com.buildstore.supplier.dto.SupplierRequest;
import com.buildstore.supplier.dto.SupplierResponse;
import com.buildstore.supplier.mapper.SupplierMapper;
import com.buildstore.supplier.model.Supplier;
import com.buildstore.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Supplier with name " + request.name() + " already exists");
        }
        Supplier supplier = supplierMapper.toEntity(request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }
}
