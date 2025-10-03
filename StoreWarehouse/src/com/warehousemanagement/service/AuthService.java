package service;

import model.*;
import java.util.Map;

public class AuthService {
    private final DataManager dataManager;
    
    public AuthService(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public User login(String username, String password) {
        Map<String, User> users = dataManager.getUsers();
        User user = users.get(username);
        
        if (user != null && user.authenticate(password)) {
            return user;
        }
        return null;
    }
}