package com.logisync.sistema_cadena_suministro.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailDispatch {
    private Integer id;
    private Integer dispatchId;
    private Integer detailOrderId;
    private Integer storageSpaceId;
    private Integer inventoryId;
    private int quantity;
}
