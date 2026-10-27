package persistence;

import library.Library;

import java.io.*;

public class LibraryPersistence {

    public void save(Library library, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(library);
        }
    }

    public Library load(String filePath) throws IOException, ClassNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (Library) in.readObject();
        }
    }
}
