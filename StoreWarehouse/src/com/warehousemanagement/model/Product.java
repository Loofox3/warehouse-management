package model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantityInWarehouse;
    private int quantityInShop;

    public Product(int id, String name, double price, int quantityInWarehouse, int quantityInShop) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantityInWarehouse = quantityInWarehouse;
        this.quantityInShop = quantityInShop;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantityInWarehouse() { return quantityInWarehouse; }
    public void setQuantityInWarehouse(int quantityInWarehouse) { this.quantityInWarehouse = quantityInWarehouse; }
    public int getQuantityInShop() { return quantityInShop; }
    public void setQuantityInShop(int quantityInShop) { this.quantityInShop = quantityInShop; }

    @Override
    public String toString() {
        return name + " (ID: " + id + ", Цена: " + price + " руб.)";
    }
}