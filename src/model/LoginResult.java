package model;

public class LoginResult {
    private User user;
    private UserRole role;

    public LoginResult(User user, UserRole role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public UserRole getRole() {
        return role;
    }
}
