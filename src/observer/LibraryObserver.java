package observer;

import model.Media;
import model.Student;

import java.util.List;

public interface LibraryObserver {
    void onMediaAdded(Media media, List<Student> concernedStudents);
}
