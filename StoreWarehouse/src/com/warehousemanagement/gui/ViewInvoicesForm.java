package gui;

import service.DataManager;
import model.Invoice;
import model.InvoiceItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewInvoicesForm extends JFrame {
    private DataManager dataManager;
    
    private JTable invoicesTable;
    private JButton btnViewDetails;
    private JButton btnClose;
    
    public ViewInvoicesForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadInvoicesData();
    }
    
    private void initializeComponents() {
        setTitle("Просмотр накладных");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        
        // Таблица накладных
        String[] columnNames = {"ID", "Тип", "Статус", "Кол-во товаров", "Общая сумма", "Дата создания"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(model);
        
        btnViewDetails = new JButton("Просмотреть детали");
        btnClose = new JButton("Закрыть");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Просмотр всех накладных", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Таблица
        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnViewDetails.addActionListener(e -> viewInvoiceDetails());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void loadInvoicesData() {
        DefaultTableModel model = (DefaultTableModel) invoicesTable.getModel();
        model.setRowCount(0);
        
        for (Invoice invoice : dataManager.getInvoices().values()) {
            Object[] rowData = {
                invoice.getId(),
                invoice.getType() != null ? invoice.getType() : "N/A",
                invoice.getStatus() != null ? invoice.getStatus() : "CREATED",
                invoice.getItems().size(),
                String.format("%.2f руб.", invoice.getTotalAmount()),
                "Сегодня" // Можно добавить поле даты в Invoice
            };
            model.addRow(rowData);
        }
    }
    
    /**
     * 
     */
    private void viewInvoiceDetails() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите накладную для просмотра", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int invoiceId = (int) invoicesTable.getValueAt(selectedRow, 0);
        Invoice invoice = dataManager.getInvoices().get(invoiceId);
        
        if (invoice != null) {
            // Создаем диалог с детальной информацией
            JDialog detailsDialog = new JDialog(this, "Детали накладной #" + invoiceId, true);
            detailsDialog.setSize(500, 400);
            detailsDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Информация о накладной
            JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            infoPanel.add(new JLabel("ID накладной:"));
            infoPanel.add(new JLabel(String.valueOf(invoiceId)));
            infoPanel.add(new JLabel("Тип:"));
            infoPanel.add(new JLabel(invoice.getType() != null ? invoice.getType() : "N/A"));
            infoPanel.add(new JLabel("Статус:"));
            infoPanel.add(new JLabel(invoice.getStatus() != null ? invoice.getStatus() : "CREATED"));
            infoPanel.add(new JLabel("Общая сумма:"));
            infoPanel.add(new JLabel(String.format("%.2f руб.", invoice.getTotalAmount())));
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            
            // Таблица товаров
            String[] columns = {"Товар", "Количество", "Цена", "Сумма"};
            DefaultTableModel itemsModel = new DefaultTableModel(columns, 0);
            
            for (InvoiceItem item : invoice.getItems()) {
                Object[] rowData = {
                    item.getProduct().getName(),
                    item.getQuantity(),
                    String.format("%.2f руб.", item.getProduct().getPrice()),
                    String.format("%.2f руб.", item.getProduct().getPrice() * item.getQuantity())
                };
                itemsModel.addRow(rowData);
            }
            
            JTable itemsTable = new JTable(itemsModel);
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            mainPanel.add(itemsScroll, BorderLayout.CENTER);
            
            // Кнопка закрытия
            JButton btnCloseDetails = new JButton("Закрыть");
            btnCloseDetails.addActionListener(e -> detailsDialog.dispose());
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(btnCloseDetails);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            detailsDialog.add(mainPanel);
            detailsDialog.setVisible(true);
        }
    }
}