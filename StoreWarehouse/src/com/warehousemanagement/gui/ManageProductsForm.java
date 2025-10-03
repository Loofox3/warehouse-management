package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;      // ← ОБЯЗАТЕЛЬНО!
import java.awt.event.ActionListener;
import java.io.IOException;

import model.Product;
import service.DataManager;



// класс для управления товаром
public class ManageProductsForm  extends JFrame{
    private DataManager dataManager;
    private JTable productsTable;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClose;

    public ManageProductsForm(DataManager dataManager){
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }
    private void initializeComponents(){
        setTitle("Управление товарами");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
    
    // Создаем таблицу
        String[] columnNames = {"ID", "Название", "Цена", "На складе", "В магазине"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование прямо в таблице
            }
        };
        productsTable = new JTable(model);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        btnEdit = new JButton("Редактировать");
        btnDelete = new JButton("Удалить");
        btnRefresh = new JButton("Обновить");
        btnClose = new JButton("Закрыть");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        // Заголовок
        JLabel lblTitle = new JLabel("Управление товарами", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Таблица с прокруткой
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок управления
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedProduct();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedProduct();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProductsData();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    void loadProductsData() {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0); // Очищаем таблицу

        for (Product product : dataManager.getProducts().values()) {
            Object[] rowData = {
                product.getId(),
                product.getName(),
                product.getPrice() + " руб.",
                product.getQuantityInWarehouse(),
                product.getQuantityInShop()
            };
            model.addRow(rowData);
        }
    }

    private void editSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Выберите товар для редактирования", 
                "Внимание", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Получаем ID выбранного товара
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        Product product = findProductById(productId);
        
        if (product != null) {
            // Открываем форму редактирования
            EditProductForm editForm = new EditProductForm(dataManager, product, this);
            editForm.setVisible(true);
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Выберите товар для удаления", 
                "Внимание", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Получаем данные выбранного товара
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);

        // Подтверждение удаления
        int result = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить товар:\n" + productName + " (ID: " + productId + ")?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // Удаляем товар
            dataManager.removeProduct(productName);
            try {
                dataManager.saveProducts();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            JOptionPane.showMessageDialog(this, 
                "Товар '" + productName + "' успешно удален", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Обновляем таблицу
            loadProductsData();
        }
    }

    private Product findProductById(int id) {
        for (Product product : dataManager.getProducts().values()) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }
}