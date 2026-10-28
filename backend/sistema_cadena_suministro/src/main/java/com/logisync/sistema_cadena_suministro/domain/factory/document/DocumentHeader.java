package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.time.LocalDateTime;

public interface DocumentHeader {
    String getReference();
    String getDocumentType();
    LocalDateTime getIssueDate();
}
