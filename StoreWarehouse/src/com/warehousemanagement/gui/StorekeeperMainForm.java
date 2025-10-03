package gui;

import model.User;
import service.DataManager;
import javax.swing.*;
import java.awt.*;

public class StorekeeperMainForm extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    
    private JButton btnReceiveGoods;
    private JButton btnProcessOrders;
    private JButton btnPrepareShipment;
    private JButton btnViewProducts;
    private JButton btnExit;

    public StorekeeperMainForm(User user, DataManager dataManager) {
        this.currentUser = user;
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Система Магазин-Склад - Кладовщик");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        btnReceiveGoods = new JButton("<html><center>Прием<br>товаров</center></html>");
        btnProcessOrders = new JButton("<html><center>Обработка<br>заказов</center></html>");
        btnPrepareShipment = new JButton("<html><center>Подготовка<br>отгрузки</center></html>");
        btnViewProducts = new JButton("<html><center>Просмотр<br>товаров</center></html>");
        btnExit = new JButton("Выход");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Панель кладовщика - " + currentUser.getLogin(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnReceiveGoods.setFont(buttonFont);
        btnProcessOrders.setFont(buttonFont);
        btnPrepareShipment.setFont(buttonFont);
        btnViewProducts.setFont(buttonFont);
        btnExit.setFont(buttonFont);

        mainPanel.add(btnReceiveGoods);
        mainPanel.add(btnProcessOrders);
        mainPanel.add(btnPrepareShipment);
        mainPanel.add(btnViewProducts);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(" Вошел как: Кладовщик ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnReceiveGoods.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Прием товаров - в разработке");
        });

        btnProcessOrders.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Обработка заказов - в разработке");
        });

        btnPrepareShipment.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Подготовка отгрузки - в разработке");
        });

        btnViewProducts.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Просмотр товаров - в разработке");
        });

        btnExit.addActionListener(e -> System.exit(0));
    }
}