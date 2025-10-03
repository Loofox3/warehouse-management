package gui;

import service.DataManager;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterForm extends JFrame {
    private DataManager dataManager;
    
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> comboRole;
    private JButton btnRegister;
    private JButton btnCancel;
    
    public RegisterForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        setupValidation();
    }
    
    private void initializeComponents() {
        setTitle("Регистрация пользователя");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        txtLogin = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtConfirmPassword = new JPasswordField(15);
        
        String[] roles = {"SELLER", "ADMIN", "WAREHOUSE_MANAGER"};
        comboRole = new JComboBox<>(roles);
        
        btnRegister = new JButton("Зарегистрировать");
        btnCancel = new JButton("Отмена");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Регистрация нового пользователя", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        inputPanel.add(new JLabel("Логин:*"));
        inputPanel.add(txtLogin);
        inputPanel.add(new JLabel("Пароль:*"));
        inputPanel.add(txtPassword);
        inputPanel.add(new JLabel("Подтверждение:*"));
        inputPanel.add(txtConfirmPassword);
        inputPanel.add(new JLabel("Роль:*"));
        inputPanel.add(comboRole);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        btnRegister.addActionListener(e -> performRegistration());
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void setupValidation() {
        // Валидация логина - только буквы и цифры
        txtLogin.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isLetterOrDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }
    
    private void performRegistration() {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();
        
        // Валидация обязательных полей
        if (login.isEmpty()) {
            showError("Введите логин");
            txtLogin.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Введите пароль");
            txtPassword.requestFocus();
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            showError("Подтвердите пароль");
            txtConfirmPassword.requestFocus();
            return;
        }
        
        // Дополнительная валидация
        if (login.length() < 3) {
            showError("Логин должен содержать минимум 3 символа");
            txtLogin.requestFocus();
            return;
        }
        
        if (password.length() < 3) {
            showError("Пароль должен содержать минимум 3 символа");
            txtPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Пароли не совпадают");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            txtPassword.requestFocus();
            return;
        }
        
        if (dataManager.getUsers().containsKey(login)) {
            showError("Пользователь с таким логином уже существует");
            txtLogin.requestFocus();
            return;
        }
        
        // Создаем нового пользователя
        int newId = dataManager.getNextUserId();
        User newUser = new User(newId, login, password, role);
        
        dataManager.addUser(newUser);
        
        JOptionPane.showMessageDialog(this, 
            "✅ Пользователь успешно зарегистрирован!\nЛогин: " + login + "\nРоль: " + role, 
            "Успех", JOptionPane.INFORMATION_MESSAGE);
            
        dispose();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
    }
}