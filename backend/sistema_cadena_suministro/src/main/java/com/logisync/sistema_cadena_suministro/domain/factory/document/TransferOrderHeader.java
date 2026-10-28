package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.time.LocalDateTime;

public record TransferOrderHeader(String reference, Integer sourceWarehouseId, Integer targetWarehouseId, LocalDateTime issueDate) implements DocumentHeader {
    @Override
    public String getReference() { return reference; }
    @Override
    public String getDocumentType() { return "INTERNAL_TRANSFER"; }
    @Override
    public LocalDateTime getIssueDate() { return issueDate; }
}
