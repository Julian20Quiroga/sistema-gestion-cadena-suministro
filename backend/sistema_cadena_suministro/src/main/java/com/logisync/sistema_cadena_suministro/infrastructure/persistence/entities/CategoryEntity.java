package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity extends BaseEntity {

    @Column(name = "category_name")
    private String name;

    @Column(name = "category_type_id")
    private Integer groupId;
}
