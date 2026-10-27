package model;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    private final String username;
    private final String password;
    private final String specialityName;
    private final List<String> subjectCodes;

    public Student(String username, String password, String specialityName, List<String> subjectCodes) {
        this.username = username;
        this.password = password;
        this.specialityName = specialityName;
        this.subjectCodes = subjectCodes;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSpecialityName() { return specialityName; }
    public List<String> getSubjectCodes() { return subjectCodes; }
}
