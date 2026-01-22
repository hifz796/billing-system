/**
 * BillItem.java
 * Represents an item in a bill/invoice
 * Place this file in: src/BillItem.java
 */

public class BillItem {
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    
    /**
     * Constructor for BillItem
     * @param productId ID of the product
     * @param productName Name of the product
     * @param price Price per unit
     * @param quantity Quantity purchased
     */
    public BillItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    
    // Getters
    public int getProductId() { 
        return productId; 
    }
    
    public String getProductName() { 
        return productName; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    /**
     * Calculate total price for this item
     * @return price * quantity
     */
    public double getTotal() { 
        return price * quantity; 
    }
    
    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "BillItem{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", total=" + getTotal() +
                '}';
    }
}
