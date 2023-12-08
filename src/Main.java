import java.io.*;
import java.util.Locale;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws IOException {
        // 1. i 2. test
        CSVReader reader = new CSVReader("with-header.csv",";",true);
        while(reader.next()){
            String id = reader.get("id");
            String imie = reader.get("imie");
            String nazwisko = reader.get("nazwisko");
            String ulica = reader.get("ulica");
            int nrdomu = reader.getInt("nrdomu");
            int nrmieszkania = reader.getInt("nrmieszkania");

            /*System.out.printf(Locale.US,"%s %s %s %s %d %d \n", id, imie, nazwisko,
                    ulica, nrdomu, nrmieszkania);*/
        }

        // 3. test
        reader = new CSVReader("missing-values.csv",";",true);
        while(reader.next()){
            int id = reader.getInt("id");
            int parent = (reader.isMissing("parent")) ? 0 : reader.getInt("parent");
            String name = reader.get("name");
            int admin_level = reader.getInt("admin_level");
            int population = (reader.isMissing("population")) ? 0: reader.getInt("population");
            double area = reader.getDouble("area");
            double density = (reader.isMissing("density")) ? 0: reader.getDouble("density");

            /*System.out.printf(Locale.US, "%d %d %s %d %d %f %f \n", id, parent, name,
                    admin_level, population, area, density);*/
        }

        // 4. test
        // int id = reader.getInt("id12");
        // String id = reader.get(123);

        // 5. test
        String text = "a,b,c\n123.4,567.8,91011.12";
        reader = new CSVReader(new StringReader(text),",",true);
        while(reader.next()){
            String a = reader.get(0);
            String b = reader.get(1);
            String c = reader.get(2);

            //System.out.printf("%s %s %s", a, b, c);
        }

        // teksty w cudzysłowach (na przykładzie odczytu części Titanica)
        reader = new CSVReader("titanic-part.csv",",(?=([^\"]|\"[^\"]*\")*$)",true);
        while(reader.next()){
            int id = reader.getInt("PassengerId");
            int pclass = reader.getInt("Pclass");
            String name = reader.get("Name");

            //System.out.printf("%d %d %s \n", id, pclass, name);
            }


        // wypisywanie wybranych jednostek
        AdminUnitList unitList = new AdminUnitList();
        unitList.read("admin-units.csv");
        AdminUnitList selected = unitList.selectByName(".*ma.*", true);
        for(AdminUnit unit: unitList.units){
            //System.out.println(unit.name);
            //unit.listChildren();
        }

        //szukanie sąsiadów dla nie-miejscowości z pomiarem czasu
        AdminUnitList brzeszcze = unitList.selectByName("gmina Brzeszcz.*", true);
        AdminUnit unit = brzeszcze.units.get(0);
        double t1 = System.nanoTime()/1e6;
        AdminUnitList neighbours = brzeszcze.getNeighbours(unit, 100);
        double t2 = System.nanoTime()/1e6;
        //System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
        for(AdminUnit neighbour: neighbours.units){
            //System.out.println(neighbour.toString());
        }

        //szukanie sąsiadów dla miejscowości
        AdminUnitList wil = unitList.selectByName("Wilamowic.*", true);
        AdminUnit wil1 = wil.units.get(0);
        t1 = System.nanoTime()/1e6;
        AdminUnitList neighbours_wil = wil.getNeighbours(wil1, 10);
        t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
        for(AdminUnit neighbour: neighbours_wil.units){
            System.out.println(neighbour.toString());
        }

        //dystans warszawa kraków
        AdminUnitList krk = unitList.selectByName("Krakó.*", true);
        AdminUnit krakow = krk.units.get(3);
        AdminUnitList wwa = unitList.selectByName("Warszaw.*", true);
        AdminUnit warszawa = wwa.units.get(0);
        //System.out.println(krakow.toString());
        //System.out.println(warszawa.toString());
        //System.out.println(krakow.bbox.distanceTo(warszawa.bbox));




        }
}

