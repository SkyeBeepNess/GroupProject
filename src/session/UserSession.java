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
    
    private static String selectedApplicantId;

    public static String getSelectedApplicantId() {
        return selectedApplicantId;
    }

    public static void setSelectedApplicantId(String applicantId) {
        selectedApplicantId = applicantId;
    }
    private static String selectedStudentId;

    public static String getSelectedStudentId() {
        return selectedStudentId;
    }

    public static void setSelectedStudentId(String StudentId) {
    	selectedStudentId = StudentId;
    }
    private Object roleModel;

    public Object getRoleModel() {
        return roleModel;
    }

    public void setRoleModel(Object roleModel) {
        this.roleModel = roleModel;
    }

    
}
