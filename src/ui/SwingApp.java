package ui;

import export.CsvExporter;
import export.ExportService;
import export.XmlExporter;
import factory.DefaultMediaFactory;
import factory.MediaFactory;
import filter.ByAuthorFilter;
import filter.BySpecialityFilter;
import filter.BySubjectFilter;
import library.Library;
import library.LibraryService;
import model.Media;
import model.Student;
import persistence.LibraryPersistence;
import stats.MostViewedBySpecialityReport;
import stats.MostViewedBySubjectReport;
import stats.ReportLine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class SwingApp {

    private final Library lib;
    private final LibraryService service;

    private final LibraryPersistence persistence = new LibraryPersistence();
    private final ExportService exportService = new ExportService();
    private final MediaFactory factory = DefaultMediaFactory.build();

    // session
    private Student loggedStudent = null;

    public SwingApp(Library lib, LibraryService service) {
        this.lib = lib;
        this.service = service;

        exportService.register(new CsvExporter());
        exportService.register(new XmlExporter());
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("ISSAE Multimedia Library");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(900, 600);
            f.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Student", buildStudentPanel(f));
            tabs.addTab("Admin", buildAdminPanel(f));

            f.setContentPane(tabs);
            f.setVisible(true);
        });
    }

    // ---------------- Student UI ----------------

    private JPanel buildStudentPanel(JFrame frame) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JTextField username = new JTextField(22);
        JPasswordField password = new JPasswordField(12);

        JButton loginBtn = new JButton("Login");
        JLabel status = new JLabel("Not logged in.");

        top.add(new JLabel("Username:"));
        top.add(username);
        top.add(new JLabel("Password:"));
        top.add(password);
        top.add(loginBtn);
        top.add(status);

        JPanel center = new JPanel(new BorderLayout(10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> results = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(results);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(details);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, detailsScroll);
        split.setResizeWeight(0.4);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JTextField idField = new JTextField(6);
        JButton byIdBtn = new JButton("Find by ID");
        JTextField titleField = new JTextField(20);
        JButton byTitleBtn = new JButton("Find by Title");
        JButton openBtn = new JButton("Open (count access)");

        row1.add(new JLabel("ID:"));
        row1.add(idField);
        row1.add(byIdBtn);
        row1.add(new JLabel("Title contains:"));
        row1.add(titleField);
        row1.add(byTitleBtn);
        row1.add(openBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JComboBox<String> filterType = new JComboBox<>(new String[]{"Author", "Subject", "Speciality"});
        JTextField filterValue = new JTextField(18);
        JButton filterBtn = new JButton("Filter");
        JButton clearBtn = new JButton("Clear");

        row2.add(new JLabel("Filter:"));
        row2.add(filterType);
        row2.add(new JLabel("Value:"));
        row2.add(filterValue);
        row2.add(filterBtn);
        row2.add(clearBtn);

        bottom.add(row1);
        bottom.add(row2);

        center.add(split, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword());

            Student st = lib.getStudentsByUsername().get(u);
            if (st == null || !st.getPassword().equals(p)) {
                loggedStudent = null;
                status.setText("Login failed.");
                JOptionPane.showMessageDialog(frame, "Invalid username/password.", "Login", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loggedStudent = st;
            status.setText("Logged in: " + st.getUsername() + " (" + st.getSpecialityName() + ")");
            JOptionPane.showMessageDialog(frame, "Welcome " + st.getUsername(), "Login", JOptionPane.INFORMATION_MESSAGE);
        });

        byIdBtn.addActionListener(e -> {
            listModel.clear();
            details.setText("");
            try {
                ensureStudentLogged(frame);
                int id = Integer.parseInt(idField.getText().trim());
                Media m = service.findById(id);
                if (m == null) {
                    JOptionPane.showMessageDialog(frame, "Not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                listModel.addElement(formatLine(m));
                details.setText(formatDetails(m));
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        byTitleBtn.addActionListener(e -> {
            listModel.clear();
            details.setText("");
            try {
                ensureStudentLogged(frame);
                String q = titleField.getText().trim();
                List<Media> res = service.findByTitle(q);
                for (Media m : res) listModel.addElement(formatLine(m));
                if (res.isEmpty()) JOptionPane.showMessageDialog(frame, "No results.", "Search", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        filterBtn.addActionListener(e -> {
            listModel.clear();
            details.setText("");
            try {
                ensureStudentLogged(frame);
                String type = (String) filterType.getSelectedItem();
                String val = filterValue.getText().trim();

                List<Media> res;
                if ("Author".equals(type)) res = service.filter(new ByAuthorFilter(val));
                else if ("Subject".equals(type)) res = service.filter(new BySubjectFilter(val));
                else res = service.filter(new BySpecialityFilter(val));

                for (Media m : res) listModel.addElement(formatLine(m));
                if (res.isEmpty()) JOptionPane.showMessageDialog(frame, "No results.", "Filter", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        clearBtn.addActionListener(e -> {
            listModel.clear();
            details.setText("");
        });

        results.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = results.getSelectedValue();
            if (selected == null) return;
            Integer id = parseIdFromLine(selected);
            if (id == null) return;
            Media m = service.findById(id);
            if (m != null) details.setText(formatDetails(m));
        });

        openBtn.addActionListener(e -> {
            try {
                ensureStudentLogged(frame);
                String selected = results.getSelectedValue();
                Integer id = selected != null ? parseIdFromLine(selected) : null;
                if (id == null) {
                    String raw = idField.getText().trim();
                    if (!raw.isEmpty()) id = Integer.parseInt(raw);
                }
                if (id == null) {
                    JOptionPane.showMessageDialog(frame, "Select a media or enter an ID.", "Open", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                service.openMedia(loggedStudent.getUsername(), id);

                Media m = service.findById(id);
                if (m != null) details.setText(formatDetails(m));

                JOptionPane.showMessageDialog(frame, "Opened media (access counted).", "Open", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private void ensureStudentLogged(JFrame frame) {
        if (loggedStudent == null) {
            throw new IllegalStateException("Please login as a student first.");
        }
    }

    // ---------------- Admin UI ----------------

    private JPanel buildAdminPanel(JFrame frame) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPasswordField adminPass = new JPasswordField(12);
        JButton unlock = new JButton("Unlock");
        JLabel status = new JLabel("Locked.");

        top.add(new JLabel("Admin password:"));
        top.add(adminPass);
        top.add(unlock);
        top.add(status);

        JPanel body = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));

        JComboBox<String> type = new JComboBox<>(new String[]{"document", "video", "quiz"});
        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField year = new JTextField();
        JTextField description = new JTextField();
        JTextField subjects = new JTextField(); 
        JTextField extra = new JTextField();    
        JLabel extraLbl = new JLabel("pages (document)");

        type.addActionListener(e -> {
            String t = (String) type.getSelectedItem();
            if ("document".equals(t)) extraLbl.setText("pages (document)");
            else if ("video".equals(t)) extraLbl.setText("duration minutes (video)");
            else extraLbl.setText("estimated minutes (quiz)  (difficulty fixed=MEDIUM)");
        });

        form.add(new JLabel("Type"));
        form.add(type);

        form.add(new JLabel("ID"));
        form.add(id);

        form.add(new JLabel("Title"));
        form.add(title);

        form.add(new JLabel("Author"));
        form.add(author);

        form.add(new JLabel("Year"));
        form.add(year);

        form.add(new JLabel("Description"));
        form.add(description);

        form.add(new JLabel("Subjects (comma)"));
        form.add(subjects);

        form.add(extraLbl);
        form.add(extra);

        JButton addBtn = new JButton("Add Media");
        JButton removeBtn = new JButton("Remove by ID");
        JTextField removeId = new JTextField(8);

        JButton exportBtn = new JButton("Export CSV + XML");
        JButton saveBtn = new JButton("Save (binary)");

        JTextArea adminLog = new JTextArea();
        adminLog.setEditable(false);
        JScrollPane logScroll = new JScrollPane(adminLog);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.add(addBtn);
        actions.add(new JLabel("Remove ID:"));
        actions.add(removeId);
        actions.add(removeBtn);
        actions.add(exportBtn);
        actions.add(saveBtn);

        body.add(form, BorderLayout.NORTH);
        body.add(actions, BorderLayout.CENTER);
        body.add(logScroll, BorderLayout.SOUTH);

        setPanelEnabled(body, false);

        unlock.addActionListener(e -> {
            String p = new String(adminPass.getPassword());
            if (!"admin".equals(p)) {
                JOptionPane.showMessageDialog(frame, "Wrong admin password.", "Admin", JOptionPane.ERROR_MESSAGE);
                status.setText("Locked.");
                setPanelEnabled(body, false);
                return;
            }
            status.setText("Unlocked.");
            setPanelEnabled(body, true);
            JOptionPane.showMessageDialog(frame, "Admin unlocked.", "Admin", JOptionPane.INFORMATION_MESSAGE);
        });

        addBtn.addActionListener(e -> {
            try {
                String t = (String) type.getSelectedItem();
                int mid = Integer.parseInt(id.getText().trim());
                int y = Integer.parseInt(year.getText().trim());
                String ttl = title.getText().trim();
                String au = author.getText().trim();
                String desc = description.getText().trim();
                List<String> subj = parseSubjects(subjects.getText().trim());

                Map<String, Object> data = new java.util.HashMap<>();
                data.put("id", mid);
                data.put("title", ttl);
                data.put("author", au);
                data.put("year", y);
                data.put("description", desc);
                data.put("subjects", subj);

                if ("document".equals(t)) {
                    int pages = Integer.parseInt(extra.getText().trim());
                    data.put("pages", pages);
                } else if ("video".equals(t)) {
                    int duration = Integer.parseInt(extra.getText().trim());
                    data.put("duration", duration);
                } else {
                    int est = Integer.parseInt(extra.getText().trim());
                    data.put("estimatedDuration", est);
                    data.put("difficulty", "MEDIUM");
                }

                Media m = factory.create(t, data);
                service.addMedia(m); 
                adminLog.append("Added: " + formatLine(m) + "\n");

            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        removeBtn.addActionListener(e -> {
            try {
                int rid = Integer.parseInt(removeId.getText().trim());
                service.removeMedia(rid);
                adminLog.append("Removed media id=" + rid + "\n");
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        exportBtn.addActionListener(e -> {
            try {
                exportService.export(lib, "csv", "data/export.csv");
                exportService.export(lib, "xml", "data/export.xml");
                adminLog.append("Exported: data/export.csv + data/export.xml\n");
                JOptionPane.showMessageDialog(frame, "Export done.", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                persistence.save(lib, "data/library.bin");
                adminLog.append("Saved: data/library.bin\n");
                JOptionPane.showMessageDialog(frame, "Saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        JButton statsSubject = new JButton("Stats Subject NFP121");
        JButton statsSpec = new JButton("Stats Speciality Informatique");
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsRow.add(statsSubject);
        statsRow.add(statsSpec);
        body.add(statsRow, BorderLayout.WEST);

        statsSubject.addActionListener(e -> {
            try {
                var rep = new MostViewedBySubjectReport().generate(lib, "NFP121");
                adminLog.append("REPORT Subject NFP121:\n");
                for (ReportLine line : rep) adminLog.append("  " + line + "\n");
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        statsSpec.addActionListener(e -> {
            try {
                var rep = new MostViewedBySpecialityReport().generate(lib, "Informatique");
                adminLog.append("REPORT Speciality Informatique:\n");
                for (ReportLine line : rep) adminLog.append("  " + line + "\n");
            } catch (Exception ex) {
                showErr(frame, ex);
            }
        });

        root.add(top, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private void setPanelEnabled(JComponent c, boolean enabled) {
        c.setEnabled(enabled);
        for (Component child : c.getComponents()) {
            child.setEnabled(enabled);
            if (child instanceof JComponent jc) setPanelEnabled(jc, enabled);
        }
    }

    // ---------------- Helpers ----------------

    private List<String> parseSubjects(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        String[] parts = raw.split(",");
        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private String formatLine(Media m) {
        return "[" + m.getId() + "] " + m.getTitle() + " | " + m.getType() + " | author=" + m.getAuthor();
    }

    private String formatDetails(Media m) {
        int acc = lib.getAccessCountByMediaId().getOrDefault(m.getId(), 0);
        return "ID: " + m.getId() +
                "\nTitle: " + m.getTitle() +
                "\nType: " + m.getType() +
                "\nAuthor: " + m.getAuthor() +
                "\nYear: " + m.getYear() +
                "\nSubjects: " + m.getSubjectCodes() +
                "\nAccesses: " + acc +
                "\n\nDescription:\n" + m.getDescription();
    }

    private Integer parseIdFromLine(String line) {
        try {
            int a = line.indexOf('[');
            int b = line.indexOf(']');
            if (a >= 0 && b > a) return Integer.parseInt(line.substring(a + 1, b));
        } catch (Exception ignored) {}
        return null;
    }

    private void showErr(JFrame frame, Exception ex) {
        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
