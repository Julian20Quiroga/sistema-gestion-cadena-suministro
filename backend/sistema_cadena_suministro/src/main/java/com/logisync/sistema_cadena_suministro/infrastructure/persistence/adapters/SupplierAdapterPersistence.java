package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.SupplierDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplierEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.SupplierPersistenceMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplierAdapterPersistence implements SupplierRepository {

    private final SupplierDAO supplierDAO;
    private final SupplierPersistenceMapper mapper;

    @Override
    public Supplier create(Supplier supplier) {
        SupplierEntity supplierEntity = mapper.toEntity(supplier);
        supplierEntity.setStatus(SoftDeleteStatus.ACTIVE);
        return mapper.toDomain(supplierDAO.save(supplierEntity));
    }

    @Override
    public Supplier update(Supplier supplier, Integer id) {
        SupplierEntity existingSupplier = supplierDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        existingSupplier.setNit(supplier.getNit());
        existingSupplier.setName(supplier.getName());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setPhone(supplier.getPhone());
        if (supplier.getStatus() != null) {
            existingSupplier.setStatus(supplier.getStatus());
        }

        return mapper.toDomain(supplierDAO.save(existingSupplier));
    }

    @Override
    public void delete(Integer id) {
        SupplierEntity existingSupplier = supplierDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        existingSupplier.setStatus(SoftDeleteStatus.DELETED);
        supplierDAO.save(existingSupplier);
    }

    @Override
    public Optional<Supplier> findById(Integer id) {
        return supplierDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Supplier> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery) {
        String pattern = "%" + (name == null ? "" : name.trim().toLowerCase(Locale.ROOT)) + "%";
        return PageSupport.toResult(supplierDAO.search(pattern, status, PageSupport.pageable(pageQuery)),
                mapper::toDomain);
    }
}
