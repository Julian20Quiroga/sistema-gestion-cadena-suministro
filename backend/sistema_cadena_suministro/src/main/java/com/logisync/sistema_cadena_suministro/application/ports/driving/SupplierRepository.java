package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.Supplier;

public interface SupplierRepository {
    Supplier create(Supplier supplier);
}
