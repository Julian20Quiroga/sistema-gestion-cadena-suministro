CREATE SCHEMA IF NOT EXISTS sistema_cadena_suministro;
USE sistema_cadena_suministro;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    user_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    user_role ENUM('PRESIDENT', 'BRANCH_ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE headquarters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    headquarters_name VARCHAR(100) NOT NULL unique,
    address VARCHAR(255) NOT NULL unique,
    phone VARCHAR(20) NOT NULL unique,
    headquarter_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_headquarters_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);
CREATE TABLE warehouses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL unique,
    address VARCHAR(255) NOT NULL unique,
    phone VARCHAR(20) NOT NULL unique,
    warehouse_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    headquarter_id INT NOT NULL,
    latitude DECIMAL(9,6) NULL,
    longitude DECIMAL(9,6) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_warehouses_headquarter
        FOREIGN KEY (headquarter_id)
        REFERENCES headquarters(id)
);
CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL UNIQUE,
    nit VARCHAR(10) NOT NULL UNIQUE,
    phone VARCHAR(12) NOT NULL UNIQUE,
    email VARCHAR(30) NOT NULL UNIQUE,
    supplier_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    supplier_id INT NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    product_description VARCHAR(500),
    model VARCHAR(100),
    ram INT,
    product_storage INT,
    processor VARCHAR(100),
    color VARCHAR(50),
    sale_price DECIMAL(12,2) NOT NULL,
    weight_kg DECIMAL(10,3) NOT NULL DEFAULT 0.001,
    reorder_point INT NOT NULL DEFAULT 0,
    product_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id)
);
CREATE TABLE storage_spaces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    storage_space_name VARCHAR(100) NOT NULL,
    warehouse_id INT NOT NULL,
    capacity INT NOT NULL,
    storage_space_status ENUM('ACTIVE', 'DELETED') NOT NULL,
    parent_id INT NULL,
    space_type ENUM('ZONE', 'RACK', 'LOCATION') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_storage_spaces_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_storage_spaces_parent
        FOREIGN KEY (parent_id)
        REFERENCES storage_spaces(id)
);
CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    physical_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL,
    storage_space_id INT NOT NULL,
    product_id INT NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_inventory_space_product UNIQUE (storage_space_id, product_id),
    CONSTRAINT fk_inventory_storage_space
        FOREIGN KEY (storage_space_id)
        REFERENCES storage_spaces(id),
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);
CREATE TABLE inventory_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movement_type ENUM('ENTRADA', 'SALIDA', 'AJUSTE', 'TRASLADO') NOT NULL,
    movement_date TIMESTAMP NOT NULL,
    reason VARCHAR(255) NULL,
    reference VARCHAR(100) NULL,
    movement_status ENUM('APPLIED', 'REVERSED') NOT NULL DEFAULT 'APPLIED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE detail_inventory_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    reserved_delta INT NOT NULL DEFAULT 0,
    inventory_movement_id INT NOT NULL,
    inventory_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_inventory_movement_movement
        FOREIGN KEY (inventory_movement_id)
        REFERENCES inventory_movements(id),
    CONSTRAINT fk_detail_inventory_movement_inventory
        FOREIGN KEY (inventory_id)
        REFERENCES inventory(id)
);
CREATE TABLE category_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_group_name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    category_type_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_category_group
        FOREIGN KEY (category_type_id)
        REFERENCES category_groups(id)
);
CREATE TABLE products_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_categories_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_products_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);
CREATE TABLE purchase_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status ENUM('REGISTRADA', 'RECIBIDA_PARCIAL', 'RECIBIDA', 'ANULADA') NOT NULL,
    warehouse_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_purchase_orders_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),
    CONSTRAINT fk_purchase_orders_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id)
);
CREATE TABLE detail_purchase_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    purchase_order_id INT NOT NULL,
    quantity INT NOT NULL,
    received_quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_purchase_orders_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_detail_purchase_orders_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id)
);
CREATE TABLE supply_needs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    requested_quantity INT NOT NULL,
    origin ENUM('LOW_STOCK', 'FORECAST', 'MANUAL') NOT NULL,
    status ENUM('OPEN', 'COVERED', 'DISMISSED') NOT NULL DEFAULT 'OPEN',
    purchase_order_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_supply_needs_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_supply_needs_purchase_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_reference VARCHAR(100) NOT NULL UNIQUE,
    delivery_address VARCHAR(255) NOT NULL,
    delivery_city VARCHAR(100) NOT NULL,
    delivery_country VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    delivery_latitude DECIMAL(9,6) NULL,
    delivery_longitude DECIMAL(9,6) NULL,
    order_status ENUM('REGISTRADO', 'PREPARACION', 'DESPACHADO', 'ENTREGADO', 'ANULADO') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE detail_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_orders_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_detail_orders_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plate VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    capacity_kg DECIMAL(10,2) NOT NULL,
    average_speed_kmh DECIMAL(5,2) NOT NULL DEFAULT 40.00,
    api_key_hash VARCHAR(128) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE dispatches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    warehouses_id INT NOT NULL,
    vehicle_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('PENDIENTE', 'PREPARANDO', 'EN_TRANSPORTE', 'ENTREGADO', 'CANCELADO') NOT NULL,
    CONSTRAINT fk_dispatches_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id),
    CONSTRAINT fk_dispatches_warehouse
        FOREIGN KEY (warehouses_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_dispatches_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(id)
);
CREATE TABLE detail_dispatches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dispatch_id INT NOT NULL,
    detail_order_id INT NOT NULL unique,
    storage_space_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_dispatches_dispatch
        FOREIGN KEY (dispatch_id)
        REFERENCES dispatches(id),
    CONSTRAINT fk_detail_dispatches_detail_order
        FOREIGN KEY (detail_order_id)
        REFERENCES detail_orders(id),
    CONSTRAINT fk_detail_dispatches_storage_space
        FOREIGN KEY (storage_space_id)
        REFERENCES storage_spaces(id)
);
CREATE TABLE shipment_tracking (
    id INT AUTO_INCREMENT PRIMARY KEY,
    shipment_tracking_status ENUM('PENDIENTE', 'PREPARANDO', 'EN_TRANSPORTE', 'ENTREGADO', 'CANCELADO') NOT NULL,
    dispatch_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipment_tracking_dispatch
        FOREIGN KEY (dispatch_id)
        REFERENCES dispatches(id)
);