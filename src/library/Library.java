package library;

import model.Media;
import model.Speciality;
import model.Student;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;


public class Library implements Serializable {
    private final Map<String, Student> studentsByUsername = new LinkedHashMap<>();
    private final Map<String, Speciality> specialitiesByName = new LinkedHashMap<>();
    private final Map<Integer, Media> mediaById = new LinkedHashMap<>();
    private final Map<Integer, Integer> accessCountByMediaId = new HashMap<>();


    public Map<String, Student> getStudentsByUsername() { return studentsByUsername; }
    public Map<String, Speciality> getSpecialitiesByName() { return specialitiesByName; }
    public Map<Integer, Media> getMediaById() { return mediaById; }
    public Map<Integer, Integer> getAccessCountByMediaId() { return accessCountByMediaId; }


    public Speciality getOrCreateSpeciality(String name) {
        return specialitiesByName.computeIfAbsent(name, Speciality::new);
    }
}
