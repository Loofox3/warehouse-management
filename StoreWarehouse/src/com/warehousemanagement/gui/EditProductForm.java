package gui;

import service.DataManager;
import model.Product;
import javax.swing.*;
import java.awt.*;

public class EditProductForm extends JFrame {
    private DataManager dataManager;
    private Product product;
    
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtWarehouseQty;
    private JTextField txtShopQty;
    private JButton btnSave;
    private JButton btnCancel;
    
    public EditProductForm(DataManager dataManager, Product product) {
        this.dataManager = dataManager;
        this.product = product;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductData();
    }
    
    private void initializeComponents() {
        setTitle("Редактирование товара");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        txtName = new JTextField(20);
        txtPrice = new JTextField(10);
        txtWarehouseQty = new JTextField(10);
        txtShopQty = new JTextField(10);
        btnSave = new JButton("Сохранить");
        btnCancel = new JButton("Отмена");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Редактирование товара", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        inputPanel.add(new JLabel("Название:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Цена:"));
        inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("Количество на складе:"));
        inputPanel.add(txtWarehouseQty);
        inputPanel.add(new JLabel("Количество в магазине:"));
        inputPanel.add(txtShopQty);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnSave.addActionListener(e -> saveProduct());
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void loadProductData() {
        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtWarehouseQty.setText(String.valueOf(product.getQuantityInWarehouse()));
        txtShopQty.setText(String.valueOf(product.getQuantityInShop()));
    }
    
    private void saveProduct() {
        try {
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText());
            int warehouseQty = Integer.parseInt(txtWarehouseQty.getText());
            int shopQty = Integer.parseInt(txtShopQty.getText());
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название товара", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            product.setName(name);
            product.setPrice(price);
            product.setQuantityInWarehouse(warehouseQty);
            product.setQuantityInShop(shopQty);
            
            dataManager.saveProducts();
            
            JOptionPane.showMessageDialog(this, "Товар успешно обновлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Проверьте правильность введенных чисел", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}