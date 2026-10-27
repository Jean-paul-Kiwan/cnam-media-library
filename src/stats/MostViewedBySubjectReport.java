package stats;

import library.Library;
import model.Media;
import model.Speciality;
import model.Subject;

import java.util.*;

public class MostViewedBySubjectReport implements ReportStrategy {

    @Override
    public List<ReportLine> generate(Library library, String subjectCode) {
        Subject subject = null;

        for (Speciality sp : library.getSpecialitiesByName().values()) {
            Subject s = sp.getSubjectsByCode().get(subjectCode);
            if (s != null) { subject = s; break; }
        }
        if (subject == null) throw new IllegalArgumentException("Unknown subject: " + subjectCode);

        Map<Integer, Integer> counts = library.getAccessCountByMediaId();
        List<ReportLine> lines = new ArrayList<>();

        for (Integer mediaId : subject.getMediaIds()) {
            Media m = library.getMediaById().get(mediaId);
            if (m == null) continue;
            int acc = counts.getOrDefault(mediaId, 0);
            lines.add(new ReportLine(m.getId(), m.getTitle(), m.getType(), acc));
        }

        lines.sort((a, b) -> Integer.compare(b.accesses, a.accesses));
        return lines;
    }
}
