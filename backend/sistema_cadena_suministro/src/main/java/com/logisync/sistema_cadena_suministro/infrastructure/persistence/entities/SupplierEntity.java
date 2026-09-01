package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter @Setter
public class SupplierEntity extends BaseEntity{

    @Column(name = "nit")
    private String nit;

    @Column(name = "supplier_name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "supplier_status")
    @Enumerated(EnumType.STRING)
    private SoftDeleteStatus status;

    @Override
    public String toString() {
        return "SupplierEntity{" +
                "nit='" + nit + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
