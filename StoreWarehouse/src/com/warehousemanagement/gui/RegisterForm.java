package gui;

import model.User;
import service.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterForm extends JFrame {  //класс для регистрации новых полльзователей
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;
    private JButton btnRegister;
    private JButton btnCancel;
    private DataManager dataManager;
    private LoginForm loginForm;

    public RegisterForm(DataManager dataManager, LoginForm loginForm) {
        this.dataManager = dataManager;
        this.loginForm = loginForm;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        setTitle("Регистрация нового пользователя");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        
        // Роли для выбора
        String[] roles = {"admin", "seller", "storekeeper"};
        comboRole = new JComboBox<>(roles);
        
        btnRegister = new JButton("Зарегистрировать");
        btnCancel = new JButton("Отмена");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Заголовок
        JLabel lblTitle = new JLabel("Регистрация нового пользователя", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Центральная панель с полями - УПРОЩЕННАЯ ВЕРСИЯ БЕЗ GridBagConstraints
        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        centerPanel.add(new JLabel("Логин:"));
        centerPanel.add(txtUsername);
        centerPanel.add(new JLabel("Пароль:"));
        centerPanel.add(txtPassword);
        centerPanel.add(new JLabel("Роль:"));
        centerPanel.add(comboRole);
        centerPanel.add(btnRegister);
        centerPanel.add(btnCancel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрыть окно регистрации
            }
        });
    }

    private void registerUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();

        // Валидация
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Заполните все поля", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Логин должен содержать минимум 3 символа", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Пароль должен содержать минимум 3 символа", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Проверка на существующего пользователя
        if (dataManager.getUsers().containsKey(username)) {
            JOptionPane.showMessageDialog(this, 
                "Пользователь с таким логином уже существует", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            txtUsername.setText("");
            txtUsername.requestFocus();
            return;
        }

        // Создание нового пользователя
        User newUser = new User(username, password, role);
        dataManager.getUsers().put(username, newUser);
        
        // Сохранение в файл
        try {
            dataManager.saveUsers();
            JOptionPane.showMessageDialog(this, 
                "Пользователь " + username + " успешно зарегистрирован!\nРоль: " + role, 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Очистка полей
            txtUsername.setText("");
            txtPassword.setText("");
            comboRole.setSelectedIndex(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка сохранения пользователя: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}