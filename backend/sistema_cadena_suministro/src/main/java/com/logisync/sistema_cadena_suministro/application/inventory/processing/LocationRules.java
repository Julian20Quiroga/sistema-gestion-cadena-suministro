package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StoredItemsReader;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Reusable validations over products and locations shared by the movement processors. */
@Component
@RequiredArgsConstructor
public class LocationRules {

    private final ProductRepository productRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final StoredItemsReader storedItemsReader;

    public void requireActiveProduct(Integer productId) {
        boolean active = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId))
                .isActive();
        if (!active) {
            throw new BusinessRuleException("Product " + productId + " is not active");
        }
    }

    public StorageSpace requireLocation(Integer spaceId) {
        StorageSpace space = storageSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Storage space", spaceId));
        if (!space.isActive() || space.getType() != StorageSpaceType.LOCATION) {
            throw new BusinessRuleException("Storage space " + spaceId + " is not an active LOCATION");
        }
        return space;
    }

    public void requireCapacityFor(StorageSpace location, int units) {
        long free = location.getCapacity() - storedItemsReader.unitsInSpace(location.getId());
        if (units > free) {
            throw new BusinessRuleException("Location " + location.getId() + " has only " + free
                    + " free units, requested " + units);
        }
    }

    public static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be greater than zero");
        }
    }
}
