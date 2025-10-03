package service;

import java.io.*;
import java.util.*;

import model.Invoice;
import model.InvoiceStatus;
import model.Product;
import model.User;

public class DataManager {
    private Map<String, Product> products = new HashMap<>();
    private Map<Integer, Invoice> invoices = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    
    private static final String DATA_DIR = "data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.txt";
    private static final String INVOICES_FILE = DATA_DIR + "invoices.txt";
    private static final String USERS_FILE = DATA_DIR + "users.txt";

    public DataManager() {
        new File(DATA_DIR).mkdirs();
    }

    public boolean loadAllData() {
        try {
            loadUsers();
            loadProducts();
            loadInvoices();
            return true;
        } catch (Exception e) {
            System.err.println("Ошибка загрузки данных: " + e.getMessage());
            return false;
        }
    }

    private void loadUsers() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            users.put("admin", new User("admin", "admin", "admin"));
            users.put("seller", new User("seller", "seller", "seller"));
            users.put("storekeeper", new User("storekeeper", "storekeeper", "storekeeper"));
            saveUsers();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    users.put(parts[0], new User(parts[0], parts[1], parts[2]));
                }
            }
        }
    }

    private void loadProducts() throws IOException {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            products.put("Ноутбук", new Product(1, "Ноутбук", 50000, 10, 2));
            products.put("Мышь", new Product(2, "Мышь", 1000, 50, 10));
            products.put("Клавиатура", new Product(3, "Клавиатура", 2000, 30, 5));
            saveProducts();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int qtyWarehouse = Integer.parseInt(parts[3]);
                    int qtyShop = Integer.parseInt(parts[4]);
                    products.put(name, new Product(id, name, price, qtyWarehouse, qtyShop));
                }
            }
        }
    }

    private void loadInvoices() throws IOException {
        File file = new File(INVOICES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(INVOICES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    int id = Integer.parseInt(parts[0]);
                    InvoiceStatus status = InvoiceStatus.valueOf(parts[1]);
                    Invoice invoice = new Invoice(id);
                    invoice.setStatus(status);
                    invoices.put(id, invoice);
                }
            }
        }
    }

    public void saveAllData() {
        try {
            saveUsers();
            saveProducts();
            saveInvoices();
        } catch (IOException e) {
            System.err.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }


    public void saveProducts() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products.values()) {
                writer.write(product.getId() + ";" + product.getName() + ";" + 
                           product.getPrice() + ";" + product.getQuantityInWarehouse() + 
                           ";" + product.getQuantityInShop() + "\n");
            }
        }
    }

    public void saveInvoices() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVOICES_FILE))) {
            for (Invoice invoice : invoices.values()) {
                writer.write(invoice.getId() + ";" + invoice.getStatus() + ";" + 
                           invoice.getDate().getTime() + "\n");
            }
        }
    }

    public Map<String, Product> getProducts() { return products; }
    public Map<Integer, Invoice> getInvoices() { return invoices; }
    public Map<String, User> getUsers() { return users; }

    public void addProduct(Product product) {
        products.put(product.getName(), product);
    }

    public boolean removeProduct(String productName) {
        return products.remove(productName) != null;
    }

    public void addInvoice(Invoice invoice) {
        invoices.put(invoice.getId(), invoice);
    }

    public Product findProductByName(String name) {
        return products.get(name);
    }
    
    public void saveUsers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getLogin() + ";" + user.getPassword() + ";" + user.getRole() + "\n");
            }
        }
    }
}