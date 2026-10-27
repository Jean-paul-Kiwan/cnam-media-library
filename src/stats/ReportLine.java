package stats;

public class ReportLine {
    public final int id;
    public final String title;
    public final String type;
    public final int accesses;

    public ReportLine(int id, String title, String type, int accesses) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.accesses = accesses;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " | " + type + " | accesses=" + accesses;
    }
}
