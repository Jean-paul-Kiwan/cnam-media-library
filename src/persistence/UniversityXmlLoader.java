package persistence;

import library.Library;
import model.Speciality;
import model.Student;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

public class UniversityXmlLoader {

    public void loadInto(Library library, String xmlPath) throws Exception {
        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            throw new IllegalArgumentException("Missing file: " + xmlPath);
        }
        if (xmlFile.length() == 0) {
            throw new IllegalArgumentException("Empty file: " + xmlPath);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        Element root = doc.getDocumentElement();
        if (root == null || !"issae".equals(root.getTagName())) {
            throw new IllegalArgumentException("Invalid root tag. Expected <issae>.");
        }

        NodeList specialiteNodes = root.getElementsByTagName("specialite");
        for (int i = 0; i < specialiteNodes.getLength(); i++) {
            Node node = specialiteNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;

            Element specialiteEl = (Element) node;
            String specialityName = specialiteEl.getAttribute("nom");
            if (specialityName == null || specialityName.isBlank()) {
                throw new IllegalArgumentException("Specialite missing attribute 'nom'.");
            }

            Speciality speciality = library.getOrCreateSpeciality(specialityName);

            NodeList etudiantNodes = specialiteEl.getElementsByTagName("etudiant");
            for (int j = 0; j < etudiantNodes.getLength(); j++) {
                Node sNode = etudiantNodes.item(j);
                if (sNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element etudiantEl = (Element) sNode;
                String username = etudiantEl.getAttribute("username");
                String password = etudiantEl.getAttribute("password");

                if (username == null || username.isBlank()) {
                    throw new IllegalArgumentException("Etudiant missing username in specialite: " + specialityName);
                }
                if (password == null || password.isBlank()) {
                    throw new IllegalArgumentException("Etudiant missing password for: " + username);
                }

                // values (subjects)
                List<String> subjectCodes = new ArrayList<>();
                NodeList valeurNodes = etudiantEl.getElementsByTagName("valeur");
                for (int k = 0; k < valeurNodes.getLength(); k++) {
                    Node vNode = valeurNodes.item(k);
                    if (vNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    String code = vNode.getTextContent();
                    if (code != null) code = code.trim();
                    if (code != null && !code.isBlank()) {
                        subjectCodes.add(code);
                        // Ensure Subject exists in that speciality
                        speciality.getOrCreateSubject(code);
                    }
                }

                Student student = new Student(username, password, specialityName, subjectCodes);
                library.getStudentsByUsername().put(username, student);
            }
        }
    }
}
