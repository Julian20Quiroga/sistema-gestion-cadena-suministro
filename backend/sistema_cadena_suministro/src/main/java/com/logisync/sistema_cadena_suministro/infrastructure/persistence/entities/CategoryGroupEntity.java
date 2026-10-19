package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category_groups")
@Getter
@Setter
public class CategoryGroupEntity extends BaseEntity {

    @Column(name = "category_group_name")
    private String name;
}
