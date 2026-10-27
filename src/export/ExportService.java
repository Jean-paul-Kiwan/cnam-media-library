package export;

import library.Library;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExportService {
    private final Map<String, Exporter> exporters = new HashMap<>();

    public void register(Exporter exporter) {
        exporters.put(exporter.formatName().toLowerCase(), exporter);
    }

    public void export(Library library, String format, String path) throws IOException {
        Exporter ex = exporters.get(format.toLowerCase());
        if (ex == null) throw new IllegalArgumentException("Unknown export format: " + format);
        ex.export(library, path);
    }
}
