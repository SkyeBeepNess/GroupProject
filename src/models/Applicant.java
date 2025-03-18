package models;

public class Applicant {
    private String userId;
    private String applicationId;
    private String dateOfApplication;
    private String name;
    private String certificate;
    private String grade;
    private int ukprn;
    private String status;
    private String previousInstitution;
    private String passportPath;
    private String diplomaPath;

    public Applicant(String userId, String applicationId, String dateOfApplication, String name,
                     String certificate, String grade, int ukprn, String status, 
                     String previousInstitution, String passportPath, String diplomaPath) {
        this.userId = userId;
        this.applicationId = applicationId;
        this.dateOfApplication = dateOfApplication;
        this.name = name;
        this.certificate = certificate;
        this.grade = grade;
        this.ukprn = ukprn;
        this.status = status;
        this.previousInstitution = previousInstitution;
        this.passportPath = passportPath;
        this.diplomaPath = diplomaPath;
    }

    public String getUserId() {
        return userId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getDateOfApplication() {
        return dateOfApplication;
    }

    public String getName() {
        return name;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getGrade() {
        return grade;
    }

    public int getUkprn() {
        return ukprn;
    }

    public String getStatus() {
        return status;
    }

    public String getPreviousInstitution() {
        return previousInstitution;
    }

    public String getPassportPath() {
        return passportPath;
    }

    public String getDiplomaPath() {
        return diplomaPath;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setDateOfApplication(String dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setUkprn(int ukprn) {
        this.ukprn = ukprn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPreviousInstitution(String previousInstitution) {
        this.previousInstitution = previousInstitution;
    }

    public void setPassportPath(String passportPath) {
        this.passportPath = passportPath;
    }

    public void setDiplomaPath(String diplomaPath) {
        this.diplomaPath = diplomaPath;
    }
}
