package stats;

import library.Library;
import model.Media;
import model.Speciality;
import model.Subject;

import java.util.*;

public class MostViewedBySpecialityReport implements ReportStrategy {

    @Override
    public List<ReportLine> generate(Library library, String specialityName) {
        Speciality sp = library.getSpecialitiesByName().get(specialityName);
        if (sp == null) throw new IllegalArgumentException("Unknown speciality: " + specialityName);

        Map<Integer, Integer> counts = library.getAccessCountByMediaId();
        Set<Integer> mediaIds = new HashSet<>();

        for (Subject subj : sp.getSubjectsByCode().values()) {
            mediaIds.addAll(subj.getMediaIds());
        }

        List<ReportLine> lines = new ArrayList<>();
        for (Integer id : mediaIds) {
            Media m = library.getMediaById().get(id);
            if (m == null) continue;
            int acc = counts.getOrDefault(id, 0);
            lines.add(new ReportLine(m.getId(), m.getTitle(), m.getType(), acc));
        }

        lines.sort((a, b) -> Integer.compare(b.accesses, a.accesses));
        return lines;
    }
}
