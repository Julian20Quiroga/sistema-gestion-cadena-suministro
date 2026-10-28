package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;

import java.util.Map;

public record CreationValidationContext(
        PurchaseOrder order,
        Supplier supplier,
        Warehouse warehouse,
        Map<Integer, Product> productsById
) {}
