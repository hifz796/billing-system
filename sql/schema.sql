-- Billing System Database Schema for MySQL 8.x
-- This file is optional - the application creates these tables automatically

-- Create database
CREATE DATABASE IF NOT EXISTS billing_system;
USE billing_system;

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    date_time DATETIME NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    tax DECIMAL(10, 2) NOT NULL,
    grand_total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_date_time (date_time),
    INDEX idx_customer_name (customer_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invoice Items Table
CREATE TABLE IF NOT EXISTS invoice_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Data
INSERT INTO products (name, price, stock) VALUES
    ('Laptop', 899.99, 15),
    ('Wireless Mouse', 29.99, 50),
    ('Keyboard', 49.99, 35),
    ('Monitor 24"', 199.99, 20),
    ('USB Cable', 9.99, 100),
    ('Headphones', 79.99, 40),
    ('Webcam', 59.99, 25),
    ('External SSD 1TB', 129.99, 30),
    ('Phone Charger', 19.99, 75),
    ('Laptop Stand', 39.99, 45),
    ('Wireless Keyboard', 69.99, 30),
    ('HDMI Cable', 14.99, 80),
    ('USB Hub', 24.99, 55),
    ('Desk Lamp', 34.99, 40),
    ('Ergonomic Mouse Pad', 19.99, 60)
ON DUPLICATE KEY UPDATE name=name;

-- Useful Queries for Reporting

-- Total sales
-- SELECT SUM(grand_total) as total_sales FROM invoices;

-- Sales by date
-- SELECT DATE(date_time) as date, SUM(grand_total) as daily_sales 
-- FROM invoices 
-- GROUP BY DATE(date_time) 
-- ORDER BY date DESC;

-- Most sold products
-- SELECT p.name, SUM(ii.quantity) as total_sold 
-- FROM invoice_items ii 
-- JOIN products p ON ii.product_id = p.id 
-- GROUP BY p.id, p.name 
-- ORDER BY total_sold DESC 
-- LIMIT 10;

-- Low stock alert
-- SELECT name, stock FROM products WHERE stock < 20 ORDER BY stock ASC;

-- Customer purchase history
-- SELECT customer_name, COUNT(*) as total_purchases, SUM(grand_total) as total_spent
-- FROM invoices
-- GROUP BY customer_name
-- ORDER BY total_spent DESC;