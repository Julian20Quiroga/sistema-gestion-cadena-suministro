package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplierResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.SupplierRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRestMapper supplierRestMapper;
    private final CreateSupplierUseCase createSupplierUseCase;

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@RequestBody CreateSupplierRequest request){
        CreateSupplierCommand command = supplierRestMapper.toCommand(request);
        SupplierResponse response = supplierRestMapper.toResponse(createSupplierUseCase.create(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
