package gui;
// класс для администратора
import javax.swing.*;
import service.DataManager;
import model.User;
import service.DataManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.ViewProductsForm;
import gui.AddProductForm;
import gui.ManageProductsForm;
public class AdminMainForm extends JFrame {
    private User currentUser;
    private DataManager dataManager;

    // Объявляем кнопки как поля класса
    private JButton btnViewProducts;
    private JButton btnAddProduct;
    private JButton btnManageProducts;
    private JButton btnManageUsers;
    private JButton btnViewInvoices;
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
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Инициализация кнопок
        btnViewProducts = new JButton("<html><center>Просмотр<br>товаров</center></html>");
        btnAddProduct = new JButton("<html><center>Добавить<br>товар</center></html>");
        btnManageProducts = new JButton("<html><center>Управление<br>товарами</center></html>");
        btnManageUsers = new JButton("<html><center>Управление<br>пользователями</center></html>");
        btnViewInvoices = new JButton("<html><center>Просмотр<br>накладных</center></html>");
        btnExit = new JButton("Выход");

        // Настройка шрифтов кнопок
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnViewProducts.setFont(buttonFont);
        btnAddProduct.setFont(buttonFont);
        btnManageProducts.setFont(buttonFont);
        btnManageUsers.setFont(buttonFont);
        btnViewInvoices.setFont(buttonFont);
        btnExit.setFont(buttonFont);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Панель администратора - " + currentUser.getLogin(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Основная панель с кнопками
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(btnViewProducts);
        mainPanel.add(btnAddProduct);
        mainPanel.add(btnManageProducts);
        mainPanel.add(btnManageUsers);
        mainPanel.add(btnViewInvoices);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        // Статус бар
        JLabel lblStatus = new JLabel(" Вошел как: Администратор ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
    // Обработчики для каждой кнопки отдельно
    
    // Просмотр товаров - ОБНОВЛЕНО!
    btnViewProducts.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ViewProductsForm viewProductsForm = new ViewProductsForm(dataManager);
            viewProductsForm.setVisible(true);
        }
    });

    // Добавить товар - ОБНОВЛЕНО!
    btnAddProduct.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        AddProductForm addProductForm = new AddProductForm(dataManager);
        addProductForm.setVisible(true);
    }
});
    
    // Управление товарами
    btnManageProducts.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        ManageProductsForm manageProductsForm = new ManageProductsForm(dataManager);
        manageProductsForm.setVisible(true);
    }
});
    
    // Управление пользователями
    btnManageUsers.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showUserManagement();
        }
    });
    
    // Просмотр накладных - ОБНОВЛЕНО!
btnViewInvoices.addActionListener(e -> {
    ViewInvoicesForm viewInvoicesForm = new ViewInvoicesForm(dataManager);
    viewInvoicesForm.setVisible(true);
});
    
    // Выход
    btnExit.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    });
}

    private void showUserManagement() {
        StringBuilder usersList = new StringBuilder("Зарегистрированные пользователи:\n\n");
        
        for (User user : dataManager.getUsers().values()) {
            usersList.append("Логин: ").append(user.getLogin())
                    .append(" | Роль: ").append(user.getRole())
                    .append("\n");
        }
        
        JOptionPane.showMessageDialog(this, 
            usersList.toString(), 
            "Управление пользователями", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}