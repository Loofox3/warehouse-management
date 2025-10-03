package gui;

import service.DataManager;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewShopProductsForm extends JFrame {
    private DataManager dataManager;
    
    private JTable productsTable;
    private JComboBox<String> comboSort;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnClear;
    private JButton btnClose;
    private JLabel lblStats;
    
    public ViewShopProductsForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }
    
    private void initializeComponents() {
        setTitle("Товары в магазине - Просмотр");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // Таблица товаров
        String[] columnNames = {"ID", "Название", "Цена", "В наличии", "На складе", "Статус"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(model);
        
        // Компоненты поиска и сортировки
        String[] sortOptions = {"По названию (А-Я)", "По названию (Я-А)", "По цене (возр.)", "По цене (убыв.)", "По наличию (убыв.)"};
        comboSort = new JComboBox<>(sortOptions);
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Найти");
        btnClear = new JButton("Сбросить");
        btnClose = new JButton("Закрыть");
        
        lblStats = new JLabel();
        lblStats.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Заголовок
        JLabel lblTitle = new JLabel("Товары в магазине", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        controlPanel.add(new JLabel("Поиск:"));
        controlPanel.add(txtSearch);
        controlPanel.add(btnSearch);
        controlPanel.add(new JLabel("Сортировка:"));
        controlPanel.add(comboSort);
        controlPanel.add(btnClear);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Таблица
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель статистики и кнопок
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Статистика
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statsPanel.add(lblStats);
        bottomPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Кнопки
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnSearch.addActionListener(e -> searchProducts());
        btnClear.addActionListener(e -> clearSearch());
        btnClose.addActionListener(e -> dispose());
        comboSort.addActionListener(e -> loadProductsData());
        
        // Поиск при нажатии Enter
        txtSearch.addActionListener(e -> searchProducts());
    }
    
    private void loadProductsData() {
        loadProductsData(null);
    }
    
    private void loadProductsData(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0);
        
        List<Product> products = dataManager.getProducts().values().stream()
                .filter(product -> product.getQuantityInShop() > 0) // Только товары в магазине
                .collect(Collectors.toList());
        
        // Применяем поиск если есть
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        
        // Применяем сортировку
        String sortOption = (String) comboSort.getSelectedItem();
        if (sortOption != null) {
            switch (sortOption) {
                case "По названию (А-Я)":
                    products.sort(Comparator.comparing(Product::getName));
                    break;
                case "По названию (Я-А)":
                    products.sort(Comparator.comparing(Product::getName).reversed());
                    break;
                case "По цене (возр.)":
                    products.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "По цене (убыв.)":
                    products.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "По наличию (убыв.)":
                    products.sort(Comparator.comparing(Product::getQuantityInShop).reversed());
                    break;
            }
        }
        
        // Заполняем таблицу
        int totalProducts = 0;
        int totalQuantity = 0;
        double totalValue = 0;
        
        for (Product product : products) {
            String status = getStockStatus(product);
            String statusColor = getStatusColor(product);
            
            Object[] rowData = {
                product.getId(),
                product.getName(),
                String.format("%.2f руб.", product.getPrice()),
                product.getQuantityInShop(),
                product.getQuantityInWarehouse(),
                status
            };
            model.addRow(rowData);
            
            totalProducts++;
            totalQuantity += product.getQuantityInShop();
            totalValue += product.getPrice() * product.getQuantityInShop();
        }
        
        // Обновляем статистику
        updateStats(totalProducts, totalQuantity, totalValue);
    }
    
    private String getStockStatus(Product product) {
        int shopQty = product.getQuantityInShop();
        
        if (shopQty >= 10) {
            return "✅ Много";
        } else if (shopQty >= 5) {
            return "⚠️ Средне";
        } else if (shopQty >= 1) {
            return "🔴 Мало";
        } else {
            return "❌ Нет в наличии";
        }
    }
    
    private String getStatusColor(Product product) {
        int shopQty = product.getQuantityInShop();
        
        if (shopQty >= 10) {
            return "green";
        } else if (shopQty >= 5) {
            return "orange";
        } else {
            return "red";
        }
    }
    
    private void updateStats(int totalProducts, int totalQuantity, double totalValue) {
        lblStats.setText(String.format(
            "Товаров: %d | Общее количество: %d шт. | Общая стоимость: %.2f руб.",
            totalProducts, totalQuantity, totalValue
        ));
        
        // Подсветка если мало товаров
        if (totalProducts == 0) {
            lblStats.setForeground(Color.RED);
            lblStats.setText(lblStats.getText() + " - НЕТ ТОВАРОВ В МАГАЗИНЕ!");
        } else if (totalProducts < 5) {
            lblStats.setForeground(Color.ORANGE);
        } else {
            lblStats.setForeground(Color.BLACK);
        }
    }
    
    private void searchProducts() {
        String searchTerm = txtSearch.getText().trim();
        loadProductsData(searchTerm);
    }
    
    private void clearSearch() {
        txtSearch.setText("");
        loadProductsData();
    }
}