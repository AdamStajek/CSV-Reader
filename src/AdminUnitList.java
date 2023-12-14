import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;


public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();

    /**
     * Czyta rekordy pliku i dodaje do listy
     *
     * @param filename nazwa pliku
     */

    public void read(String filename) throws IOException {
        CSVReader reader = new CSVReader(filename, ",", true);
        Map<Long, AdminUnit> ids = new HashMap<>();
        Map<AdminUnit, Long> parents = new HashMap<>();
        Map<Long, List<AdminUnit>> parentid2child = new HashMap<>();
        while (reader.next()) {
            long id = reader.getInt("id");
            long parent = reader.isMissing("parent") ? 0 : reader.getInt("parent");
            String name = reader.get("name");
            int admin_level = reader.isMissing("admin_level") ? 0 : reader.getInt("admin_level");
            double population = reader.isMissing("population") ? 0 : reader.getDouble("population");
            double area = reader.isMissing("area") ? 0 : reader.getDouble("area");
            double density = reader.isMissing("density") ? 0 : reader.getDouble("density");
            double x1 = reader.isMissing("x1") ? 0 : reader.getDouble("x1");
            double x2 = reader.isMissing("x2") ? 0 : reader.getDouble("x2");
            double x3 = reader.isMissing("x3") ? 0 : reader.getDouble("x3");
            double x4 = reader.isMissing("x4") ? 0 : reader.getDouble("x4");
            double y1 = reader.isMissing("y1") ? 0 : reader.getDouble("y1");
            double y2 = reader.isMissing("y2") ? 0 : reader.getDouble("y2");
            double y3 = reader.isMissing("y3") ? 0 : reader.getDouble("y3");
            double y4 = reader.isMissing("y4") ? 0 : reader.getDouble("y4");
            if (x1 * x2 * x3 * x4 * y1 * y2 * y3 * y4 == 0) {
                continue;
            } // Jak wyszukiwać sąsiadów dla miejsc bez współrzędnych?
            AdminUnit unit = new AdminUnit(name, admin_level, population, area, density);
            unit.bbox.addPoint(x1, y1);
            unit.bbox.addPoint(x2, y2);
            unit.bbox.addPoint(x3, y3);
            unit.bbox.addPoint(x4, y4);
            units.add(unit);
            ids.put(id, unit);
            parents.put(unit, parent);
            if (parentid2child.get(parent) != null) {
                parentid2child.get(parent).add(unit);
            } else {
                List<AdminUnit> children = new ArrayList<>();
                children.add(unit);
                parentid2child.put(parent, children);
            }
        }
        for (long id : ids.keySet()) {
            AdminUnit unit = ids.get(id);
            long parentId = parents.get(unit);
            unit.parent = ids.get(parentId);
            unit.children = parentid2child.get(id);
        }
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     *
     * @param out
     */
    void list(PrintStream out) {
        for (AdminUnit unit : units) {
            System.out.println(unit.toString());
        }
    }

    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     *
     * @param out    - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit  - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out, int offset, int limit) {
        for (int i = offset; i < offset + limit; i++) {
            System.out.println(units.get(i));
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     *
     * @param pattern - wzorzec dla nazwy
     * @param regex   - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();
        if (regex) {
            for (AdminUnit unit : this.units) {
                if (unit.toString().matches(pattern)) {
                    ret.units.add(unit);
                }
            }
        } else {
            for (AdminUnit unit : this.units) {
                if (unit.toString().contains(pattern)) {
                    ret.units.add(unit);
                }
            }
        }
        return ret;
    }

    private void fixMissingValues(AdminUnit unit) {
        if (unit.density == 0) {
            if (unit.parent.density == 0) {
                fixMissingValues(unit.parent);
            }
            unit.density = unit.parent.density;
        }
        if (unit.population == 0) {
            unit.population = unit.area * unit.density;
        }
    }

    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     *
     * @param unit        - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbours(AdminUnit unit, double maxdistance) throws IOException {
        AdminUnitList unitList = new AdminUnitList();
        unitList.read("admin-units.csv");
        AdminUnitList neighbours = new AdminUnitList();
        if (unit.adminLevel == 8) {
            for (AdminUnit pot_neighbour : unitList.units) {
                if (pot_neighbour.adminLevel == unit.adminLevel) {
                    if (unit.bbox.distanceTo(pot_neighbour.bbox) <= maxdistance) {
                        neighbours.units.add(pot_neighbour);
                    }
                }
            }}
        else {
            for (AdminUnit pot_neighbour : unitList.units) {
                if (pot_neighbour.adminLevel == unit.adminLevel) {
                    if (unit.bbox.intersects(pot_neighbour.bbox)) {
                        if (!Objects.equals(pot_neighbour.name, unit.name)) {
                            neighbours.units.add(pot_neighbour);
                        }
                    }
                }
            }}

        return neighbours;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInPlaceByName(){
        class cmp implements Comparator<AdminUnit>{
            public int compare(AdminUnit a, AdminUnit b){
                return a.name.compareTo(b.name);
            }
        }

        this.units.sort(new cmp());
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByArea() {
        Comparator<AdminUnit> byArea = new Comparator<>() {
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area, o2.area);
            }
        };
        this.units.sort(byArea);
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByPopulation(){
        this.units.sort((x,y)->Double.compare(x.population, y.population));
        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> cmp){
        this.units.sort(cmp);
        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> cmp){
        AdminUnitList newlist = new AdminUnitList();
        newlist.units = new ArrayList<>(this.units);
        newlist.sortInplace(cmp);
        return newlist;
    }

    /**
     *
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred){
        AdminUnitList newlist = new AdminUnitList();
        newlist.units = new ArrayList<>(this.units);
        newlist.units.removeIf(pred.negate());
        return newlist;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
        AdminUnitList newlist = new AdminUnitList();
        newlist.units = new ArrayList<>(this.units);
        newlist.units.removeIf(pred.negate());
        if(limit < newlist.units.size()){
            newlist.units = newlist.units.subList(0, limit);}
        return newlist;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
        AdminUnitList newlist = new AdminUnitList();
        newlist.units = new ArrayList<>(this.units);
        newlist.units.removeIf(pred.negate());
        if(offset + limit < newlist.units.size()){
            newlist.units = newlist.units.subList(offset, offset + limit);}
        else if (offset + limit > newlist.units.size()) {
            newlist.units = newlist.units.subList(offset, newlist.units.size());
        }
        else if (offset > newlist.units.size()) {
            newlist.units = new ArrayList<>();
        }
        return newlist;
    }






}