import java.io.IOException;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException {
        CSVReader reader = new CSVReader("with-header.csv",";",true);
        while(reader.next()){
            int id = reader.getInt("id");
            String name = reader.get("imie");
            double nrmieszkania = reader.getDouble("nrmieszkania");

            System.out.printf(Locale.US,"%d %s %f \n",id, name, nrmieszkania);
        }
    }
}
