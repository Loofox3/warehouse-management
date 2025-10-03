package gui;

import service.DataManager;
import model.Invoice;
import model.InvoiceItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StorekeeperInvoicesForm extends JFrame {
    private DataManager dataManager;
    
    private JTable invoicesTable;
    private JButton btnProcess;
    private JButton btnView;
    private JButton btnClose;
    
    public StorekeeperInvoicesForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadInvoicesData();
    }
    
    private void initializeComponents() {
        setTitle("Накладные для обработки");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        
        // Таблица накладных
        String[] columnNames = {"ID", "Тип", "Статус", "Кол-во товаров", "Общая сумма"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(model);
        
        btnProcess = new JButton("Обработать");
        btnView = new JButton("Просмотреть");
        btnClose = new JButton("Закрыть");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Накладные для обработки кладовщиком", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Таблица
        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnProcess);
        buttonPanel.add(btnView);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnProcess.addActionListener(e -> processInvoice());
        btnView.addActionListener(e -> viewInvoice());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void loadInvoicesData() {
        DefaultTableModel model = (DefaultTableModel) invoicesTable.getModel();
        model.setRowCount(0);
        
        for (Invoice invoice : dataManager.getInvoices().values()) {
            Object[] rowData = {
                invoice.getId(),
                invoice.getType(),
                invoice.getStatus(),
                invoice.getItems().size(),
                String.format("%.2f руб.", invoice.getTotalAmount())
            };
            model.addRow(rowData);
        }
    }
    
    private void processInvoice() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите накладную для обработки", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int invoiceId = (int) invoicesTable.getValueAt(selectedRow, 0);
        Invoice invoice = dataManager.getInvoices().get(invoiceId);
        
        if (invoice != null) {
            // Логика обработки накладной
            invoice.setStatus("PROCESSING");
            dataManager.saveInvoices();
            loadInvoicesData();
            
            JOptionPane.showMessageDialog(this, 
                "Накладная #" + invoiceId + " взята в обработку", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void viewInvoice() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите накладную для просмотра", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int invoiceId = (int) invoicesTable.getValueAt(selectedRow, 0);
        Invoice invoice = dataManager.getInvoices().get(invoiceId);
        
        if (invoice != null) {
            StringBuilder details = new StringBuilder();
            details.append("Накладная #").append(invoiceId).append("\n");
            details.append("Тип: ").append(invoice.getType()).append("\n");
            details.append("Статус: ").append(invoice.getStatus()).append("\n\n");
            details.append("Товары:\n");
            
            for (InvoiceItem item : invoice.getItems()) {
                details.append("- ").append(item.getProduct().getName())
                      .append(": ").append(item.getQuantity())
                      .append(" шт.\n");
            }
            
            details.append("\nОбщая сумма: ").append(String.format("%.2f руб.", invoice.getTotalAmount()));
            
            JOptionPane.showMessageDialog(this, details.toString(), "Детали накладной", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}