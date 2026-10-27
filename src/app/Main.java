package app;

import factory.DefaultMediaFactory;
import factory.MediaFactory;
import library.Library;
import library.LibraryService;
import model.Media;

import filter.ByAuthorFilter;
import filter.BySpecialityFilter;
import filter.BySubjectFilter;

import observer.EmailNotifier;

import persistence.UniversityXmlLoader;
import ui.SwingApp;
import persistence.LibraryPersistence;

//import export.CsvExporter;
//import export.XmlExporter;
//import export.ExportService;

import ui.ConsoleUI;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // ===== Load (binary first, else XML) =====
            LibraryPersistence persistence = new LibraryPersistence();
            Library lib = persistence.load("data/library.bin");

            if (lib == null) {
                lib = new Library();
                new UniversityXmlLoader().loadInto(lib, "data/universite.xml");
                System.out.println("Loaded from XML.");
            } else {
                System.out.println("Loaded from binary file.");
            }

            // ===== Print loaded base data =====
            System.out.println("Loaded students: " + lib.getStudentsByUsername().size());
            System.out.println("Loaded specialities: " + lib.getSpecialitiesByName().size());

            lib.getSpecialitiesByName().forEach((name, spec) ->
                    System.out.println("Speciality: " + name + " | subjects: " + spec.getSubjectsByCode().keySet())
            );

            // ===== Services + observers =====
            LibraryService service = new LibraryService(lib);
            service.addObserver(new EmailNotifier());

            // ===== Create media using factory =====
            MediaFactory mf = DefaultMediaFactory.build();

            Media m1 = mf.create("document", Map.of(
                    "id", 1,
                    "title", "Concept de Généricité",
                    "author", "Dr X",
                    "year", 2024,
                    "description", "This media explains Java generics...",
                    "subjects", List.of("NFP121", "NFA035"),
                    "pages", 12
            ));
            Media m2 = mf.create("video", Map.of(
    "id", 2,
    "title", "CPU Scheduling",
    "author", "Dr Y",
    "year", 2025,
    "description", "Video lesson about CPU scheduling algorithms and performance metrics.",
    "subjects", List.of("NSY103"),
    "duration", 45
));

Media m3 = mf.create("quiz", Map.of(
    "id", 3,
    "title", "PERT and Gantt Quiz",
    "author", "Dr Z",
    "year", 2025,
    "description", "Quiz about project planning using PERT and Gantt charts.",
    "subjects", List.of("GDN100"),
    "estimatedDuration", 10,
    "difficulty", "Medium"
));


            // Add only if not already present (important when loading from binary)
            if (!lib.getMediaById().containsKey(1)) {
                service.addMedia(m1);
            }
            if (!lib.getMediaById().containsKey(2)) {
                service.addMedia(m2);
            }
            if (!lib.getMediaById().containsKey(3)) {
                service.addMedia(m3);
            }

            // ===== Simulate accesses =====
            service.openMedia("student2@example.invalid", 1);
            service.openMedia("student2@example.invalid", 1);
            service.openMedia("student3@example.invalid", 1);
            service.openMedia("student1@example.invalid", 1);
            service.openMedia("student1@example.invalid", 2);

            // ===== Reports =====
            stats.ReportStrategy r1 = new stats.MostViewedBySubjectReport();
            System.out.println("REPORT Subject NFP121:");
            for (stats.ReportLine line : r1.generate(lib, "NFP121")) {
                System.out.println(line);
            }

            stats.ReportStrategy r2 = new stats.MostViewedBySpecialityReport();
            System.out.println("\nREPORT Speciality Informatique:");
            for (stats.ReportLine line : r2.generate(lib, "Informatique")) {
                System.out.println(line);
            }

            // ===== Search + filters =====
            System.out.println("Media count = " + lib.getMediaById().size());
            System.out.println("Search by title 'Gen' = " + service.findByTitle("Gen").size());
            System.out.println("Filter author 'dr' = " + service.filter(new ByAuthorFilter("dr")).size());
            System.out.println("Filter subject NFP121 = " + service.filter(new BySubjectFilter("NFP121")).size());
            System.out.println("Filter speciality Informatique = " + service.filter(new BySpecialityFilter("Informatique")).size());

           //ConsoleUI ui = new ConsoleUI(lib, service);
           //ui.start();
           //// ===== Save at end =====
           //persistence.save(lib, "data/library.bin");
           //System.out.println("Library saved.");

           //ExportService exportService = new ExportService();
           //exportService.register(new CsvExporter());
           //exportService.register(new XmlExporter());

           //exportService.export(lib, "csv", "data/export.csv");
           //exportService.export(lib, "xml", "data/export.xml");
           //System.out.println("Exported CSV + XML to data/ folder.");

           //new SwingApp(lib, service).start();

           // ===== Choose UI =====
           // true  -> Swing popup UI
           // false -> Console (terminal) UI
           boolean useSwingUI = true;
                   
           if (useSwingUI) {
               new SwingApp(lib, service).start();   // Popup UI (Swing)
           } else {
               ConsoleUI ui = new ConsoleUI(lib, service); // Terminal UI
               ui.start();
           }




        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
