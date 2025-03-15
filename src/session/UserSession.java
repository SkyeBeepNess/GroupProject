package session;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String name;
    private String role;

    private UserSession(int userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public static void createSession(int userId, String username, String role) {
        instance = new UserSession(userId, username, role);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clearSession() {
        instance = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
