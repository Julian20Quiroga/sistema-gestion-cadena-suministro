package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public class Supplier {
    Integer id;
    String nit;
    String name;
    String phone;
    String email;
    SoftDeleteStatus status;

    public Supplier(Integer id, String nit, String name, String phone, String email, SoftDeleteStatus status) {
        this.id = id;
        this.nit = nit;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getNit() {
        return nit;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public SoftDeleteStatus getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", nit='" + nit + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
