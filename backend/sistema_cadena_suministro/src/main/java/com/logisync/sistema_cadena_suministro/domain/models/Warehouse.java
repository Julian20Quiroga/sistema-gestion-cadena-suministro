package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private SoftDeleteStatus status;
    private Integer headquarterId;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public boolean isActive() {
        return status == SoftDeleteStatus.ACTIVE;
    }
}
