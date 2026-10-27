package observer;

import model.Media;
import model.Student;

import java.util.List;

public class EmailNotifier implements LibraryObserver {

    @Override
    public void onMediaAdded(Media media, List<Student> concernedStudents) {
        for (Student s : concernedStudents) {
            System.out.println("EMAIL sent to " + s.getUsername()
                    + " about new media: " + media.getTitle());
        }
    }
}
