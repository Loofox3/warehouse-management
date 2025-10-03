package gui;
// класс для просмотра товаров
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Product;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import service.DataManager;

public class ViewProductsForm extends JFrame{
    private DataManager dataManager;
    private JTable productsTable; //Конструктор таблицы с инициализацией объекта моделями по умолчанию : моделью данных, моделью колонок, моделью выделения.
    private JButton btnClose;

    public ViewProductsForm(DataManager dataManager){
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadProductsData();
    }
    private void initializeComponents(){
        setTitle("Просмотр товаров");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        //Таблица
        String[] columnNames = {"ID", "Название", "Цена", "На складе", "В магазине"};
        DefaultTableModel model = new DefaultTableModel(columnNames,0); // для хранения и управления данными, которые будут отображаться в компоненте JTable.
        productsTable = new JTable(model);
        btnClose = new JButton("Закрыть");
    }
    private void setupLayout(){
        setLayout(new BorderLayout());
        //Заголовок
        JLabel lblTitle = new JLabel("Список товаров", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18)); //Название шрифта, Курсив и Шрифт
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10,0,10,0)); // Отступы к заголовку.
        add(lblTitle, BorderLayout.NORTH);//Размещение заголовка в верхней части.

        //Таблица с прокуруткой.
        JScrollPane scrollPane = new JScrollPane(productsTable); //возможность прокручивать таблицу
        add(scrollPane, BorderLayout.CENTER); // добавление в окно по центру


        //Панель кнопок
        JPanel buttoPanel = new JPanel(new FlowLayout());
        buttoPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        buttoPanel.add(btnClose);
        add(buttoPanel, BorderLayout.SOUTH);//добавление нопок снизу
    }

    private void setupListeners(){ //закрытие окна и возвращение в главное меню
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e ){
                dispose();
            }
        });
    }
    private void loadProductsData(){ // метод для просмотра товаров, он вызывается при открытии окна товаров
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0); // очищаем таблицу

        for(Product product : dataManager.getProducts().values()){
            Object[] rowData = {product.getId(),
                product.getName(),
                product.getPrice() + " руб.",
                product.getQuantityInWarehouse(),
                product.getQuantityInShop()};
            model.addRow(rowData);    

        }
    }
}
