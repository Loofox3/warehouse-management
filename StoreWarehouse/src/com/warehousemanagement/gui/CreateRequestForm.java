package gui;

import service.DataManager;
import javax.swing.*;
import java.awt.*;

public class CreateRequestForm extends JFrame {
    private DataManager dataManager;

    public CreateRequestForm(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setTitle("Создание заявки на пополнение");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("Создание заявки на пополнение товаров", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.add(new JLabel("Форма создания заявки в разработке..."));
        add(contentPanel, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Закрыть");
        btnClose.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}