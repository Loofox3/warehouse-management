package gui;

import model.User;
import service.DataManager;
import javax.swing.*;
import java.awt.*;

public class AdminMainForm extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    
    private JButton btnManageProducts;
    private JButton btnViewReports;
    private JButton btnManageUsers;
    private JButton btnExit;

    public AdminMainForm(User user, DataManager dataManager) {
        this.currentUser = user;
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Система Магазин-Склад - Администратор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        btnManageProducts = new JButton("<html><center>Управление<br>товарами</center></html>");
        btnViewReports = new JButton("<html><center>Просмотр<br>отчетов</center></html>");
        btnManageUsers = new JButton("<html><center>Управление<br>пользователями</center></html>");
        btnExit = new JButton("Выход");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Панель администратора - " + currentUser.getLogin(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnManageProducts.setFont(buttonFont);
        btnViewReports.setFont(buttonFont);
        btnManageUsers.setFont(buttonFont);
        btnExit.setFont(buttonFont);

        mainPanel.add(btnManageProducts);
        mainPanel.add(btnViewReports);
        mainPanel.add(btnManageUsers);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(" Вошел как: Администратор ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnManageProducts.addActionListener(e -> {
            ManageProductsForm manageForm = new ManageProductsForm(dataManager);
            manageForm.setVisible(true);
        });

        btnViewReports.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Просмотр отчетов - в разработке");
        });

        btnManageUsers.addActionListener(e -> {
            // Можно создать отдельную форму для управления пользователями
            JOptionPane.showMessageDialog(this, "Управление пользователями - в разработке");
        });

        btnExit.addActionListener(e -> System.exit(0));
    }
}