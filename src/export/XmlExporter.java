package export;

import library.Library;
import model.Media;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class XmlExporter implements Exporter {

    @Override
    public String formatName() {
        return "XML";
    }

    @Override
    public void export(Library library, String filePath) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            Map<Integer, Integer> counts = library.getAccessCountByMediaId();

            w.write("<?xml version=\"1.0\"?>\n");
            w.write("<library>\n");
            w.write("  <mediaList>\n");

            for (Media m : library.getMediaById().values()) {
                int acc = counts.getOrDefault(m.getId(), 0);

                w.write("    <media id=\"" + m.getId() + "\" type=\"" + esc(m.getType()) + "\">\n");
                w.write("      <title>" + esc(m.getTitle()) + "</title>\n");
                w.write("      <author>" + esc(m.getAuthor()) + "</author>\n");
                w.write("      <year>" + m.getYear() + "</year>\n");
                w.write("      <description>" + esc(m.getDescription()) + "</description>\n");
                w.write("      <subjects>\n");
                for (String code : m.getSubjectCodes()) {
                    w.write("        <subject>" + esc(code) + "</subject>\n");
                }
                w.write("      </subjects>\n");
                w.write("      <accesses>" + acc + "</accesses>\n");
                w.write("    </media>\n");
            }

            w.write("  </mediaList>\n");
            w.write("</library>\n");
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
