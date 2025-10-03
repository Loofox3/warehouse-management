package gui;

import service.DataManager;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageProductsForm extends JFrame {
    private DataManager dataManager;
    
    private JTable productsTable;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClose;
    
    public ManageProductsForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }
    
    private void initializeComponents() {
        setTitle("Управление товарами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        
        String[] columnNames = {"ID", "Название", "Цена", "На складе", "В магазине"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(model);
        
        btnAdd = new JButton("Добавить");
        btnEdit = new JButton("Редактировать");
        btnDelete = new JButton("Удалить");
        btnRefresh = new JButton("Обновить");
        btnClose = new JButton("Закрыть");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Управление товарами", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnAdd.addActionListener(e -> {
            AddProductForm addForm = new AddProductForm(dataManager);
            addForm.setVisible(true);
            addForm.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadProductsData();
                }
            });
        });
        
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnRefresh.addActionListener(e -> loadProductsData());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void loadProductsData() {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0);
        
        for (Product product : dataManager.getProducts().values()) {
            Object[] rowData = {
                product.getId(),
                product.getName(),
                String.format("%.2f руб.", product.getPrice()),
                product.getQuantityInWarehouse(),
                product.getQuantityInShop()
            };
            model.addRow(rowData);
        }
    }
    
    private void editProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите товар для редактирования", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        Product product = dataManager.getProducts().get(productId);
        
        if (product != null) {
            EditProductForm editForm = new EditProductForm(dataManager, product);
            editForm.setVisible(true);
            editForm.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadProductsData();
                }
            });
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите товар для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить товар: " + productName + "?", 
            "Подтверждение удаления", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.getProducts().remove(productId);
            dataManager.saveProducts();
            loadProductsData();
            JOptionPane.showMessageDialog(this, "Товар удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}