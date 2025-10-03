package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice {
    private int id;
    private Date date;
    private InvoiceStatus status;
    private List<InvoiceItem> items;

    public Invoice(int id) {
        this.id = id;
        this.date = new Date();
        this.status = InvoiceStatus.CREATED;
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        for (InvoiceItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new InvoiceItem(product, quantity));
    }

    public double getTotalAmount() {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getId() { return id; }
    public Date getDate() { return date; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public List<InvoiceItem> getItems() { return items; }

    @Override
    public String toString() {
        return "Накладная #" + id + " от " + date + " (" + status.getDisplayName() + ")";
    }
}