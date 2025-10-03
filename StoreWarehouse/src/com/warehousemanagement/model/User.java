package model;

public class User {
    private String login;
    private String password;
    private String role;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getLogin() { return login; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return login + " (" + role + ")";
    }
}