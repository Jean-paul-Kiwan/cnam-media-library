package stats;

import library.Library;
import java.util.List;

public interface ReportStrategy {
    List<ReportLine> generate(Library library, String key); 
    // key = speciality name OR subject code depending on report
}
