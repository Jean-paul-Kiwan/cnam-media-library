package filter;

import library.Library;
import model.Media;
import model.Speciality;

public class BySpecialityFilter implements MediaFilter {
    private final String specialityName;

    public BySpecialityFilter(String specialityName) {
        this.specialityName = specialityName == null ? "" : specialityName.trim();
    }

    @Override
    public boolean matches(Media media, Library library) {
        Speciality sp = library.getSpecialitiesByName().get(specialityName);
        if (sp == null) return false;

        // if media has at least one subject that exists in this speciality
        for (String code : media.getSubjectCodes()) {
            if (sp.getSubjectsByCode().containsKey(code)) return true;
        }
        return false;
    }
}
