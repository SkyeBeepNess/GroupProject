package university.management;

import javafx.beans.property.SimpleStringProperty;

public class Applicant {
    private final SimpleStringProperty name;
    private final SimpleStringProperty date;
    private final SimpleStringProperty certificate;
    private final SimpleStringProperty grade;
    private final SimpleStringProperty status;

    public Applicant(String name, String date, String certificate, String grade, String status) {
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.certificate = new SimpleStringProperty(certificate);
        this.grade = new SimpleStringProperty(grade);
        this.status = new SimpleStringProperty(status);
    }

    public String getName() { return name.get(); }
    public String getDate() { return date.get(); }
    public String getCertificate() { return certificate.get(); }
    public String getGrade() { return grade.get(); }
    public String getStatus() { return status.get(); }

    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty certificateProperty() { return certificate; }
    public SimpleStringProperty gradeProperty() { return grade; }
    public SimpleStringProperty statusProperty() { return status; }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
}
