import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseManager {
private String DB_HOST;
private String DB_PORT;
private String DB_NAME;
private String DB_USER;
private String DB_PASSWORD;
private String DB_URL;
    
   
    private Connection conn;
    
    private void loadConfig() {
    Properties props = new Properties();

    try (InputStream input = getClass()
            .getClassLoader()
            .getResourceAsStream("config.properties")) {

        if (input == null) {
            System.out.println("config.properties not found! Using default values.");

            DB_HOST = "localhost";
            DB_PORT = "3306";
            DB_NAME = "billing_system";
            DB_USER = "root";
            DB_PASSWORD = "";

        } else {
            props.load(input);

            DB_HOST = props.getProperty("db.host");
            DB_PORT = props.getProperty("db.port");
            DB_NAME = props.getProperty("db.name");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
        }

        DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
                 "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    } catch (Exception e) {
        System.err.println("Failed to load config.properties");
        e.printStackTrace();
    }
}

    public DatabaseManager() {
    loadConfig();
    initializeDatabase();
}
    
    private void initializeDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First, create database if it doesn't exist
            createDatabaseIfNotExists();
            
            // Connect to the database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database successfully!");
            
            createTables();
            insertSampleData();
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            System.err.println("Please add mysql-connector-java-8.x.jar to classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error!");
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Username and password are correct");
            System.err.println("3. MySQL is accessible on " + DB_HOST + ":" + DB_PORT);
            e.printStackTrace();
        }
    }
    
    private void createDatabaseIfNotExists() throws SQLException {
        String dbUrlWithoutDb = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + 
                                "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        
        try (Connection tempConn = DriverManager.getConnection(dbUrlWithoutDb, DB_USER, DB_PASSWORD);
             Statement stmt = tempConn.createStatement()) {
            
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDbSql);
            System.out.println("Database '" + DB_NAME + "' is ready.");
        }
    }
    
    private void createTables() {
        String createProductsTable = 
            "CREATE TABLE IF NOT EXISTS products (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "price DECIMAL(10, 2) NOT NULL, " +
            "stock INT NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        String createInvoicesTable = 
            "CREATE TABLE IF NOT EXISTS invoices (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "customer_name VARCHAR(255) NOT NULL, " +
            "customer_phone VARCHAR(20), " +
            "date_time DATETIME NOT NULL, " +
            "subtotal DECIMAL(10, 2) NOT NULL, " +
            "tax DECIMAL(10, 2) NOT NULL, " +
            "grand_total DECIMAL(10, 2) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        String createInvoiceItemsTable = 
            "CREATE TABLE IF NOT EXISTS invoice_items (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "invoice_id INT NOT NULL, " +
            "product_id INT NOT NULL, " +
            "product_name VARCHAR(255) NOT NULL, " +
            "price DECIMAL(10, 2) NOT NULL, " +
            "quantity INT NOT NULL, " +
            "total DECIMAL(10, 2) NOT NULL, " +
            "FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE, " +
            "INDEX idx_invoice_id (invoice_id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProductsTable);
            stmt.execute(createInvoicesTable);
            stmt.execute(createInvoiceItemsTable);
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating tables!");
            e.printStackTrace();
        }
    }
    
    private void insertSampleData() {
        try {
            // Check if products already exist
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM products");
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Sample data already exists.");
                return; // Sample data already exists
            }
            
            String insertSql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            
            Object[][] sampleProducts = {
                {"Laptop", 899.99, 15},
                {"Wireless Mouse", 29.99, 50},
                {"Keyboard", 49.99, 35},
                {"Monitor 24\"", 199.99, 20},
                {"USB Cable", 9.99, 100},
                {"Headphones", 79.99, 40},
                {"Webcam", 59.99, 25},
                {"External SSD 1TB", 129.99, 30},
                {"Phone Charger", 19.99, 75},
                {"Laptop Stand", 39.99, 45},
                {"Wireless Keyboard", 69.99, 30},
                {"HDMI Cable", 14.99, 80},
                {"USB Hub", 24.99, 55},
                {"Desk Lamp", 34.99, 40},
                {"Ergonomic Mouse Pad", 19.99, 60}
            };
            
            for (Object[] product : sampleProducts) {
                pstmt.setString(1, (String) product[0]);
                pstmt.setDouble(2, (Double) product[1]);
                pstmt.setInt(3, (Integer) product[2]);
                pstmt.executeUpdate();
            }
            
            System.out.println("Sample data inserted successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample data!");
            e.printStackTrace();
        }
    }
    
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products!");
            e.printStackTrace();
        }
        
        return products;
    }
    
    public ArrayList<Product> searchProducts(String query) {
        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY name";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching products!");
            e.printStackTrace();
        }
        
        return products;
    }
    
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStock());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding product!");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateProduct(int productId, String name, double price, int stock) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating product!");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting product!");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateProductStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating stock!");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean saveInvoice(Invoice invoice) {
        String insertInvoice = 
            "INSERT INTO invoices (customer_name, customer_phone, date_time, " +
            "subtotal, tax, grand_total) VALUES (?, ?, ?, ?, ?, ?)";
        
        String insertItem = 
            "INSERT INTO invoice_items (invoice_id, product_id, product_name, " +
            "price, quantity, total) VALUES (?, ?, ?, ?, ?, ?)";
        
        String updateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
        
        try {
            conn.setAutoCommit(false);
            
            // Insert invoice
            PreparedStatement invoiceStmt = conn.prepareStatement(insertInvoice, 
                Statement.RETURN_GENERATED_KEYS);
            invoiceStmt.setString(1, invoice.getCustomerName());
            invoiceStmt.setString(2, invoice.getCustomerPhone());
            
            // Convert LocalDateTime to SQL Timestamp
            Timestamp timestamp = Timestamp.valueOf(invoice.getDateTime());
            invoiceStmt.setTimestamp(3, timestamp);
            
            invoiceStmt.setDouble(4, invoice.getSubtotal());
            invoiceStmt.setDouble(5, invoice.getTax());
            invoiceStmt.setDouble(6, invoice.getGrandTotal());
            invoiceStmt.executeUpdate();
            
            ResultSet rs = invoiceStmt.getGeneratedKeys();
            int invoiceId = 0;
            if (rs.next()) {
                invoiceId = rs.getInt(1);
                invoice.setId(invoiceId);
            }
            
            // Insert invoice items and update stock
            PreparedStatement itemStmt = conn.prepareStatement(insertItem);
            PreparedStatement stockStmt = conn.prepareStatement(updateStock);
            
            for (BillItem item : invoice.getItems()) {
                itemStmt.setInt(1, invoiceId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setString(3, item.getProductName());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.setInt(5, item.getQuantity());
                itemStmt.setDouble(6, item.getTotal());
                itemStmt.executeUpdate();
                
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getProductId());
                stockStmt.executeUpdate();
            }
            
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("Invoice saved successfully!");
            return true;
            
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error saving invoice!");
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<Invoice> getAllInvoices() {
        ArrayList<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY date_time DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                ArrayList<BillItem> items = getInvoiceItems(id);
                
                // Convert SQL Timestamp to LocalDateTime
                Timestamp timestamp = rs.getTimestamp("date_time");
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                
                invoices.add(new Invoice(
                    id,
                    rs.getString("customer_name"),
                    rs.getString("customer_phone"),
                    dateTime,
                    rs.getDouble("subtotal"),
                    rs.getDouble("tax"),
                    rs.getDouble("grand_total"),
                    items
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoices!");
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    private ArrayList<BillItem> getInvoiceItems(int invoiceId) {
        ArrayList<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(new BillItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoice items!");
            e.printStackTrace();
        }
        
        return items;
    }
    
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ArrayList<BillItem> items = getInvoiceItems(invoiceId);
                Timestamp timestamp = rs.getTimestamp("date_time");
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                
                return new Invoice(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_phone"),
                    dateTime,
                    rs.getDouble("subtotal"),
                    rs.getDouble("tax"),
                    rs.getDouble("grand_total"),
                    items
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoice!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get sales report
    public double getTotalSales() {
        String sql = "SELECT SUM(grand_total) as total FROM invoices";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    public int getTotalInvoicesCount() {
        String sql = "SELECT COUNT(*) as count FROM invoices";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}