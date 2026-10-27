package filter;

import library.Library;
import model.Media;

@FunctionalInterface
public interface MediaFilter {
    boolean matches(Media media, Library library);
}
