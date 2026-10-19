package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.time.LocalDateTime;

public record PurchaseOrderHeader(String reference, Integer supplierId, LocalDateTime issueDate) implements DocumentHeader {
    @Override
    public String getReference() { return reference; }
    @Override
    public String getDocumentType() { return "PURCHASE_ORDER"; }
    @Override
    public LocalDateTime getIssueDate() { return issueDate; }
}
