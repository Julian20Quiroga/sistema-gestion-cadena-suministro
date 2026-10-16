package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.mappers.SupplierApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    SupplierRepository supplierRepository;

    @Test
    void findByIdReturnsMappedSupplier() {
        SupplierApplicationMapper mapper = Mappers.getMapper(SupplierApplicationMapper.class);
        SupplierService service = new SupplierService(mapper, supplierRepository);
        when(supplierRepository.findById(1))
                .thenReturn(Optional.of(new Supplier(1, "123", "Acme", "300", "a@b.co", SoftDeleteStatus.ACTIVE)));

        assertEquals("Acme", service.findById(1).name());
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        SupplierService service = new SupplierService(mock(SupplierApplicationMapper.class), supplierRepository);
        when(supplierRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9));
    }
}
