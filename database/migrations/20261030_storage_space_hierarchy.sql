ALTER TABLE storage_spaces
  ADD COLUMN parent_id INT NULL,
  ADD COLUMN space_type ENUM('ZONE', 'RACK', 'LOCATION') NOT NULL DEFAULT 'ZONE',
  ADD CONSTRAINT fk_storage_spaces_parent
    FOREIGN KEY (parent_id) REFERENCES storage_spaces(id);
