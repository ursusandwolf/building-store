package com.buildstore.warehouse.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.warehouse.dto.WarehouseRequest;
import com.buildstore.warehouse.dto.WarehouseResponse;
import com.buildstore.warehouse.mapper.WarehouseMapper;
import com.buildstore.warehouse.model.Warehouse;
import com.buildstore.warehouse.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        log.info("Creating warehouse with code: {}", request.code());
        if (warehouseRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException("Warehouse with code " + request.code() + " already exists");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(saved);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        log.info("Updating warehouse ID: {}, code: {}", id, request.code());
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + id));

        if (!warehouse.getCode().equals(request.code())) {
            if (warehouseRepository.findByCode(request.code()).isPresent()) {
                throw new IllegalArgumentException("Warehouse with code " + request.code() + " already exists");
            }
        }

        warehouseMapper.updateEntityFromRequest(request, warehouse);
        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        log.info("Fetching all warehouses");
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        log.info("Fetching warehouse ID: {}", id);
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + id));
    }
}
