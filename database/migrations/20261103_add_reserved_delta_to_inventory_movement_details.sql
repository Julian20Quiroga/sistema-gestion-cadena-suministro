ALTER TABLE detail_inventory_movements
  ADD COLUMN reserved_delta INT NOT NULL DEFAULT 0;
