package models;

public class Applicant {
    private String userId;
    private String applicationId;
    private String dateOfApplication;
    private String name;
    private String lastName;
    private String nationality;
    private String dob; 
    private String ukprn;
    private String certificate;
    private String institution;
    private String grade;
    private String docDate;
    private String passportPath;
    private String diplomaPath;
    private String status;
    private String profilePicture;

    public Applicant(String userId, String applicationId, String dateOfApplication, String name, String lastName,
                     String nationality, String dob, String ukprn, String certificate, String institution, String grade,
                     String docDate,
                     String passportPath, String diplomaPath, String status, String profilePicture) {
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
        this.profilePicture = profilePicture;
    }
    
    public Applicant(String userId, String name, String applicationId, String dateOfApplication, String certificate, String grade, String ukprn, String status) {
    	this.userId = userId;
        this.name = name;
        this.applicationId = applicationId;
        this.dateOfApplication = dateOfApplication;
        this.certificate = certificate;
        this.grade = grade;
        this.ukprn = ukprn;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getApplicationId() { return applicationId; }
    public String getDateOfApplication() { return dateOfApplication; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getNationality() { return nationality; }
    public String getDob() { return dob; }
    public String getUkprn() { return ukprn; }
    public String getCertificate() { return certificate; }
    public String getPreviousInstitution() { return institution; }
    public String getGrade() { return grade; }
    public String getDocDate() { return docDate; }
    public String getPassportPath() { return passportPath; }
    public String getDiplomaPath() { return diplomaPath; }
    public String getStatus() { return status; }
    public String getProfilePicture() {return profilePicture; }
    
    public void setUserId(String userId) { this.userId = userId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public void setDateOfApplication(String dateOfApplication) { this.dateOfApplication = dateOfApplication; }
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setDob(String dob) { this.dob = dob; }
    public void setUkprn(String ukprn) { this.ukprn = ukprn; }
    public void setCertificate(String certificate) { this.certificate = certificate; }
    public void setInstitution(String institution) { this.institution = institution; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setDocDate(String docDate) { this.docDate = docDate; }
    public void setPassportPath(String passportPath) { this.passportPath = passportPath; }
    public void setDiplomaPath(String diplomaPath) { this.diplomaPath = diplomaPath; }
    public void setStatus(String status) { this.status = status; }
	public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
