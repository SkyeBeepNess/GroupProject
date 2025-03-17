package session;

public class UserSession {
    private static UserSession instance;
    private String userId;
    private String name;
    private String role;

    private UserSession(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public static void createSession(String userId, String username, String role) {
        instance = new UserSession(userId, username, role);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clearSession() {
        instance = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
