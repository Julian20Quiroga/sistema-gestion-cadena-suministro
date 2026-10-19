package com.logisync.sistema_cadena_suministro.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Integer id;
    private String name;
    private Integer groupId;
}
