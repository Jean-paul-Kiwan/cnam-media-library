package export;

import library.Library;
import model.Media;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter implements Exporter {

    @Override
    public String formatName() {
        return "CSV";
    }

    @Override
    public void export(Library library, String filePath) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            // Header
            w.write("id,title,author,year,type,subjects,accesses");
            w.newLine();

            Map<Integer, Integer> counts = library.getAccessCountByMediaId();

            for (Media m : library.getMediaById().values()) {
                int acc = counts.getOrDefault(m.getId(), 0);
                String subjects = String.join("|", m.getSubjectCodes());

                // Escape quotes by doubling them, wrap in quotes if needed
                w.write(csv(m.getId() + "") + "," +
                        csv(m.getTitle()) + "," +
                        csv(m.getAuthor()) + "," +
                        csv(m.getYear() + "") + "," +
                        csv(m.getType()) + "," +
                        csv(subjects) + "," +
                        csv(acc + ""));
                w.newLine();
            }
        }
    }

    private String csv(String s) {
        if (s == null) return "";
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
