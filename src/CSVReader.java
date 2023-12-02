import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;
    List<String> columnLabels = new ArrayList<>();
    String[] current;

    // odwzorowanie: nazwa kolumny -> numer kolumny
    Map<String,Integer> columnLabelsToInt = new HashMap<>();

    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) throws IOException {
        try{
        reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8));}
        catch(Exception FileNotFoundException){
            System.out.print("Wrong file name.");
        }
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
        // nazwy kolumn w takiej kolejności, jak w pliku
    }

    CSVReader(String filename,String delimiter) throws IOException{
        try{
            reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8));}
        catch(Exception FileNotFoundException){
            System.out.print("Wrong file name.");
        }

        this.delimiter = delimiter;
        this.hasHeader = true;
        parseHeader();
    }

    CSVReader(String filename) throws IOException{
        try{
            reader = new BufferedReader(new FileReader(filename,StandardCharsets.UTF_8));}
        catch(Exception FileNotFoundException){
            System.out.print("Wrong file name.");
        }
        this.delimiter = ",";
        this.hasHeader = true;
        parseHeader();
    }

    public CSVReader(Reader reader, String delimiter, boolean hasHeader) throws IOException {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }

    public CSVReader(Reader reader) throws IOException {
        this(reader, ",",true);
    }

    void parseHeader() throws IOException {
        // wczytaj wiersz
        String line  = reader.readLine();
        if(line==null){
            return;
        }
        // podziel na pola
        String[]header = line.split(delimiter);
        // przetwarzaj dane w wierszu
        for(int i=0;i<header.length;i++){
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i],i);
            // dodaj nazwy kolumn do columnLabels i numery do columnLabelsToInt
        }}


    boolean next() throws IOException {
        // czyta następny wiersz, dzieli na elementy i przypisuje do current
        //
        String line = reader.readLine();
        if(line==null){
            return false;}
        this.current = line.split(delimiter);
        return true;

    }

    String get(int columnindex){
        if(columnindex > current.length){
            throw new RuntimeException("Wrong index of column!");
        }
        String el = current[columnindex];
        return (el==null) ? "": el;
    }

    String get(String columnlabel){
        int index;
        try{
        index = columnLabelsToInt.get(columnlabel);}
        catch(NullPointerException e){
            throw new RuntimeException("Name of the column is wrong!");}
        String el = current[index];
        return (el==null) ? "": el;
    }

    int getInt(int columnindex) throws NumberFormatException{
        String el = this.get(columnindex);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }

        return Integer.parseInt(el);
    }

    int getInt(String columnlabel){
        String el = this.get(columnlabel);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }
        return Integer.parseInt(el);
    }

    double getDouble(int columnindex){
        String el = this.get(columnindex);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }
        return Double.parseDouble(el);
    }

    double getDouble(String columnlabel){
        String el = this.get(columnlabel);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }
        return Double.parseDouble(el);
    }

    long getLong(int columnindex){
        String el = this.get(columnindex);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }
        return Long.parseLong(el);
    }

    long getLong(String columnlabel){
        String el = this.get(columnlabel);
        if(el.isEmpty()){
            throw new RuntimeException("the value can't be converted to number!");
        }
        return Long.parseLong(el);
    }

    List<String> getColumnLabels(){
        return columnLabels;
    }

    int getRecordLength(){
        return current.length;
    }

    boolean isMissing(String columnLabel){
        int columnIndex = columnLabelsToInt.get(columnLabel);
        return isMissing(columnIndex);
    }

    boolean isMissing(int columnIndex){
        if(columnIndex >= getRecordLength()){
        return true;
        }
        return current[columnIndex].isEmpty();
    }

    LocalTime getTime(int columnIndex, String format){
        String val = this.get(columnIndex);
        return LocalTime.parse(val, DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(int columnIndex, String format){
        String val = this.get(columnIndex);
        return LocalDate.parse(val, DateTimeFormatter.ofPattern(format));
    }





}