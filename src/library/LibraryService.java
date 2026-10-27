package library;

import model.Media;
import model.Speciality;
import model.Subject;
import filter.MediaFilter;  // add at top with imports
import observer.LibraryObserver;
import model.Student;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LibraryService {

    private final Library library;
    private final java.util.List<LibraryObserver> observers = new java.util.ArrayList<>();


    
    public LibraryService(Library library) {
        this.library = library;
    }

    // ---------- CRUD ----------
    public void addMedia(Media media) {
        if (library.getMediaById().containsKey(media.getId())) {
            throw new IllegalArgumentException("Media id already exists: " + media.getId());
        }
        library.getMediaById().put(media.getId(), media);

        // Link media to each subject (create subject if missing in its speciality)
        for (String code : media.getSubjectCodes()) {
            Subject subject = findOrCreateSubject(code);
            subject.addMedia(media.getId());
        }
        notifyMediaAdded(media);
    }

    public void updateMedia(Media media) {
        if (!library.getMediaById().containsKey(media.getId())) {
            throw new IllegalArgumentException("Media id not found: " + media.getId());
        }
        library.getMediaById().put(media.getId(), media);
    }

    public void removeMedia(int id) {
        Media removed = library.getMediaById().remove(id);
        if (removed == null) throw new IllegalArgumentException("Media id not found: " + id);

        // Unlink from subjects
        for (String code : removed.getSubjectCodes()) {
            Subject s = findSubject(code);
            if (s != null) s.removeMedia(id);
        }
    }

    public void openMedia(String username, int mediaId) {
    // auth check minimal: just ensure student exists
    model.Student s = library.getStudentsByUsername().get(username);
    if (s == null) throw new IllegalArgumentException("Unknown student: " + username);

    model.Media m = library.getMediaById().get(mediaId);
    if (m == null) throw new IllegalArgumentException("Unknown media id: " + mediaId);

    // increment access count
    java.util.Map<Integer, Integer> counts = library.getAccessCountByMediaId();
    counts.put(mediaId, counts.getOrDefault(mediaId, 0) + 1);

    // "open" = display description (project definition)
    System.out.println("\n--- OPEN MEDIA ---");
    System.out.println("Student: " + username);
    System.out.println("Media: [" + m.getId() + "] " + m.getTitle() + " (" + m.getType() + ")");
    System.out.println("Description:\n" + m.getDescription());
    System.out.println("---------------\n");
}


    // ---------- Search ----------
    public Media findById(int id) {
        return library.getMediaById().get(id);
    }

    
    public List<Media> findByTitle(String titlePart) {
        List<Media> res = new ArrayList<>();
        String q = titlePart.toLowerCase();
        for (Media m : library.getMediaById().values()) {
            if (m.getTitle().toLowerCase().contains(q)) res.add(m);
        }
        return res;
    }

    public java.util.List<model.Media> filter(MediaFilter filter) {
    java.util.List<model.Media> res = new java.util.ArrayList<>();
    for (model.Media m : library.getMediaById().values()) {
        if (filter.matches(m, library)) res.add(m);
    }
    return res;
}
    public void addObserver(LibraryObserver obs) {
    observers.add(obs);
}

private void notifyMediaAdded(model.Media media) {
    java.util.List<Student> concerned = new java.util.ArrayList<>();

    for (Student s : library.getStudentsByUsername().values()) {
        for (String code : media.getSubjectCodes()) {
            if (s.getSubjectCodes().contains(code)) {
                concerned.add(s);
                break;
            }
        }
    }

    for (LibraryObserver obs : observers) {
        obs.onMediaAdded(media, concerned);
    }
}


    // ---------- Subject lookup helpers ----------
    private Subject findSubject(String code) {
        for (Speciality sp : library.getSpecialitiesByName().values()) {
            Subject s = sp.getSubjectsByCode().get(code);
            if (s != null) return s;
        }
        return null;
    }

    private Subject findOrCreateSubject(String code) {
        Subject existing = findSubject(code);
        if (existing != null) return existing;

        // If not found, create under a fallback speciality (or you can enforce existence)
        Speciality fallback = library.getOrCreateSpeciality("Unknown");
        return fallback.getOrCreateSubject(code);
    }
}
