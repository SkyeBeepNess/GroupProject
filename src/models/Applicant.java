package models;

public class Applicant {
    private String userId;
    private String applicationId;
    private String date;
    private String name;
    private String certificate;
    private String grade;
    private int ukprn;
    private String status;

    public Applicant(String userId, String applicationId, String date, String name, String certificate, String grade, int ukprn, String status) {
        this.userId = userId;
        this.applicationId = applicationId;
        this.date = date;
        this.name = name;
        this.certificate = certificate;
        this.grade = grade;
        this.ukprn = ukprn;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getApplicationId() { return applicationId; }
    public String getDate() { return date; }
    public String getName() { return name; }
    public String getCertificate() { return certificate; }
    public String getGrade() { return grade; }
    public int getUkprn() { return ukprn; }
    public String getStatus() { return status; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public void setDate(String date) { this.date = date; }
    public void setName(String name) { this.name = name; }
    public void setCertificate(String certificate) { this.certificate = certificate; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setUkprn(int ukprn) { this.ukprn = ukprn; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Applicant{" +
                "userId='" + userId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", certificate='" + certificate + '\'' +
                ", grade='" + grade + '\'' +
                ", ukprn=" + ukprn +
                ", status='" + status + '\'' +
                '}';
    }
}
