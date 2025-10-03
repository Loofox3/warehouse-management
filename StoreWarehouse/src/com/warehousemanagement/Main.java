

import service.AuthService;
import service.DataManager;
import gui.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            DataManager dataManager = new DataManager();
            AuthService authService = new AuthService(dataManager);
            
            boolean loaded = dataManager.loadAllData();
            if (!loaded) {
                JOptionPane.showMessageDialog(null, "Ошибка загрузки данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            new LoginForm(authService, dataManager).setVisible(true);
        });
    }
}