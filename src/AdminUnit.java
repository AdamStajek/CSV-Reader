import java.util.ArrayList;
import java.util.List;

public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent;
    BoundingBox bbox = new BoundingBox();
    List<AdminUnit> children;

    public String toString(){
        return String.format("%s %d %f %f %f %s %s", name, adminLevel, population, area, density, parent != null ? parent.name : "", bbox.toString());
    }

    //funkcja pomocnicza
    public void listChildren(){
        if(children != null){
        for(AdminUnit child: children){
            System.out.printf("%s ", child.name);
        }
        System.out.println();
    }}

    AdminUnit(String name, int adminLevel, double population, double area, double density){
        this.name = name;
        this.adminLevel = adminLevel;
        this.population = population;
        this.area = area;
        this.density = density;
    }

}
