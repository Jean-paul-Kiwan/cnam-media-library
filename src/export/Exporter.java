package export;

import library.Library;
import java.io.IOException;

public interface Exporter {
    void export(Library library, String filePath) throws IOException;
    String formatName();
}
