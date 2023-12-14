import java.io.*;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;

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
        //System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
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

        //test sortowania
        AdminUnitList unitList1 = new AdminUnitList();
        unitList1.read("admin-units.csv");
        unitList1.sortInplaceByArea();
        unitList1.units.removeIf(a->a.parent==null);

        //unitList1.list(out);


        //elementy na k
        //unitList1.filter(a->a.name.startsWith("K")).sort(Comparator.comparingDouble(a -> a.area)).list(out);
        //powiaty w Małopolsce
        //unitList1.filter(a->a.parent.name.equals("województwo małopolskie")).sort(Comparator.comparingDouble(a -> a.area)).list(out);
        //inne
        //unitList1.filter(a->a.population > 10000).sort(Comparator.comparingDouble(a -> a.population)).list(out);
        //unitList1.filter(a->a.area > 25).sort(Comparator.comparingDouble(a -> a.area)).list(out);
        //unitList1.filter(a->a.parent.name.equals("powiat ostrowiecki")).sort(Comparator.comparingDouble(a -> a.area)).list(out);

        Predicate<AdminUnit> p = (a->a.population > 10000);
        p = p.and(a->a.parent.name.equals("województwo śląskie"));
        Predicate<AdminUnit> r = (a->a.area > 25 && a.adminLevel==7);
        p = p.or(r);
        //unitList1.filter(p, 100, 150).list(out);

        //zapytania
        unitList.units.removeIf(a->a.parent==null);

        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(unitList)
                .where(a->a.area>1000)
                .or(a->a.name.startsWith("Sz"))
                .sort((a,b)->Double.compare(a.area,b.area))
                .limit(100);
        query.execute().list(out);

        AdminUnitQuery query1 = new AdminUnitQuery()
                .selectFrom(unitList)
                .where(a->a.adminLevel==6)
                .or(a->a.name.startsWith("Ostr"))
                .sort((a,b)->Double.compare(a.population,b.population))
                .offset(50);
        query1.execute().list(out);

        AdminUnitQuery query2 = new AdminUnitQuery()
                .selectFrom(unitList)
                .where(a->a.population > 30000)
                .or(a->a.parent.name.startsWith("powiat"))
                .sort((a,b)->Double.compare(a.population,b.population))
                .limit(10);
        query2.execute().list(out);

        AdminUnitQuery query3 = new AdminUnitQuery()
                .selectFrom(unitList)
                .where(a->a.population > 30000)
                .or(a->a.area > 30)
                .sort((a,b)->Double.compare(a.adminLevel,b.adminLevel))
                .limit(100000);
        query3.execute().list(out);













    }}

