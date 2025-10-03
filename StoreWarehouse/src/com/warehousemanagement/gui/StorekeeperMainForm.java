package gui;

import javax.swing.*;

import model.User;
import service.DataManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StorekeeperMainForm extends JFrame { // класс для кладовщика
    private User currentUser;
    private DataManager dataManager;

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
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Панель кладовщика - " + currentUser.getLogin(), JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnViewWarehouse = new JButton("<html><center>Просмотр<br>склада</center></html>");
        JButton btnReceiveGoods = new JButton("<html><center>Приемка<br>товаров</center></html>");
        JButton btnManageStock = new JButton("<html><center>Управление<br>запасами</center></html>");
        JButton btnExit = new JButton("Выход");

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnViewWarehouse.setFont(buttonFont);
        btnReceiveGoods.setFont(buttonFont);
        btnManageStock.setFont(buttonFont);
        btnExit.setFont(buttonFont);

        mainPanel.add(btnViewWarehouse);
        mainPanel.add(btnReceiveGoods);
        mainPanel.add(btnManageStock);
        mainPanel.add(btnExit);

        add(mainPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(" Вошел как: Кладовщик ");
        lblStatus.setBorder(BorderFactory.createRaisedBevelBorder());
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        ActionListener tempHandler = e -> {
            JButton source = (JButton) e.getSource();
            JOptionPane.showMessageDialog(this, 
                "Функция кладовщика: " + source.getText().replaceAll("<.*?>", ""), 
                "Информация", 
                JOptionPane.INFORMATION_MESSAGE);
        };

        for (Component comp : ((JPanel)getContentPane().getComponent(1)).getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(tempHandler);
            }
        }
    }
}