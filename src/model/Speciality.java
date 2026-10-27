package model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Speciality implements Serializable {
    private final String name;
    private final Map<String, Subject> subjectsByCode = new LinkedHashMap<>();

    public Speciality(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Map<String, Subject> getSubjectsByCode() { return subjectsByCode; }

    public Subject getOrCreateSubject(String code) {
        return subjectsByCode.computeIfAbsent(code, c -> new Subject(c, name));
    }
}
