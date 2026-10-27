package filter;

import library.Library;
import model.Media;

public class BySubjectFilter implements MediaFilter {
    private final String subjectCode;

    public BySubjectFilter(String subjectCode) {
        this.subjectCode = subjectCode == null ? "" : subjectCode.trim();
    }

    @Override
    public boolean matches(Media media, Library library) {
        return media.getSubjectCodes().contains(subjectCode);
    }
}
