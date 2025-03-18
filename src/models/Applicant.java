package models;

public class Applicant {
    private String userId;
    private String applicationId;
    private String dateOfApplication;
    private String name;
    private String lastName;
    private String nationality;
    private String dob; 
    private int ukprn;
    private String certificate;
    private String institution;
    private String grade;
    private String docDate;
    private String passportPath;
    private String diplomaPath;
    private String status;

    public Applicant(String userId, String applicationId, String dateOfApplication, String name, String lastName,
                     String nationality, String dob, int ukprn, String certificate, String institution, String grade,
                     String docDate,
                     String passportPath, String diplomaPath, String status) {
        this.userId = userId;
        this.applicationId = applicationId;
        this.dateOfApplication = dateOfApplication;
        this.name = name;
        this.lastName = lastName;
        this.nationality = nationality;
        this.dob = dob;
        this.ukprn = ukprn;
        this.certificate = certificate;
        this.institution = institution;
        this.grade = grade;
        this.docDate = docDate;
        this.passportPath = passportPath;
        this.diplomaPath = diplomaPath;
        this.status = status;
    }
    
    public Applicant(String userId, String name, String applicationId, String dateOfApplication, String certificate, String grade, String status) {
    	this.userId = userId;
        this.name = name;
        this.applicationId = applicationId;
        this.dateOfApplication = dateOfApplication;
        this.certificate = certificate;
        this.grade = grade;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getApplicationId() { return applicationId; }
    public String getDateOfApplication() { return dateOfApplication; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getNationality() { return nationality; }
    public String getDob() { return dob; }
    public String getCertificate() { return certificate; }
    public String getPreviousInstitution() { return institution; }
    public String getGrade() { return grade; }
    public String getDocDate() { return docDate; }
    public String getPassportPath() { return passportPath; }
    public String getDiplomaPath() { return diplomaPath; }
    public String getStatus() { return status; }
    
    public void setDob(String dob) { this.dob = dob; }
}
