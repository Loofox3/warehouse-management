package service;

import model.Product;
import model.Invoice;
import model.User;
import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static final String PRODUCTS_FILE = "data/products.txt";
    private static final String INVOICES_FILE = "data/invoices.txt";
    private static final String USERS_FILE = "data/users.txt";
    
    private Map<Integer, Product> products = new HashMap<>();
    private Map<Integer, Invoice> invoices = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    
    public DataManager() {
        new File("data").mkdirs();
    }
    
    public boolean loadAllData() {
        try {
            loadProducts();
            loadInvoices();
            loadUsers();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void loadProducts() {
        products.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int warehouseQty = Integer.parseInt(parts[3].trim());
                    int shopQty = Integer.parseInt(parts[4].trim());
                    
                    products.put(id, new Product(id, name, price, warehouseQty, shopQty));
                }
            }
        } catch (FileNotFoundException e) {
            createTestData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadInvoices() {
        invoices.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(INVOICES_FILE))) {
            // Пока просто создаем пустой файл
        } catch (FileNotFoundException e) {
            // Файл создастся при первом сохранении
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadUsers() {
        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String login = parts[1].trim();
                    String password = parts[2].trim();
                    String role = parts[3].trim();
                    
                    users.put(login, new User(id, login, password, role));
                }
            }
        } catch (FileNotFoundException e) {
            createTestUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveProducts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            writer.println("# ID|Название|Цена|Склад|Магазин");
            for (Product product : products.values()) {
                writer.println(product.getId() + "|" + 
                              product.getName() + "|" + 
                              product.getPrice() + "|" + 
                              product.getQuantityInWarehouse() + "|" + 
                              product.getQuantityInShop());
            }
            writer.flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения товаров", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void saveInvoices() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVOICES_FILE))) {
            writer.println("# Накладные");
            for (Invoice invoice : invoices.values()) {
                writer.println(invoice.getId());
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("# ID|Логин|Пароль|Роль");
            for (User user : users.values()) {
                writer.println(user.getId() + "|" + 
                              user.getLogin() + "|" + 
                              user.getPassword() + "|" + 
                              user.getRole());
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createTestData() {
        // Удаляем старые .dat файлы если есть
        File oldFile = new File("data/products.dat");
        if (oldFile.exists()) oldFile.delete();
        
        Product p1 = new Product(1, "Ноутбук", 50000.0, 10, 2);
        Product p2 = new Product(2, "Мышь", 1000.0, 50, 10);
        Product p3 = new Product(3, "Клавиатура", 2000.0, 30, 5);
        
        products.put(1, p1);
        products.put(2, p2);
        products.put(3, p3);
        saveProducts();
    }
    
    private void createTestUsers() {
        User seller = new User(1, "seller", "123", "SELLER");
        User admin = new User(2, "admin", "123", "ADMIN");
        User warehouse = new User(3, "warehouse", "123", "WAREHOUSE_MANAGER");
        
        users.put("seller", seller);
        users.put("admin", admin);
        users.put("warehouse", warehouse);
        saveUsers();
    }
    
    public Map<Integer, Product> getProducts() { return products; }
    public Map<Integer, Invoice> getInvoices() { return invoices; }
    public Map<String, User> getUsers() { return users; }
    
    public void addProduct(Product product) {
        products.put(product.getId(), product);
        saveProducts(); // Сохраняем сразу после добавления
    }
    
    public void addInvoice(Invoice invoice) {
        invoices.put(invoice.getId(), invoice);
        saveInvoices();
    }
    
    public void addUser(User user) {
        users.put(user.getLogin(), user);
        saveUsers();
    }
    
    public Product findProductByName(String name) {
        for (Product product : products.values()) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }
    
    public int getNextProductId() {
        return products.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
    
    public int getNextInvoiceId() {
        return invoices.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
    
    public int getNextUserId() {
        return users.values().stream().mapToInt(User::getId).max().orElse(0) + 1;
    }
}