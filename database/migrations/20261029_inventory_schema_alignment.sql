ALTER TABLE warehouses
  ADD COLUMN latitude DECIMAL(10,7) NULL,
  ADD COLUMN longitude DECIMAL(10,7) NULL;

ALTER TABLE inventory
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE inventory_movements
  ADD COLUMN reason VARCHAR(500) NULL,
  ADD COLUMN reference VARCHAR(100) NULL,
  ADD COLUMN movement_status VARCHAR(30) NULL;