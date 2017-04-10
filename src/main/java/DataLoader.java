import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import model.Coordinate;
import model.HospitalProfessional;
import model.HospitalService;
import model.Node;
import service.*;

import javax.persistence.EntityManagerFactory;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLoader {
    public static void main(String[] args) {
        EntityManagerFactory emf = EMFProvider.getInstance().getEMFactory();
        try {
            loadLocations(emf, "data/belkinHouse/locations.tsv");
            loadLocations(emf, "data/floor1/locations.tsv");
            loadLocations(emf, "data/floor2/locations.tsv");
            loadLocations(emf, "data/floor3/locations.tsv");
            loadLocations(emf, "data/floor4/locations.tsv");
            loadLocations(emf, "data/floor5/locations.tsv");
            loadLocations(emf, "data/floor6/locations.tsv");
            loadLocations(emf, "data/floor7/locations.tsv");

            loadPeople(emf, "data/belkinHouse/people.tsv");
            loadPeople(emf, "data/floor2/people.tsv");
            loadPeople(emf, "data/floor3/people.tsv");
            loadPeople(emf, "data/floor4/people.tsv");
            loadPeople(emf, "data/floor5/people.tsv");

            loadService(emf, "data/belkinHouse/services.tsv");
            loadService(emf, "data/floor1/services.tsv");
            loadService(emf, "data/floor2/services.tsv");
            loadService(emf, "data/floor3/services.tsv");
            loadService(emf, "data/floor4/services.tsv");
            loadService(emf, "data/floor5/services.tsv");
            loadService(emf, "data/floor6/services.tsv");
            loadService(emf, "data/floor7/services.tsv");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            emf.close();
        }
    }

    private static void loadLocations(EntityManagerFactory emf, String locationsFilePath) throws FileNotFoundException {
        NodeService nodeService = new NodeService();
        CoordinateService coordinateService = new CoordinateService();


        // Create all the nodes
        TsvParserSettings parserSettings = new TsvParserSettings();
        TsvParser parser = new TsvParser(parserSettings);

        List<String[]> allRows = parser.parseAll(DataLoader.class.getClassLoader().getResourceAsStream(locationsFilePath));
        for (String[] row : allRows.subList(1, allRows.size())) {
            if (Arrays.asList(row).contains(null)) continue;  // Test for blank line or value

            String[] split = row[0].split("\\s+\\-\\s+");
            if(split.length < 3){
                System.out.print("Could not add ");
                for(int i = 0; i < split.length; i++){
                    System.out.print(split[i]);
                }
                System.out.println("");
            } else {

                // Parse the Coordinate
                double x = Double.parseDouble(split[1]);
                double y = Double.parseDouble(split[2]);
                Coordinate location = new Coordinate(x, y, 4);

                coordinateService.persist(location);

                String name = split[0];

                nodeService.persist(new Node(location, name));
            }
        }
    }

    private static void loadPeople(EntityManagerFactory emf, String peopleFilePath) throws FileNotFoundException {
        HospitalProfessionalService professionalService = new HospitalProfessionalService();
        NodeService nodeService = new NodeService();

        TsvParserSettings parserSettings = new TsvParserSettings();
        TsvParser parser = new TsvParser(parserSettings);

        List<String[]> allRows = parser.parseAll(DataLoader.class.getClassLoader().getResourceAsStream(peopleFilePath));
        for (String[] row : allRows.subList(1, allRows.size())) {
            if (Arrays.asList(row).contains(null)) continue;

            String[] split = row[0].split("\\s+\\-\\s+");
            if(split.length < 3){
                System.out.print("Could not add ");
                for(int i = 0; i < split.length; i++){
                    System.out.print(split[i]);
                }
                System.out.println("");
            } else {

                String name =split[0];
                String title = split[1];

                List<Node> nodes = new ArrayList<>();//= nodeService.findNodeByName(split[2]);
                HospitalProfessional hp = new HospitalProfessional(name, title, nodes);

                professionalService.persist(new HospitalProfessional(name, title, nodes));

            }
        }
    }

    private static void loadService(EntityManagerFactory emf, String serviceFilePath) throws FileNotFoundException {
        HospitalServiceService serviceService = new HospitalServiceService();
        NodeService nodeService = new NodeService();

        TsvParserSettings parserSettings = new TsvParserSettings();
        TsvParser parser = new TsvParser(parserSettings);

        List<String[]> allRows = parser.parseAll(DataLoader.class.getClassLoader().getResourceAsStream(serviceFilePath));
        for (String[] row : allRows.subList(1, allRows.size())) {
            if (Arrays.asList(row).contains(null)) continue;

            String[] split = row[0].split("\\s+\\-\\s+");
            if(split.length < 2){
                System.out.print("Could not add ");
                for(int i = 0; i < split.length; i++){
                    System.out.print(split[i]);
                }
                System.out.println("");
            } else {

                String name =split[0];


                List<Node> nodes = new ArrayList<>();
               //= nodeService.findNodeByName(split[2]);
                HospitalService hs = new HospitalService(nodes, name);

                serviceService.persist(new HospitalService(nodes, name));


            }
        }
    }
}