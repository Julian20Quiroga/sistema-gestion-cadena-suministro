ALTER TABLE detail_dispatches
  ADD COLUMN inventory_id INT NOT NULL,
  ADD COLUMN quantity INT NOT NULL,
  ADD CONSTRAINT fk_detail_dispatches_inventory
    FOREIGN KEY (inventory_id) REFERENCES inventory(id);
