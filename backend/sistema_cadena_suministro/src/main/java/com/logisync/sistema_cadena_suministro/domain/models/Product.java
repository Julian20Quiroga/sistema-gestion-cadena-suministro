package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private Integer supplierId;
    private String sku;
    private String description;
    private String model;
    private Integer ram;
    private Integer storage;
    private String processor;
    private String color;
    private BigDecimal salePrice;
    private BigDecimal weightKg;
    private Integer reorderPoint;
    private SoftDeleteStatus status;
    private List<Category> categories = new ArrayList<>();

    public boolean isActive() {
        return status == SoftDeleteStatus.ACTIVE;
    }
}
