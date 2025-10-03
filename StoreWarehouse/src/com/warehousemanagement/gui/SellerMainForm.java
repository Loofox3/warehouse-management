package gui;

import javax.swing.*;
import model.User;
import service.DataManager;
import java.awt.*;

public class SellerMainForm extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    
    // Объявляем кнопки как поля класса
    private JButton btnSellProducts;
    private JButton btnViewProducts;
    private JButton btnCreateInvoice;
    private JButton btnExit;

    public SellerMainForm(User user, DataManager dataManager) {
        this.currentUser = user;
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Система Магазин-Склад - Продавец");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        // Инициализируем кнопки
        btnSellProducts = new JButton("<html><center>Продажа<br>товаров</center></html>");
        btnViewProducts = new JButton("<html><center>Просмотр<br>товаров</center></html>");
        btnCreateInvoice = new JButton("<html><center>Создать<br>накладную</center></html>");
        btnExit = new JButton("Выход");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Панель продавца - " + currentUser.getLogin(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Устанавливаем шрифт для кнопок
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnSellProducts.setFont(buttonFont);
        btnViewProducts.setFont(buttonFont);
        btnCreateInvoice.setFont(buttonFont);
        btnExit.setFont(buttonFont);

        // Добавляем кнопки на панель
        mainPanel.add(btnSellProducts);
        mainPanel.add(btnViewProducts);
        mainPanel.add(btnCreateInvoice);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(" Вошел как: Продавец ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        // Оформление продаж
        btnSellProducts.addActionListener(e -> {
            SellProductsForm sellForm = new SellProductsForm(dataManager);
            sellForm.setVisible(true);
        });

        // Создание накладной
        btnCreateInvoice.addActionListener(e -> {
            CreateInvoiceForm invoiceForm = new CreateInvoiceForm(dataManager);
            invoiceForm.setVisible(true);
        });

        // Просмотр товаров
        btnViewProducts.addActionListener(e -> {
            ViewProductsForm viewForm = new ViewProductsForm(dataManager);
            viewForm.setVisible(true);
        });

        // Выход
        btnExit.addActionListener(e -> System.exit(0));
    }
}