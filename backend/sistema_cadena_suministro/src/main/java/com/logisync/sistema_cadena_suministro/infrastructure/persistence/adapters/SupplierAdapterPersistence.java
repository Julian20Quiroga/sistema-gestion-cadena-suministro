package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.SupplierDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplierEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.SupplierPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
