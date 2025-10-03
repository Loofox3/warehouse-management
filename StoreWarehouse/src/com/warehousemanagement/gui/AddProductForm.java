package gui;
// класс для добавление товаров
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Product;

import java.awt.*;
import service.DataManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class AddProductForm extends JFrame {
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtQuantityWarehouse;
    private JTextField txtQuantityShop;
    private JButton btnAdd;
    private JButton btnCancel;
    private DataManager dataManager;

    public AddProductForm(DataManager dataManager){
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents(){
        setTitle("Добавление новго товара");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,300);
        setLocationRelativeTo(null);
        setResizable(false);

        txtName = new JTextField(20);
        txtPrice = new JTextField(20);
        txtQuantityWarehouse = new JTextField(20);
        txtQuantityShop = new JTextField(20);
        btnAdd = new JButton("Добавить товар");
        btnCancel = new JButton("Отмена");
    }
    private void setupLayout(){
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Добавление нового товара", JLabel.CENTER);
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
        centerPanel.add(btnAdd);
        centerPanel.add(btnCancel);

        add(centerPanel, BorderLayout.CENTER);
    }

     private void setupListeners() {
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрыть форму
            }
        });
    }

    private void addProduct(){
        try{
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int quantityWarehouse = Integer.parseInt(txtQuantityWarehouse.getText().trim());
            int quantityShop = Integer.parseInt(txtQuantityShop.getText().trim());
            
            //Валидация
            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this, "Введите название товара", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(price<=0){
                JOptionPane.showMessageDialog(this, "Цена должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quantityWarehouse < 0 || quantityShop < 0) {
                JOptionPane.showMessageDialog(this, "Количество не может быть отрицательным", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Проверяем, нет ли уже товара с таким названием
            if (dataManager.findProductByName(name) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Товар с названием '" + name + "' уже существует", 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Создаем новый товар
            int newId = findNextAvailableId();
            Product newProduct = new Product(newId, name, price, quantityWarehouse, quantityShop);
            
            // Добавляем в систему
            dataManager.addProduct(newProduct);
            dataManager.saveProducts();
            
            JOptionPane.showMessageDialog(this, 
                "Товар '" + name + "' успешно добавлен!\nID: " + newId, 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Очищаем поля для следующего ввода
            clearFields();
            
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Проверьте правильность введенных чисел\n(цена и количество должны быть числами)", 
                "Ошибка ввода", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка при добавлении товара: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }

    }

     private int findNextAvailableId() {
        // Находим максимальный ID и возвращаем следующий
        int maxId = 0;
        for (Product product : dataManager.getProducts().values()) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        return maxId + 1;
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        txtQuantityWarehouse.setText("");
        txtQuantityShop.setText("");
        txtName.requestFocus();
    }
}


