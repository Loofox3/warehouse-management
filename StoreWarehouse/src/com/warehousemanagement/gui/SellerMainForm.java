package gui;

import model.User;
import service.DataManager;
import javax.swing.*;
import java.awt.*;

public class SellerMainForm extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    
    private JButton btnSellProducts;
    private JButton btnViewShopProducts; // НОВАЯ КНОПКА
    private JButton btnCreateOrder;
    private JButton btnReceiveGoods;
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
        
        btnSellProducts = new JButton("<html><center>Продажа<br>товаров</center></html>");
        btnViewShopProducts = new JButton("<html><center>Просмотр<br>товаров в магазине</center></html>"); // НОВАЯ КНОПКА
        btnCreateOrder = new JButton("<html><center>Заказ товаров<br>со склада</center></html>");
        btnReceiveGoods = new JButton("<html><center>Прием товаров<br>со склада</center></html>");
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

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnSellProducts.setFont(buttonFont);
        btnViewShopProducts.setFont(buttonFont); // НОВАЯ КНОПКА
        btnCreateOrder.setFont(buttonFont);
        btnReceiveGoods.setFont(buttonFont);
        btnExit.setFont(buttonFont);

        mainPanel.add(btnSellProducts);
        mainPanel.add(btnViewShopProducts); // НОВАЯ КНОПКА
        mainPanel.add(btnCreateOrder);
        mainPanel.add(btnReceiveGoods);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(" Вошел как: Продавец ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnSellProducts.addActionListener(e -> {
            SellProductsForm sellForm = new SellProductsForm(dataManager);
            sellForm.setVisible(true);
        });

        // НОВЫЙ ОБРАБОТЧИК - просмотр товаров в магазине
        btnViewShopProducts.addActionListener(e -> {
            ViewShopProductsForm viewForm = new ViewShopProductsForm(dataManager);
            viewForm.setVisible(true);
        });

        btnCreateOrder.addActionListener(e -> {
            CreateInvoiceForm orderForm = new CreateInvoiceForm(dataManager);
            orderForm.setVisible(true);
        });

        btnReceiveGoods.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Прием товаров со склада - в разработке");
        });

        btnExit.addActionListener(e -> System.exit(0));
    }
}