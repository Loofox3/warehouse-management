package gui;

import service.DataManager;
import model.Product;
import model.Invoice;
import model.InvoiceItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class CreateInvoiceForm extends JFrame {
    private DataManager dataManager;
    
    private JComboBox<String> comboProducts;
    private JTextField txtQuantity;
    private JButton btnAddItem;
    private JButton btnCreateInvoice;
    private JButton btnCancel;
    
    private JTable itemsTable;
    private Map<Product, Integer> selectedItems;
    
    public CreateInvoiceForm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.selectedItems = new HashMap<>();
        initializeComponents();
        setupLayout();
        setupListeners();
        setupValidation();
        loadProductsData();
    }

    private void initializeComponents() {
        setTitle("Создание накладной");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Комбобокс с товарами
        comboProducts = new JComboBox<>();
        txtQuantity = new JTextField(10);
        btnAddItem = new JButton("Добавить в накладную");
        btnCreateInvoice = new JButton("Создать накладную");
        btnCancel = new JButton("Отмена");

        // Таблица для выбранных товаров
        String[] columnNames = {"Товар", "Количество", "Цена", "Сумма"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(model);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Создание новой накладной", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Панель добавления товаров
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addPanel.add(new JLabel("Товар:"));
        addPanel.add(comboProducts);
        addPanel.add(new JLabel("Количество:"));
        addPanel.add(txtQuantity);
        addPanel.add(btnAddItem);
        
        add(addPanel, BorderLayout.NORTH);

        // Таблица с выбранными товарами
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnCreateInvoice);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAddItem.addActionListener(e -> addItemToInvoice());
        btnCreateInvoice.addActionListener(e -> createInvoice());
        btnCancel.addActionListener(e -> dispose());
    }

    private void setupValidation() {
        // Валидация количества - только цифры
        txtQuantity.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }

    private void loadProductsData() {
        comboProducts.removeAllItems();
        for (Product product : dataManager.getProducts().values()) {
            comboProducts.addItem(product.getName() + " (ID: " + product.getId() + ")");
        }
    }

    private void addItemToInvoice() {
        try {
            int selectedIndex = comboProducts.getSelectedIndex();
            if (selectedIndex == -1) {
                showError("Выберите товар из списка");
                comboProducts.requestFocus();
                return;
            }

            String quantityText = txtQuantity.getText().trim();
            if (quantityText.isEmpty()) {
                showError("Введите количество товара");
                txtQuantity.requestFocus();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showError("Количество должно быть больше 0");
                txtQuantity.requestFocus();
                return;
            }

            // Получаем выбранный товар
            String selectedProductInfo = (String) comboProducts.getSelectedItem();
            String productName = selectedProductInfo.split(" \\(")[0]; // Извлекаем название
            Product product = dataManager.findProductByName(productName);

            if (product == null) {
                showError("Товар не найден в системе");
                return;
            }

            // Проверяем наличие на складе
            if (product.getQuantityInWarehouse() < quantity) {
                showError("Недостаточно товара на складе\nДоступно: " + product.getQuantityInWarehouse() + " шт.");
                txtQuantity.setText("");
                txtQuantity.requestFocus();
                return;
            }

            // Проверяем, не добавлен ли уже этот товар
            if (selectedItems.containsKey(product)) {
                int currentQty = selectedItems.get(product);
                if (product.getQuantityInWarehouse() < currentQty + quantity) {
                    showError("Недостаточно товара на складе с учетом уже добавленного количества\nДоступно: " + 
                             (product.getQuantityInWarehouse() - currentQty) + " шт.");
                    return;
                }
            }

            // Добавляем в выбранные товары
            selectedItems.put(product, selectedItems.getOrDefault(product, 0) + quantity);
            updateItemsTable();

            // Очищаем поле количества
            txtQuantity.setText("");
            txtQuantity.requestFocus();

        } catch (NumberFormatException e) {
            showError("Введите корректное целое число для количества");
            txtQuantity.requestFocus();
        }
    }

    private void updateItemsTable() {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        model.setRowCount(0);

        double totalAmount = 0;

        for (Map.Entry<Product, Integer> entry : selectedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double itemTotal = product.getPrice() * quantity;
            totalAmount += itemTotal;

            Object[] rowData = {
                product.getName(),
                quantity,
                String.format("%.2f руб.", product.getPrice()),
                String.format("%.2f руб.", itemTotal)
            };
            model.addRow(rowData);
        }

        // Добавляем строку с итогом
        if (!selectedItems.isEmpty()) {
            model.addRow(new Object[]{"", "", "ИТОГО:", String.format("%.2f руб.", totalAmount)});
        }
    }

    private void createInvoice() {
        if (selectedItems.isEmpty()) {
            showError("Добавьте хотя бы один товар в накладную");
            return;
        }

        // Подтверждение создания накладной
        int confirm = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите создать накладную?\nТоваров: " + selectedItems.size() + "\n" +
            "Общая сумма: " + String.format("%.2f руб.", calculateTotalAmount()),
            "Подтверждение создания накладной",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Создаем новую накладную
            int newInvoiceId = dataManager.getNextInvoiceId();
            Invoice newInvoice = new Invoice(newInvoiceId);
            newInvoice.setType("ORDER");
            newInvoice.setStatus("CREATED");

            // Добавляем товары в накладную
            for (Map.Entry<Product, Integer> entry : selectedItems.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                newInvoice.addItem(product, quantity);

                // Обновляем остатки на складе
                product.setQuantityInWarehouse(product.getQuantityInWarehouse() - quantity);
                product.setQuantityInShop(product.getQuantityInShop() + quantity);
            }

            dataManager.addInvoice(newInvoice);
            dataManager.saveProducts();

            JOptionPane.showMessageDialog(this, 
                "✅ Накладная #" + newInvoiceId + " успешно создана!\n" +
                "Товаров: " + selectedItems.size() + "\n" +
                "Общая сумма: " + String.format("%.2f руб.", newInvoice.getTotalAmount()), 
                "Успех", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception e) {
            showError("Ошибка при создании накладной: " + e.getMessage());
        }
    }

    private double calculateTotalAmount() {
        return selectedItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
    }
}