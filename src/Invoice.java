/**
 * Invoice.java
 * Represents a complete invoice/bill
 * Place this file in: src/Invoice.java
 */

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Invoice {
    private int id;
    private String customerName;
    private String customerPhone;
    private LocalDateTime dateTime;
    private double subtotal;
    private double tax;
    private double grandTotal;
    private ArrayList<BillItem> items;
    
    /**
     * Constructor for Invoice
     * @param id Invoice ID
     * @param customerName Customer name
     * @param customerPhone Customer phone number
     * @param dateTime Date and time of invoice
     * @param subtotal Subtotal before tax
     * @param tax Tax amount
     * @param grandTotal Total amount including tax
     * @param items List of bill items
     */
    public Invoice(int id, String customerName, String customerPhone, 
                   LocalDateTime dateTime, double subtotal, double tax, 
                   double grandTotal, ArrayList<BillItem> items) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.dateTime = dateTime;
        this.subtotal = subtotal;
        this.tax = tax;
        this.grandTotal = grandTotal;
        this.items = new ArrayList<BillItem>(items);
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public String getCustomerName() { 
        return customerName; 
    }
    
    public String getCustomerPhone() { 
        return customerPhone; 
    }
    
    public LocalDateTime getDateTime() { 
        return dateTime; 
    }
    
    public double getSubtotal() { 
        return subtotal; 
    }
    
    public double getTax() { 
        return tax; 
    }
    
    public double getGrandTotal() { 
        return grandTotal; 
    }
    
    public ArrayList<BillItem> getItems() { 
        return items; 
    }
    
    // Setters
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public void setTax(double tax) {
        this.tax = tax;
    }
    
    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public void setItems(ArrayList<BillItem> items) {
        this.items = items;
    }
    
    /**
     * Get the total number of items in this invoice
     * @return total item count
     */
    public int getTotalItems() {
        int total = 0;
        for (BillItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", dateTime=" + dateTime +
                ", grandTotal=" + grandTotal +
                ", itemCount=" + items.size() +
                '}';
    }
}