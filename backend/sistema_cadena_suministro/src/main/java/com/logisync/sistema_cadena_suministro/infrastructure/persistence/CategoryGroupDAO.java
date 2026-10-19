package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.CategoryGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryGroupDAO extends JpaRepository<CategoryGroupEntity, Integer> {
}
