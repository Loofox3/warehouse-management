package gui;

import javax.swing.JFrame;

import java.awt.*;
import gui.AdminMainForm;
import javax.swing.*;

import model.Product;
import service.DataManager;


import java.awt.event.ActionEvent;      // ← ОБЯЗАТЕЛЬНО!
import java.awt.event.ActionListener;
//подробное редактирование товара
public class EditProductForm extends JFrame {
    private DataManager dataManager;
    private Product productToEdit;
    private ManageProductsForm parentForm;
    
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtQuantityWarehouse;
    private JTextField txtQuantityShop;
    private JButton btnSave;
    private JButton btnCancel;

    public EditProductForm(DataManager dataManager, Product product, ManageProductsForm parentForm) {
        this.dataManager = dataManager;
        this.productToEdit = product;
        this.parentForm = parentForm;
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
        setResizable(false);

        txtName = new JTextField(20);
        txtPrice = new JTextField(20);
        txtQuantityWarehouse = new JTextField(20);
        txtQuantityShop = new JTextField(20);
        btnSave = new JButton("Сохранить изменения");
        btnCancel = new JButton("Отмена");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Редактирование товара (ID: " + productToEdit.getId() + ")", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Центральная панель с полями
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        centerPanel.add(new JLabel("Название товара:"));
        centerPanel.add(txtName);
        centerPanel.add(new JLabel("Цена (руб.):"));
        centerPanel.add(txtPrice);
        centerPanel.add(new JLabel("Количество на складе:"));
        centerPanel.add(txtQuantityWarehouse);
        centerPanel.add(new JLabel("Количество в магазине:"));
        centerPanel.add(txtQuantityShop);
        centerPanel.add(btnSave);
        centerPanel.add(btnCancel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProductChanges();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadProductData() {
        // Заполняем поля текущими данными товара
        txtName.setText(productToEdit.getName());
        txtPrice.setText(String.valueOf(productToEdit.getPrice()));
        txtQuantityWarehouse.setText(String.valueOf(productToEdit.getQuantityInWarehouse()));
        txtQuantityShop.setText(String.valueOf(productToEdit.getQuantityInShop()));
    }

    private void saveProductChanges() {
        try {
            // Получаем новые данные из полей
            String newName = txtName.getText().trim();
            double newPrice = Double.parseDouble(txtPrice.getText().trim());
            int newQuantityWarehouse = Integer.parseInt(txtQuantityWarehouse.getText().trim());
            int newQuantityShop = Integer.parseInt(txtQuantityShop.getText().trim());

            // Валидация
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название товара", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Цена должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newQuantityWarehouse < 0 || newQuantityShop < 0) {
                JOptionPane.showMessageDialog(this, "Количество не может быть отрицательным", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверяем, не занято ли новое название другим товаром
            if (!newName.equals(productToEdit.getName())) {
                if (dataManager.findProductByName(newName) != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Товар с названием '" + newName + "' уже существует", 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Обновляем данные товара
            productToEdit.setName(newName);
            productToEdit.setPrice(newPrice);
            productToEdit.setQuantityInWarehouse(newQuantityWarehouse);
            productToEdit.setQuantityInShop(newQuantityShop);

            // Если изменилось название, нужно обновить ключ в Map
            if (!newName.equals(productToEdit.getName())) {
                dataManager.getProducts().remove(productToEdit.getName()); // Удаляем старый ключ
                dataManager.getProducts().put(newName, productToEdit);     // Добавляем с новым ключом
            }

            // Сохраняем изменения
            dataManager.saveProducts();
            
            JOptionPane.showMessageDialog(this, 
                "Изменения товара сохранены!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Закрываем форму редактирования
            dispose();
            
            // Обновляем таблицу в родительской форме
            if (parentForm != null) {
                parentForm.loadProductsData();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Проверьте правильность введенных чисел", 
                "Ошибка ввода", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка при сохранении изменений: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}