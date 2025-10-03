package gui;

import javax.swing.*;

import model.User;
import service.AuthService;
import service.DataManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame { // класс для входа в систему (база)
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister; // ДОБАВЛЕНО
    private JButton btnExit;
    private AuthService authService;
    private DataManager dataManager;

    public LoginForm(AuthService authService, DataManager dataManager) {
        this.authService = authService;
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Система Магазин-Склад - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350); // УВЕЛИЧИЛИ ВЫСОТУ ДЛЯ НОВОЙ КНОПКИ
        setLocationRelativeTo(null);
        setResizable(false);

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Войти");
        btnRegister = new JButton("Регистрация"); // ДОБАВЛЕНО
        btnExit = new JButton("Выход");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Вход в систему", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        centerPanel.add(new JLabel("Логин:"));
        centerPanel.add(txtUsername);
        centerPanel.add(new JLabel("Пароль:"));
        centerPanel.add(txtPassword);
        centerPanel.add(btnLogin);
        centerPanel.add(btnExit);

        add(centerPanel, BorderLayout.CENTER);

        // НИЖНЯЯ ПАНЕЛЬ С КНОПКОЙ РЕГИСТРАЦИИ
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.add(btnRegister);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> performLogin());
        
        // ДОБАВЛЕНО - обработчик для кнопки регистрации
        btnRegister.addActionListener(e -> openRegisterForm());
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин и пароль", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            openMainForm(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }

    private void openMainForm(User user) {
        switch (user.getRole()) {
            case "admin":
                new AdminMainForm(user, dataManager).setVisible(true);
                break;
            case "seller":
                new SellerMainForm(user, dataManager).setVisible(true);
                break;
            case "storekeeper":
                new StorekeeperMainForm(user, dataManager).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Неизвестная роль", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ДОБАВЛЕНО - метод для открытия формы регистрации
    private void openRegisterForm() {
        RegisterForm registerForm = new RegisterForm(dataManager, this);
        registerForm.setVisible(true);
    }
}