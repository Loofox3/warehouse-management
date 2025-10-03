package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements Serializable {
    private int id;
    private List<InvoiceItem> items;
    private String type; // ДОБАВИТЬ: "ORDER", "TRANSFER", "SALE"
    private String status; // ДОБАВИТЬ: "CREATED", "PROCESSING", "COMPLETED"
    
    public Invoice(int id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.status = "CREATED"; // значение по умолчанию
    }
    
    public void addItem(Product product, int quantity) {
        this.items.add(new InvoiceItem(product, quantity));
    }
    
    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public List<InvoiceItem> getItems() { return items; }
    
    // ДОБАВИТЬ новые геттеры и сеттеры
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}