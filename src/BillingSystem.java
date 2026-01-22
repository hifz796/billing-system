/**
 * BillingSystem.java - Modern Dark Theme Version
 * Main application class for the Billing System
 * Place this file in: src/BillingSystem.java
 */

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BillingSystem extends JFrame {
    private JTable productTable, billTable;
    private DefaultTableModel productModel, billModel;
    private JTextField searchField, productNameField, productPriceField, productQtyField;
    private JTextField customerNameField, customerPhoneField;
    private JLabel totalLabel, taxLabel, grandTotalLabel;
    private ArrayList<BillItem> currentBill;
    private DatabaseManager dbManager;
    private double taxRate = 0.10; // 10% tax
    
    // Modern Dark Color Palette
    private static final Color PRIMARY_DARK = new Color(44, 62, 80);      // Dark blue-gray
    private static final Color SECONDARY_DARK = new Color(52, 73, 94);    // Medium dark
    private static final Color ACCENT_BLUE = new Color(41, 128, 185);     // Bright blue
    private static final Color ACCENT_GREEN = new Color(39, 174, 96);     // Green
    private static final Color ACCENT_RED = new Color(192, 57, 43);       // Dark red
    private static final Color ACCENT_TEAL = new Color(22, 160, 133);     // Teal
    private static final Color ACCENT_GRAY = new Color(127, 140, 141);    // Gray
    private static final Color BG_LIGHT = new Color(236, 240, 241);       // Light background
    
    public BillingSystem() {
        setTitle("Billing System - Point of Sale");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        dbManager = new DatabaseManager();
        currentBill = new ArrayList<BillItem>();
        
        initComponents();
        loadProducts();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel - Customer Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(PRIMARY_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("BILLING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        
        customerNameField = new JTextField(15);
        customerPhoneField = new JTextField(12);
        
        styleTextField(customerNameField);
        styleTextField(customerPhoneField);
        
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(createLabel("Customer Name:", Color.WHITE));
        topPanel.add(customerNameField);
        topPanel.add(createLabel("Phone:", Color.WHITE));
        topPanel.add(customerPhoneField);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Split into Products and Bill
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(8);
        
        // Left Panel - Product Management
        JPanel leftPanel = createProductPanel();
        
        // Right Panel - Current Bill
        JPanel rightPanel = createBillPanel();
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createProductPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SECONDARY_DARK, 2), 
            "Products",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            SECONDARY_DARK
        ));
        
        // Product Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BG_LIGHT);
        
        searchField = new JTextField(20);
        styleTextField(searchField);
        
        JButton searchBtn = createStyledButton("🔍 Search", ACCENT_BLUE);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });
        
        JButton refreshBtn = createStyledButton("🔄 Refresh", ACCENT_GRAY);
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProducts();
                searchField.setText("");
            }
        });
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        
        // Product Table
        String[] productCols = {"ID", "Product Name", "Price", "Stock"};
        productModel = new DefaultTableModel(productCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        productTable = new JTable(productModel);
        styleTable(productTable);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addToBill();
            }
        });
        
        JScrollPane productScroll = new JScrollPane(productTable);
        
        // Product Entry Form
        JPanel productFormPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        productFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productFormPanel.setBackground(BG_LIGHT);
        
        productNameField = new JTextField();
        productPriceField = new JTextField();
        productQtyField = new JTextField();
        
        styleTextField(productNameField);
        styleTextField(productPriceField);
        styleTextField(productQtyField);
        
        JButton addProductBtn = createStyledButton("➕ Add Product", ACCENT_GREEN);
        addProductBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        
        productFormPanel.add(new JLabel("Product Name:"));
        productFormPanel.add(productNameField);
        productFormPanel.add(new JLabel("Price:"));
        productFormPanel.add(productPriceField);
        productFormPanel.add(new JLabel("Stock Quantity:"));
        productFormPanel.add(productQtyField);
        productFormPanel.add(new JLabel());
        productFormPanel.add(addProductBtn);
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(productScroll, BorderLayout.CENTER);
        leftPanel.add(productFormPanel, BorderLayout.SOUTH);
        
        return leftPanel;
    }
    
    private JPanel createBillPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_TEAL, 2),
            "Current Bill",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            ACCENT_TEAL
        ));
        
        // Bill Table
        String[] billCols = {"Product", "Price", "Qty", "Total"};
        billModel = new DefaultTableModel(billCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        billTable = new JTable(billModel);
        styleTable(billTable);
        JScrollPane billScroll = new JScrollPane(billTable);
        
        // Bill Actions
        JPanel billActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        billActionsPanel.setBackground(BG_LIGHT);
        
        JButton addToBillBtn = createStyledButton("➕ Add Selected", ACCENT_BLUE);
        JButton removeItemBtn = createStyledButton("❌ Remove Item", ACCENT_RED);
        
        addToBillBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addToBill();
            }
        });
        removeItemBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFromBill();
            }
        });
        
        billActionsPanel.add(addToBillBtn);
        billActionsPanel.add(removeItemBtn);
        
        // Bill Summary
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        summaryPanel.setBackground(BG_LIGHT);
        
        totalLabel = new JLabel("Rs0.00");
        taxLabel = new JLabel("RS0.00");
        grandTotalLabel = new JLabel("RS0.00");
        
        Font summaryFont = new Font("Arial", Font.BOLD, 16);
        totalLabel.setFont(summaryFont);
        taxLabel.setFont(summaryFont);
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        grandTotalLabel.setForeground(ACCENT_GREEN);
        
        summaryPanel.add(createLabel("Subtotal:", SECONDARY_DARK));
        summaryPanel.add(totalLabel);
        summaryPanel.add(createLabel("Tax (10%):", SECONDARY_DARK));
        summaryPanel.add(taxLabel);
        summaryPanel.add(createLabel("GRAND TOTAL:", SECONDARY_DARK));
        summaryPanel.add(grandTotalLabel);
        
        JButton generateBillBtn = createStyledButton("📄 Generate Invoice", ACCENT_TEAL);
        JButton clearBillBtn = createStyledButton("🗑️ Clear Bill", ACCENT_GRAY);
        
        generateBillBtn.setFont(new Font("Arial", Font.BOLD, 14));
        
        generateBillBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateInvoice();
            }
        });
        clearBillBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearBill();
            }
        });
        
        summaryPanel.add(generateBillBtn);
        summaryPanel.add(clearBillBtn);
        
        rightPanel.add(billScroll, BorderLayout.CENTER);
        rightPanel.add(billActionsPanel, BorderLayout.NORTH);
        rightPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        return rightPanel;
    }
    
    // Helper method to create styled buttons
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    // Helper method to style text fields
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_DARK, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    // Helper method to style tables
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(SECONDARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(200, 200, 200));
    }
    
    private JLabel createLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        if (color != null) label.setForeground(color);
        return label;
    }
    
    private void loadProducts() {
        productModel.setRowCount(0);
        ArrayList<Product> products = dbManager.getAllProducts();
        for (Product p : products) {
            productModel.addRow(new Object[]{p.getId(), p.getName(), 
                String.format("RS%.2f", p.getPrice()), p.getStock()});
        }
    }
    
    private void searchProducts() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadProducts();
            return;
        }
        
        productModel.setRowCount(0);
        ArrayList<Product> products = dbManager.searchProducts(query);
        for (Product p : products) {
            productModel.addRow(new Object[]{p.getId(), p.getName(), 
                String.format("RS%.2f", p.getPrice()), p.getStock()});
        }
    }
    
    private void addProduct() {
        try {
            String name = productNameField.getText().trim();
            double price = Double.parseDouble(productPriceField.getText().trim());
            int stock = Integer.parseInt(productQtyField.getText().trim());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name required!");
                return;
            }
            
            Product product = new Product(0, name, price, stock);
            if (dbManager.addProduct(product)) {
                JOptionPane.showMessageDialog(this, "✅ Product added successfully!");
                productNameField.setText("");
                productPriceField.setText("");
                productQtyField.setText("");
                loadProducts();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid price or quantity!");
        }
    }
    
    private void addToBill() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        int productId = (int) productModel.getValueAt(row, 0);
        String productName = (String) productModel.getValueAt(row, 1);
        String priceStr = (String) productModel.getValueAt(row, 2);
        double price = Double.parseDouble(priceStr.replace("RS", ""));
        int stock = (int) productModel.getValueAt(row, 3);
        
        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "❌ Product out of stock!");
            return;
        }
        
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null) return;
        
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0 || qty > stock) {
                JOptionPane.showMessageDialog(this, "❌ Invalid quantity!");
                return;
            }
            
            BillItem item = new BillItem(productId, productName, price, qty);
            currentBill.add(item);
            
            billModel.addRow(new Object[]{
                productName, 
                String.format("RS%.2f", price),
                qty,
                String.format("RS%.2f", item.getTotal())
            });
            
            updateBillSummary();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid quantity!");
        }
    }
    
    private void removeFromBill() {
        int row = billTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
            return;
        }
        
        currentBill.remove(row);
        billModel.removeRow(row);
        updateBillSummary();
    }
    
    private void updateBillSummary() {
        double subtotal = 0;
        for (BillItem item : currentBill) {
            subtotal += item.getTotal();
        }
        
        double tax = subtotal * taxRate;
        double grandTotal = subtotal + tax;
        
        totalLabel.setText(String.format("RS%.2f", subtotal));
        taxLabel.setText(String.format("RS%.2f", tax));
        grandTotalLabel.setText(String.format("RS%.2f", grandTotal));
    }
    
    private void generateInvoice() {
        if (currentBill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ No items in bill!");
            return;
        }
        
        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();
        
        if (customerName.isEmpty()) {
            customerName = "Walk-in Customer";
        }
        
        double subtotal = Double.parseDouble(totalLabel.getText().replace("RS", ""));
        double tax = Double.parseDouble(taxLabel.getText().replace("RS", ""));
        double grandTotal = Double.parseDouble(grandTotalLabel.getText().replace("RS", ""));
        
        Invoice invoice = new Invoice(0, customerName, customerPhone, 
            LocalDateTime.now(), subtotal, tax, grandTotal, currentBill);
        
        if (dbManager.saveInvoice(invoice)) {
            showInvoice(invoice);
            clearBill();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to save invoice!");
        }
    }
    
    private void showInvoice(Invoice invoice) {
        JDialog dialog = new JDialog(this, "Invoice", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.WHITE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("                  INVOICE                       \n");
        sb.append("================================================\n\n");
        sb.append("Invoice #: ").append(invoice.getId()).append("\n");
        sb.append("Date: ").append(invoice.getDateTime()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Customer: ").append(invoice.getCustomerName()).append("\n");
        sb.append("Phone: ").append(invoice.getCustomerPhone()).append("\n\n");
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%-25s %5s %8s %10s\n", "ITEM", "QTY", "PRICE", "TOTAL"));
        sb.append("------------------------------------------------\n");
        
        for (BillItem item : invoice.getItems()) {
            sb.append(String.format("%-25s %5d RS%7.2f RS%9.2f\n",
                item.getProductName().substring(0, Math.min(25, item.getProductName().length())),
                item.getQuantity(),
                item.getPrice(),
                item.getTotal()));
        }
        
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%42s RS%9.2f\n", "Subtotal:", invoice.getSubtotal()));
        sb.append(String.format("%42s RS%9.2f\n", "Tax (10%):", invoice.getTax()));
        sb.append("================================================\n");
        sb.append(String.format("%42s RS%9.2f\n", "GRAND TOTAL:", invoice.getGrandTotal()));
        sb.append("================================================\n");
        sb.append("\n          Thank you for your business!\n");
        
        textArea.setText(sb.toString());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BG_LIGHT);
        
        JButton closeBtn = createStyledButton("✖️ Close", SECONDARY_DARK);
        JButton printBtn = createStyledButton("🖨️ Print", ACCENT_BLUE);
        
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        printBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Print failed!");
                }
            }
        });
        
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void clearBill() {
        currentBill.clear();
        billModel.setRowCount(0);
        customerNameField.setText("");
        customerPhoneField.setText("");
        updateBillSummary();
    }
    
    /**
     * Main method - Entry point of the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        BillingSystem app = new BillingSystem();
        app.setVisible(true);
    }
}