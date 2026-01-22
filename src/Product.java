/**
 * Product.java
 * Represents a product in the billing system
 * Place this file in: src/Product.java
 */

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    
    /**
     * Constructor for Product
     * @param id Product ID
     * @param name Product name
     * @param price Product price
     * @param stock Stock quantity
     */
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getStock() { 
        return stock; 
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}