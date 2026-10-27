package filter;

import library.Library;
import model.Media;

public class ByAuthorFilter implements MediaFilter {
    private final String author;

    public ByAuthorFilter(String author) {
        this.author = author == null ? "" : author.trim().toLowerCase();
    }

    @Override
    public boolean matches(Media media, Library library) {
        return media.getAuthor() != null && media.getAuthor().toLowerCase().contains(author);
    }
}
