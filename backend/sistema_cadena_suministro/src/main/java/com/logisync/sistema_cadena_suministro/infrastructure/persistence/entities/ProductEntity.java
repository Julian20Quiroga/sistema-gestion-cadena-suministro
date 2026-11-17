package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity extends BaseEntity {

    @Column(name = "product_name")
    private String name;

    @Column(name = "supplier_id", nullable = true)
    private Integer supplierId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "product_description")
    private String description;

    @Column(name = "model")
    private String model;

    @Column(name = "ram")
    private Integer ram;

    @Column(name = "product_storage")
    private Integer storage;

    @Column(name = "processor")
    private String processor;

    @Column(name = "color")
    private String color;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(name = "product_status")
    @Enumerated(EnumType.STRING)
    private SoftDeleteStatus status;

    @ManyToMany
    @JoinTable(name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @BatchSize(size = 50)
    private Set<CategoryEntity> categories = new HashSet<>();
}

