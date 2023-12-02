import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();

    /**
     * Czyta rekordy pliku i dodaje do listy
     * @param filename nazwa pliku
     */

    public void read(String filename) throws IOException {
        CSVReader reader = new CSVReader(filename, ",", true);
        Map<Long, AdminUnit> ids = new HashMap<>();
        Map<AdminUnit, Long> parents = new HashMap<>();
        Map<Long,List<AdminUnit>> parentid2child = new HashMap<>();
        while (reader.next()) {
            long id = reader.getInt("id");
            long parent = reader.isMissing("parent") ? 0: reader.getInt("parent");
            String name = reader.get("name");
            int admin_level = reader.isMissing("admin_level") ? 0 : reader.getInt("admin_level");
            double population = reader.isMissing("population") ? 0: reader.getDouble("population");
            double area = reader.isMissing("area") ? 0: reader.getDouble("area");
            double density = reader.isMissing("density") ? 0: reader.getDouble("density");
            AdminUnit unit = new AdminUnit(name, admin_level, population, area, density);
            units.add(unit);
            ids.put(id, unit);
            parents.put(unit, parent);
            if(parentid2child.get(parent) != null) {
                parentid2child.get(parent).add(unit);
            }
            else{
                List<AdminUnit> children = new ArrayList<>();
                children.add(unit);
                parentid2child.put(parent, children);
            }

        }
        for(long id: ids.keySet()){
            AdminUnit unit = ids.get(id);
            long parentId = parents.get(unit);
            unit.parent = ids.get(parentId);
            unit.children = parentid2child.get(id);
        }
    }
    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     * @param out
     */
    void list(PrintStream out){
        for(AdminUnit unit: units){
            System.out.println(unit.toString());
        }
    }
    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     * @param out - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out, int offset, int limit ){
        for(int i=offset; i < offset+limit; i++){
            System.out.println(units.get(i));
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     * @param pattern - wzorzec dla nazwy
     * @param regex - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex){
        AdminUnitList ret = new AdminUnitList();
        if(regex){
            for(AdminUnit unit: this.units){
                if(unit.toString().matches(pattern)){
                    ret.units.add(unit);
                }
            }
        }
        else{
            for(AdminUnit unit: this.units){
                if(unit.toString().contains(pattern)){
                    ret.units.add(unit);
                }
            }
        }
        return ret;
    }

    private void fixMissingValues(AdminUnit unit){
        if(unit.density == 0){
            if(unit.parent.density == 0){
                fixMissingValues(unit.parent);
            }
            unit.density = unit.parent.density;
            }
        if(unit.population == 0){
            unit.population = unit.area * unit.density;
        }
        }
    }
