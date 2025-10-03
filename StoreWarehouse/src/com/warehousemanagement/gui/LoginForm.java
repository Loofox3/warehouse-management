package gui;

import model.User;
import service.AuthService;
import service.DataManager;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private AuthService authService;
    private DataManager dataManager;
    
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnCancel;
    
    public LoginForm(AuthService authService, DataManager dataManager) {
        this.authService = authService;
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initializeComponents() {
        setTitle("Вход в систему");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250); // Увеличили высоту для новой кнопки
        setLocationRelativeTo(null);
        setResizable(false);
        
        txtLogin = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Вход");
        btnRegister = new JButton("Регистрация");
        btnCancel = new JButton("Отмена");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Заголовок
        JLabel lblTitle = new JLabel("Система Магазин-Склад", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Панель ввода
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        inputPanel.add(new JLabel("Логин:"));
        inputPanel.add(txtLogin);
        inputPanel.add(new JLabel("Пароль:"));
        inputPanel.add(txtPassword);
        
        add(inputPanel, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnLogin.addActionListener(e -> performLogin());
        btnRegister.addActionListener(e -> openRegistration());
        btnCancel.addActionListener(e -> System.exit(0));
        
        // Enter для логина
        txtPassword.addActionListener(e -> performLogin());
    }
    private void performLogin() {
    String login = txtLogin.getText().trim();
    String password = new String(txtPassword.getPassword());
    
    // Валидация
    if (login.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Введите логин", "Ошибка", JOptionPane.ERROR_MESSAGE);
        txtLogin.requestFocus();
        return;
    }
    
    if (password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Введите пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
        txtPassword.requestFocus();
        return;
    }
    
    User user = authService.authenticate(login, password);
    if (user != null) {
        dispose();
        
        switch (user.getRole()) {
            case "SELLER":
                new SellerMainForm(user, dataManager).setVisible(true);
                break;
            case "ADMIN":
                new AdminMainForm(user, dataManager).setVisible(true);
                break;
            case "WAREHOUSE_MANAGER":
                new StorekeeperMainForm(user, dataManager).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Неизвестная роль пользователя", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Неверный логин или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}
    
    private void openRegistration() {
        RegisterForm registerForm = new RegisterForm(dataManager);
        registerForm.setVisible(true);
    }
}