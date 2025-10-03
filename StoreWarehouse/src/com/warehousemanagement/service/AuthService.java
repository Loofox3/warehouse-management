package service;

import model.User;

public class AuthService {
    private DataManager dataManager;
    
    public AuthService(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ДОБАВИТЬ этот метод
    public User authenticate(String login, String password) {
        User user = dataManager.getUsers().get(login);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}