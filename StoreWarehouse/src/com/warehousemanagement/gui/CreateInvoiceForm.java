package gui;


import service.DataManager;
import model.Product;
import model.Invoice;
import model.InvoiceItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
// создание накладнгых
public class CreateInvoiceForm extends JFrame {
    private DataManager dataManager;
    
    private JComboBox<String> comboProducts;
    private JTextField txtQuantity;
    private JButton btnAddItem;
    private JButton btnCreateInvoiceForm;
    private JButton btnCancel;
    
    private JTable itemsTable;
    private Map<Product, Integer> selectedItems;
    
    public CreateInvoiceForm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.selectedItems = new HashMap<>();
        initializeComponents();
        setupLayout();
        setupListeners();
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
        btnCreateInvoiceForm = new JButton("Создать накладную");
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
        buttonPanel.add(btnCreateInvoiceForm);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAddItem.addActionListener(e -> addItemToInvoice());
        btnCreateInvoiceForm.addActionListener(e -> createInvoice());
        btnCancel.addActionListener(e -> dispose());
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
            String productName = selectedProductInfo.split(" \\(")[0]; // Извлекаем название
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

            // Добавляем в выбранные товары
            selectedItems.put(product, selectedItems.getOrDefault(product, 0) + quantity);
            updateItemsTable();

            // Очищаем поле количества
            txtQuantity.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректное число", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Добавьте товары в накладную", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Создаем новую накладную
            int newInvoiceId = findNextAvailableInvoiceId();
            Invoice newInvoice = new Invoice(newInvoiceId);

            // Добавляем товары в накладную
            for (Map.Entry<Product, Integer> entry : selectedItems.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                newInvoice.addItem(product, quantity);

                // Обновляем остатки на складе
                product.setQuantityInWarehouse(product.getQuantityInWarehouse() - quantity);
                product.setQuantityInShop(product.getQuantityInShop() + quantity);
            }

            // Сохраняем накладную и обновляем товары
            dataManager.addInvoice(newInvoice);
            dataManager.saveInvoices();
            dataManager.saveProducts();

            JOptionPane.showMessageDialog(this, 
                "Накладная #" + newInvoiceId + " успешно создана!\n" +
                "Товаров: " + selectedItems.size() + "\n" +
                "Общая сумма: " + String.format("%.2f руб.", newInvoice.getTotalAmount()), 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка при создании накладной: " + e.getMessage(), 
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