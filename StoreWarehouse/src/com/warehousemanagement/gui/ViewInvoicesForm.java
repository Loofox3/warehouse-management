package gui;

import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;

import javax.swing.table.DefaultTableModel;

import model.Invoice;
import service.DataManager;

// работа с накладными
public class ViewInvoicesForm extends JFrame {
    private DataManager dataManager;
    private JTable invoicesTable;
    private JButton btnClose;
    private JButton btnCreateNew;

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
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Создаем таблицу для накладных
        String[] columnNames = {"ID", "Дата", "Статус", "Кол-во позиций", "Общая сумма"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(model);
        
        btnClose = new JButton("Закрыть");
        btnCreateNew = new JButton("Создать новую");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Накладные", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Таблица с прокруткой
        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnCreateNew);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnClose.addActionListener(e -> dispose());
        btnCreateNew.addActionListener(e -> createNewInvoice());
    }

    private void loadInvoicesData() {
        DefaultTableModel model = (DefaultTableModel) invoicesTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Invoice invoice : dataManager.getInvoices().values()) {
            int itemsCount = invoice.getItems().size();
            double totalAmount = invoice.getTotalAmount();
            
            Object[] rowData = {
                invoice.getId(),
                dateFormat.format(invoice.getDate()),
                invoice.getStatus().getDisplayName(),
                itemsCount + " шт.",
                String.format("%.2f руб.", totalAmount)
            };
            model.addRow(rowData);
        }
    }

    private void createNewInvoice() {
    CreateInvoiceForm createInvoiceForm = new CreateInvoiceForm(dataManager);
    createInvoiceForm.setVisible(true);
    dispose(); // Закрываем текущую форму
    }
}
