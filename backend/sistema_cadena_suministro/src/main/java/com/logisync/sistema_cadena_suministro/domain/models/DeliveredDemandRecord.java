package com.logisync.sistema_cadena_suministro.domain.models;

import java.time.LocalDateTime;

/** A quantity of a product from a completed customer order. */
public record DeliveredDemandRecord(Integer productId, LocalDateTime deliveredOrderDate, Integer quantity) {}
