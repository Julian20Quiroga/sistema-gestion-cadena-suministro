package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageSupplyNeedUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplyNeedResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.PurchaseOrderRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supply-needs")
@Tag(name = "Supply Needs", description = "Automated and manual supply needs triggered by low stock")
@RequiredArgsConstructor
public class SupplyNeedController {

    private final ManageSupplyNeedUseCase manageSupplyNeedUseCase;
    private final PurchaseOrderRestMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "List supply needs")
    public ResponseEntity<PageResponse<SupplyNeedResponse>> findAll(
            @RequestParam(name = "status", required = false) SupplyNeedStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.from(
                manageSupplyNeedUseCase.findAll(status, PageQuery.of(page, size)),
                mapper::toSupplyNeedResponse));
    }

    @PostMapping("/{id}/dismiss")
    @PreAuthorize("hasRole('PRESIDENT')")
    @Operation(summary = "Dismiss supply need")
    public ResponseEntity<SupplyNeedResponse> dismiss(@PathVariable("id") Integer id) {
        var res = manageSupplyNeedUseCase.dismiss(id);
        return ResponseEntity.ok(mapper.toSupplyNeedResponse(res));
    }
}
