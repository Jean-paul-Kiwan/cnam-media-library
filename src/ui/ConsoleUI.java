package ui;

import export.CsvExporter;
import export.ExportService;
import export.XmlExporter;
import factory.DefaultMediaFactory;
import factory.MediaFactory;
import filter.ByAuthorFilter;
import filter.BySpecialityFilter;
import filter.BySubjectFilter;
import filter.MediaFilter;
import library.Library;
import library.LibraryService;
import model.Media;
import persistence.LibraryPersistence;

import java.util.*;

public class ConsoleUI {
    private final Library lib;
    private final LibraryService service;
    private final MediaFactory factory;
    private final LibraryPersistence persistence;
    private final ExportService exportService;

    private final Scanner sc = new Scanner(System.in);

    public ConsoleUI(Library lib, LibraryService service) {
        this.lib = lib;
        this.service = service;
        this.factory = DefaultMediaFactory.build();
        this.persistence = new LibraryPersistence();

        this.exportService = new ExportService();
        this.exportService.register(new CsvExporter());
        this.exportService.register(new XmlExporter());
    }

    public void start() {
        while (true) {
            System.out.println("\n===== ISSAE Multimedia Library =====");
            System.out.println("1) Student login");
            System.out.println("2) Admin menu");
            System.out.println("0) Exit");
            int choice = readInt("Choice: ");

            if (choice == 0) return;
            if (choice == 1) studentFlow();
            else if (choice == 2) adminFlow();
            else System.out.println("Invalid choice.");
        }
    }

    // ---------------- STUDENT ----------------
    private void studentFlow() {
        System.out.println("\n--- Student Login ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        var student = lib.getStudentsByUsername().get(username);
        if (student == null || !student.getPassword().equals(password)) {
            System.out.println("Login failed.");
            return;
        }

        System.out.println("Welcome " + username + " (Speciality: " + student.getSpecialityName() + ")");

        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1) Search by ID");
            System.out.println("2) Search by Title");
            System.out.println("3) Filter");
            System.out.println("4) Open media (access)");
            System.out.println("0) Logout");
            int c = readInt("Choice: ");

            if (c == 0) return;

            switch (c) {
                case 1 -> {
                    int id = readInt("Media id: ");
                    Media m = service.findById(id);
                    printOne(m);
                }
                case 2 -> {
                    String q = readLine("Title contains: ");
                    List<Media> res = service.findByTitle(q);
                    printList(res);
                }
                case 3 -> {
                        try {
                            exportService.export(lib, "csv", "data/export.csv");
                            exportService.export(lib, "xml", "data/export.xml");
                            System.out.println("Exported to data/export.csv and data/export.xml");
                        } catch (Exception e) {
                            System.out.println("Export failed: " + e.getMessage());
                        }
                    }
                case 4 -> {
                        try {
                            persistence.save(lib, "data/library.bin");
                            System.out.println("Saved to data/library.bin");
                        } catch (Exception e) {
                            System.out.println("Save failed: " + e.getMessage());
                        }
                    }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private MediaFilter buildFilterMenu() {
        System.out.println("\n--- Filter Menu ---");
        System.out.println("1) By author");
        System.out.println("2) By subject code");
        System.out.println("3) By speciality name");
        System.out.println("0) Cancel");
        int c = readInt("Choice: ");
        if (c == 0) return null;

        return switch (c) {
            case 1 -> new ByAuthorFilter(readLine("Author contains: "));
            case 2 -> new BySubjectFilter(readLine("Subject code (e.g., NFP121): ").trim());
            case 3 -> new BySpecialityFilter(readLine("Speciality name (e.g., Informatique): ").trim());
            default -> {
                System.out.println("Invalid choice.");
                yield null;
            }
        };
    }

    // ---------------- ADMIN ----------------
    private void adminFlow() {
        // Simple admin password (optional). You can remove this if you want.
        String adminPass = readLine("Admin password (hint: admin): ");
        if (!"admin".equals(adminPass)) {
            System.out.println("Access denied.");
            return;
        }

        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1) Add media");
            System.out.println("2) Remove media");
            System.out.println("3) Export CSV + XML");
            System.out.println("4) Save (binary)");
            System.out.println("0) Back");
            int c = readInt("Choice: ");

            if (c == 0) return;

            switch (c) {
                case 1 -> addMediaFlow();
                case 2 -> {
                    int id = readInt("Media id to remove: ");
                    service.removeMedia(id);
                    System.out.println("Removed.");
                }
                case 3 -> {
                    try {
                        exportService.export(lib, "csv", "data/export.csv");
                        exportService.export(lib, "xml", "data/export.xml");
                        System.out.println("Exported to data/export.csv and data/export.xml");
                    } catch (Exception e) {
                        System.out.println("Export failed: " + e.getMessage());
                    }
                }
                case 4 -> {
                    try {
                        persistence.save(lib, "data/library.bin");
                        System.out.println("Saved to data/library.bin");
                    } catch (Exception e) {
                        System.out.println("Save failed: " + e.getMessage());
                    }
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addMediaFlow() {
        System.out.println("\n--- Add Media ---");
        System.out.println("Type: document | video | quiz");
        String type = readLine("Type: ").trim().toLowerCase();

        int id = readInt("id (int): ");
        String title = readLine("title: ");
        String author = readLine("author: ");
        int year = readInt("year (int): ");
        String description = readLine("description: ");

        String subjectsRaw = readLine("subjects codes separated by comma (e.g., NFP121,NFA035): ");
        List<String> subjects = parseCsvList(subjectsRaw);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", title);
        data.put("author", author);
        data.put("year", year);
        data.put("description", description);
        data.put("subjects", subjects);

        switch (type) {
            case "document" -> {
                int pages = readInt("pages (int): ");
                data.put("pages", pages);
            }
            case "video" -> {
                int duration = readInt("duration minutes (int): ");
                data.put("duration", duration);
            }
            case "quiz" -> {
                int est = readInt("estimated minutes (int): ");
                String diff = readLine("difficulty (EASY/MEDIUM/HARD): ");
                data.put("estimatedDuration", est);
                data.put("difficulty", diff);
            }
            default -> {
                System.out.println("Unknown type.");
                return;
            }
        }

        Media m = factory.create(type, data);
        service.addMedia(m);
        System.out.println("Added media: [" + m.getId() + "] " + m.getTitle() + " (" + m.getType() + ")");
    }

    // ---------------- Helpers ----------------
    private void printOne(Media m) {
        if (m == null) {
            System.out.println("Not found.");
            return;
        }
        System.out.println("[" + m.getId() + "] " + m.getTitle() + " | " + m.getType() + " | author=" + m.getAuthor());
        System.out.println("subjects=" + m.getSubjectCodes());
    }

    private void printList(List<Media> list) {
        if (list.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        for (Media m : list) {
            System.out.println("[" + m.getId() + "] " + m.getTitle() + " | " + m.getType() + " | author=" + m.getAuthor());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private List<String> parseCsvList(String raw) {
        if (raw == null || raw.trim().isEmpty()) return List.of();
        String[] parts = raw.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}
