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

public class SellProductsForm extends JFrame {
    private DataManager dataManager;
    
    private JComboBox<String> comboProducts;
    private JTextField txtQuantity;
    private JButton btnAddToCart;
    private JButton btnProcessSale;
    private JButton btnCancel;
    
    private JTable cartTable;
    private Map<Product, Integer> cartItems;
    private JLabel lblTotalAmount;
    
    public SellProductsForm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.cartItems = new HashMap<>();
        initializeComponents();
        setupLayout();
        setupListeners();
        setupValidation();
        loadAvailableProducts();
    }

    private void initializeComponents() {
        setTitle("Оформление продажи товаров");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Комбобокс с товарами в магазине
        comboProducts = new JComboBox<>();
        txtQuantity = new JTextField(5);
        btnAddToCart = new JButton("Добавить в корзину");
        btnProcessSale = new JButton("Оформить продажу");
        btnCancel = new JButton("Отмена");

        // Таблица корзины
        String[] columnNames = {"Товар", "Количество", "Цена", "Сумма"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(model);
        
        lblTotalAmount = new JLabel("Общая сумма: 0.00 руб.");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Оформление продажи товаров", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Панель добавления товаров
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Добавление товара"));
        addPanel.add(new JLabel("Товар в магазине:"));
        addPanel.add(comboProducts);
        addPanel.add(new JLabel("Количество:"));
        addPanel.add(txtQuantity);
        addPanel.add(btnAddToCart);
        
        add(addPanel, BorderLayout.NORTH);

        // Панель корзины
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Корзина покупок"));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        cartPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Панель итогов
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(lblTotalAmount);
        cartPanel.add(totalPanel, BorderLayout.SOUTH);
        
        add(cartPanel, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnProcessSale);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAddToCart.addActionListener(e -> addToCart());
        btnProcessSale.addActionListener(e -> processSale());
        btnCancel.addActionListener(e -> dispose());
        
        // Enter для быстрого добавления
        txtQuantity.addActionListener(e -> addToCart());
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

    private void loadAvailableProducts() {
        comboProducts.removeAllItems();
        for (Product product : dataManager.getProducts().values()) {
            // Показываем только товары, которые есть в магазине
            if (product.getQuantityInShop() > 0) {
                comboProducts.addItem(product.getName() + " (в наличии: " + product.getQuantityInShop() + " шт.)");
            }
        }
        
        if (comboProducts.getItemCount() == 0) {
            comboProducts.addItem("Нет товаров в магазине");
            btnAddToCart.setEnabled(false);
        }
    }

    private void addToCart() {
        try {
            int selectedIndex = comboProducts.getSelectedIndex();
            if (selectedIndex == -1 || comboProducts.getItemCount() == 0) {
                showError("Выберите товар из списка");
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

            // Проверяем наличие в магазине
            if (product.getQuantityInShop() < quantity) {
                showError("Недостаточно товара в магазине\nДоступно: " + product.getQuantityInShop() + " шт.");
                txtQuantity.setText("");
                txtQuantity.requestFocus();
                return;
            }

            // Проверяем, не добавлен ли уже этот товар в корзину
            if (cartItems.containsKey(product)) {
                int currentQty = cartItems.get(product);
                if (product.getQuantityInShop() < currentQty + quantity) {
                    showError("Недостаточно товара в магазине с учетом уже добавленного в корзину\nДоступно: " + 
                             (product.getQuantityInShop() - currentQty) + " шт.");
                    return;
                }
            }

            // Добавляем в корзину
            cartItems.put(product, cartItems.getOrDefault(product, 0) + quantity);
            updateCartTable();

            // Очищаем поле количества
            txtQuantity.setText("");
            txtQuantity.requestFocus();

        } catch (NumberFormatException e) {
            showError("Введите корректное целое число для количества");
            txtQuantity.requestFocus();
        }
    }

    private void updateCartTable() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0);

        double totalAmount = 0;

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
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

        // Обновляем общую сумму
        lblTotalAmount.setText("Общая сумма: " + String.format("%.2f руб.", totalAmount));
        
        // Активируем кнопку оформления если есть товары в корзине
        btnProcessSale.setEnabled(!cartItems.isEmpty());
    }

    private void processSale() {
        if (cartItems.isEmpty()) {
            showError("Добавьте товары в корзину");
            return;
        }

        double totalAmount = calculateTotalAmount();
        
        // Подтверждение продажи
        int confirm = JOptionPane.showConfirmDialog(this,
            "Подтвердите оформление продажи:\n\n" +
            "Товаров: " + cartItems.size() + "\n" +
            "Общая сумма: " + String.format("%.2f руб.", totalAmount) + "\n\n" +
            "Продолжить?",
            "Подтверждение продажи",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Создаем накладную продажи
            int newInvoiceId = dataManager.getNextInvoiceId();
            Invoice saleInvoice = new Invoice(newInvoiceId);
            saleInvoice.setType("SALE");
            saleInvoice.setStatus("COMPLETED");

            // Обрабатываем каждый товар в корзине
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                saleInvoice.addItem(product, quantity);

                // Списание товаров из магазина
                product.setQuantityInShop(product.getQuantityInShop() - quantity);
            }

            // Сохраняем накладную и обновляем товары
            dataManager.addInvoice(saleInvoice);
            dataManager.saveProducts();

            // Показываем чек
            showReceipt(saleInvoice, totalAmount);

            // Закрываем форму
            dispose();

        } catch (Exception e) {
            showError("Ошибка при оформлении продажи: " + e.getMessage());
        }
    }

    private void showReceipt(Invoice invoice, double totalAmount) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("═══════════════════════════════\n");
        receipt.append("           ЧЕК ПРОДАЖИ\n");
        receipt.append("═══════════════════════════════\n");
        receipt.append("Накладная: #").append(invoice.getId()).append("\n");
        receipt.append("Дата: ").append(new java.util.Date()).append("\n");
        receipt.append("───────────────────────────────\n");
        
        for (InvoiceItem item : invoice.getItems()) {
            receipt.append(String.format("%-15s %2d x %6.2f = %7.2f руб.\n",
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.getProduct().getPrice() * item.getQuantity()));
        }
        
        receipt.append("───────────────────────────────\n");
        receipt.append(String.format("ИТОГО: %23.2f руб.\n", totalAmount));
        receipt.append("═══════════════════════════════\n");
        receipt.append("Спасибо за покупку!");

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Чек продажи", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calculateTotalAmount() {
        return cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}