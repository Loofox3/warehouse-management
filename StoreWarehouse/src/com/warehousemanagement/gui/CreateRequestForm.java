package gui;

import service.DataManager;
import model.Product;
import model.Invoice;
import model.InvoiceStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CreateRequestForm extends JFrame {
    private DataManager dataManager;
    
    private JComboBox<String> comboProducts;
    private JTextField txtQuantity;
    private JButton btnAddItem;
    private JButton btnCreateRequest;
    private JButton btnCancel;
    
    private JTable itemsTable;
    private Map<Product, Integer> requestedItems;
    
    public CreateRequestForm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.requestedItems = new HashMap<>();
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }

    private void initializeComponents() {
        setTitle("Создание заявки на пополнение");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        comboProducts = new JComboBox<>();
        txtQuantity = new JTextField(10);
        btnAddItem = new JButton("Добавить в заявку");
        btnCreateRequest = new JButton("Создать заявку");
        btnCancel = new JButton("Отмена");

        // Таблица для запрашиваемых товаров
        String[] columnNames = {"Товар", "Запрошено", "На складе", "В магазине"};
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
        JLabel lblTitle = new JLabel("Заявка на пополнение магазина", JLabel.CENTER);
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

        // Таблица с запрошенными товарами
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnCreateRequest);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAddItem.addActionListener(e -> addItemToRequest());
        btnCreateRequest.addActionListener(e -> createRequest());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadProductsData() {
        comboProducts.removeAllItems();
        for (Product product : dataManager.getProducts().values()) {
            comboProducts.addItem(product.getName() + " (На складе: " + product.getQuantityInWarehouse() + ")");
        }
    }

    private void addItemToRequest() {
        try {
            int selectedIndex = comboProducts.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Выберите товар", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String quantityText = txtQuantity.getText().trim();
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Количество должно быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Получаем выбранный товар
            String selectedProductInfo = (String) comboProducts.getSelectedItem();
            String productName = selectedProductInfo.split(" \\(")[0];
            Product product = dataManager.findProductByName(productName);

            if (product == null) {
                JOptionPane.showMessageDialog(this, "Товар не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверяем наличие на складе
            if (product.getQuantityInWarehouse() < quantity) {
                JOptionPane.showMessageDialog(this, 
                    "Недостаточно товара на складе\nДоступно: " + product.getQuantityInWarehouse(), 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Добавляем в заявку
            requestedItems.put(product, requestedItems.getOrDefault(product, 0) + quantity);
            updateRequestTable();
            txtQuantity.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректное число", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRequestTable() {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        model.setRowCount(0);

        for (Map.Entry<Product, Integer> entry : requestedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            Object[] rowData = {
                product.getName(),
                quantity + " шт.",
                product.getQuantityInWarehouse() + " шт.",
                product.getQuantityInShop() + " шт."
            };
            model.addRow(rowData);
        }
    }

    private void createRequest() {
        if (requestedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Добавьте товары в заявку", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Создаем новую накладную (заявку)
            int newInvoiceId = findNextAvailableInvoiceId();
            Invoice newInvoice = new Invoice(newInvoiceId);

            // Добавляем товары в накладную
            for (Map.Entry<Product, Integer> entry : requestedItems.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                newInvoice.addItem(product, quantity);
            }

            // Сохраняем накладную со статусом CREATED
            dataManager.addInvoice(newInvoice);
            dataManager.saveInvoices();

            JOptionPane.showMessageDialog(this, 
                "Заявка #" + newInvoiceId + " создана успешно!\n" +
                "Товаров: " + requestedItems.size() + "\n" +
                "Статус: Ожидает комплектации", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка при создании заявки: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findNextAvailableInvoiceId() {
        int maxId = 0;
        for (Invoice invoice : dataManager.getInvoices().values()) {
            if (invoice.getId() > maxId) {
                maxId = invoice.getId();
            }
        }
        return maxId + 1;
    }
}