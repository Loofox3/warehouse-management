package gui;

import service.DataManager;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewProductsForm extends JFrame {
    private DataManager dataManager;
    
    private JTable productsTable;
    private JButton btnClose;
    
    public ViewProductsForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }
    
    private void initializeComponents() {
        setTitle("Просмотр товаров");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        
        // Таблица товаров
        String[] columnNames = {"ID", "Название", "Цена", "На складе", "В магазине", "Общее количество"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(model);
        
        btnClose = new JButton("Закрыть");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Список всех товаров", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Таблица
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnClose.addActionListener(e -> dispose());
    }
    
    private void loadProductsData() {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0);
        
        for (Product product : dataManager.getProducts().values()) {
            int totalQuantity = product.getQuantityInWarehouse() + product.getQuantityInShop();
            
            Object[] rowData = {
                product.getId(),
                product.getName(),
                String.format("%.2f руб.", product.getPrice()),
                product.getQuantityInWarehouse(),
                product.getQuantityInShop(),
                totalQuantity
            };
            model.addRow(rowData);
        }
    }
}