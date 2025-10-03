package gui;

import service.DataManager;
import model.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AddProductForm extends JFrame {
    private DataManager dataManager;
    
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtWarehouseQty;
    private JTextField txtShopQty;
    private JButton btnAdd;
    private JButton btnCancel;
    
    public AddProductForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        setupValidation();
    }
    
    private void initializeComponents() {
        setTitle("Добавление товара");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        txtName = new JTextField(20);
        txtPrice = new JTextField(10);
        txtWarehouseQty = new JTextField(10);
        txtShopQty = new JTextField(10);
        btnAdd = new JButton("Добавить");
        btnCancel = new JButton("Отмена");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Добавление нового товара", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        inputPanel.add(new JLabel("Название:*"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Цена:*"));
        inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("Количество на складе:*"));
        inputPanel.add(txtWarehouseQty);
        inputPanel.add(new JLabel("Количество в магазине:*"));
        inputPanel.add(txtShopQty);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnAdd.addActionListener(e -> addProduct());
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void setupValidation() {
        // Валидация цены - только числа и точка
        txtPrice.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = ((JTextField) e.getSource()).getText();
                
                if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
                
                // Проверка на одну точку
                if (c == '.' && text.contains(".")) {
                    e.consume();
                }
            }
        });
        
        // Валидация количеств - только цифры
        txtWarehouseQty.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
        
        txtShopQty.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }
    
    private void addProduct() {
        // Валидация обязательных полей
        if (txtName.getText().trim().isEmpty()) {
            showError("Введите название товара");
            txtName.requestFocus();
            return;
        }
        
        if (txtPrice.getText().trim().isEmpty()) {
            showError("Введите цену товара");
            txtPrice.requestFocus();
            return;
        }
        
        if (txtWarehouseQty.getText().trim().isEmpty()) {
            showError("Введите количество на складе");
            txtWarehouseQty.requestFocus();
            return;
        }
        
        if (txtShopQty.getText().trim().isEmpty()) {
            showError("Введите количество в магазине");
            txtShopQty.requestFocus();
            return;
        }
        
        try {
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText());
            int warehouseQty = Integer.parseInt(txtWarehouseQty.getText());
            int shopQty = Integer.parseInt(txtShopQty.getText());
            
            // Дополнительная валидация
            if (name.length() < 2) {
                showError("Название товара должно содержать минимум 2 символа");
                txtName.requestFocus();
                return;
            }
            
            if (price <= 0) {
                showError("Цена должна быть больше 0");
                txtPrice.requestFocus();
                return;
            }
            
            if (warehouseQty < 0) {
                showError("Количество на складе не может быть отрицательным");
                txtWarehouseQty.requestFocus();
                return;
            }
            
            if (shopQty < 0) {
                showError("Количество в магазине не может быть отрицательным");
                txtShopQty.requestFocus();
                return;
            }
            
            // Проверка на уникальность названия
            if (dataManager.findProductByName(name) != null) {
                showError("Товар с таким названием уже существует");
                txtName.requestFocus();
                return;
            }
            
            int newId = dataManager.getNextProductId();
            Product product = new Product(newId, name, price, warehouseQty, shopQty);
            
            dataManager.addProduct(product);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Товар успешно добавлен!\nID: " + newId + "\nНазвание: " + name, 
                "Успех", JOptionPane.INFORMATION_MESSAGE);
                
            dispose();
            
        } catch (NumberFormatException e) {
            showError("Проверьте правильность введенных чисел:\n- Цена должна быть числом (например: 1500.50)\n- Количества должны быть целыми числами");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
    }

    
}