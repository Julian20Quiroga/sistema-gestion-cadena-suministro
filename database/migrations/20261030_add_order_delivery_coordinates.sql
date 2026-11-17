ALTER TABLE orders
  ADD COLUMN delivery_latitude DECIMAL(10,7) NULL,
  ADD COLUMN delivery_longitude DECIMAL(10,7) NULL;
