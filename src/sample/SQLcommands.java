package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.LocalGregorianCalendar;

import java.io.Console;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;
import static javafx.scene.input.KeyCode.L;
import static javafx.scene.input.KeyCode.O;

/**
 * Created by Bartłomiej on 29.12.2016.
 */
public class SQLcommands {

    public Connection conn;
    Connection conn2;
    SimpleDateFormat sdf;
    private String driver="oracle.jdbc.driver.OracleDriver";
    String quoatation = "\"";

    public SQLcommands() {

        try {

            Class.forName(driver);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "janek2901");
            conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/world","root","janek2901");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getNumberOfColumns(String tableName) throws SQLException {
        PreparedStatement pst = conn.prepareStatement("SELECT * FROM "+quoatation+tableName+quoatation+"");
        ResultSet rs = pst.executeQuery();
        System.out.println(rs.getMetaData().getColumnCount());
        return rs.getMetaData().getColumnCount();

    }

    public int getNumberOfColumnsHistory() throws SQLException {
        PreparedStatement pst = conn.prepareStatement("select \"wykonana_naprawa\".\"id_wykonanej_naprawy\", \"wykonana_naprawa\".\"status\", \"wykonana_naprawa\".\"calkowity_koszt_naprawy\", \"wykonana_naprawa\".\"DATA_NAPRAWY\", \"wykonana_naprawa\".\"CZYNNOSC\", \"pojazd\".\"nazwa_modelu\"  from \"wykonana_naprawa\" join \"naprawa\" on  \"wykonana_naprawa\".\"id_naprawy\" = \"naprawa\".\"id_naprawy\" join \"pojazd\" on  \"pojazd\".\"id_pojazdu\" = \"naprawa\".\"id_pojazdu\" where \"pojazd\".\"id_pojazdu\" = "+SelectedVehicle.id_pojazdu);
        ResultSet rs = pst.executeQuery();
        System.out.println(rs.getMetaData().getColumnCount());
        return rs.getMetaData().getColumnCount();

    }

    public ArrayList<String> getColumnsNames(String tableName) throws SQLException{

        ArrayList<String> colNames = new ArrayList<>();
        PreparedStatement pst = conn.prepareStatement("SELECT column_name  FROM user_updatable_columns  WHERE table_name = '"+tableName+"'");
        ResultSet rs = pst.executeQuery();

        while (rs.next()){
            colNames.add(rs.getString(1));
        }

        return colNames;
    }

    public ArrayList<String> getColumnsNamesHistory() throws SQLException{

        ArrayList<String> colNames = new ArrayList<>();
        colNames.add("id wykonanej naprawy");
        colNames.add("wykonana czynnosc");
        colNames.add("status");
        colNames.add("model pojazdu");
        colNames.add("koszt naprawy");
        colNames.add("data naprawy");

        return colNames;
    }

    public ArrayList<String> getColumnsNamesService(){
        ArrayList<String> colNames = new ArrayList<>();
        colNames.add("id wykonanej naprawy");
        colNames.add("wykonana czynnosc");
        colNames.add("status");
        colNames.add("model pojazdu");
        colNames.add("producent");
        colNames.add("kraj poczenia");
        colNames.add("koszt naprawy");
        colNames.add("data naprawy");
        colNames.add("id przegladu");

        return colNames;
    }


    public ObservableList<ObservableList> getTable( String tableName, ArrayList<String> requirements, int colNum) throws SQLException{

        String statement;
        if(requirements.isEmpty()){
            statement = "SELECT * FROM "+quoatation+tableName+quoatation;
        }
        else {
            statement = "SELECT * FROM "+quoatation+tableName+quoatation+" WHERE ";
            for (String req : requirements) {
                statement = statement + req + " AND ";
            }
            statement = statement.substring(0,statement.lastIndexOf("A"));
        }

        System.out.println(statement);

        PreparedStatement pst = conn.prepareStatement(statement);
        ResultSet rs = pst.executeQuery();

        return addRowsToTable(rs,colNum);
    }

    public ObservableList<ObservableList> getHistoryTable(int colNum, ArrayList<String> requirements, String statement) throws SQLException {

        if(requirements.isEmpty()){
            System.out.println(statement);
        }
        else {
            for (String req : requirements) {
                statement = statement+ " AND " + req;
            }
            //statement = statement.substring(0,statement.lastIndexOf("A"));
        }

        System.out.println(statement);

        PreparedStatement pst = conn.prepareStatement(statement);
        ResultSet rs = pst.executeQuery();

        return addRowsToTable(rs,colNum);
    }

    private ObservableList<ObservableList> addRowsToTable( ResultSet rs, int colNum) throws SQLException {

        ObservableList<ObservableList> table = FXCollections.observableArrayList();
        System.out.println("liczba kolumn: "+ colNum);

        while (rs.next()){
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i<=colNum;i++){
                //System.out.println(i);
                row.add(rs.getString(i));
            }
            table.add(row);
        }

        return table;
    }

    public void insertRepair() throws SQLException {
        int id = 1;
        String statement;

        statement = "select COUNT(*) from \"wykonana_naprawa\"";
        PreparedStatement preparedStatemen = conn.prepareStatement(statement);
        ResultSet rs = preparedStatemen.executeQuery();
        if(rs.next())
            id = rs.getInt(1) + 1;
        rs.close();

        statement = "INSERT INTO \"SYSTEM\".\"wykonana_naprawa\" (\"id_naprawy\", \"calkowity_koszt_naprawy\", \"status\", \"id_wykonanej_naprawy\", DATA_NAPRAWY, CZYNNOSC) VALUES ('"+ SelectedVehicle.id_pojazdu +"', '"+InsertData.repairPrice+"', '"+InsertData.status+"', '"+ id +"', TO_DATE('"+InsertData.repairDate+"', 'YYYY-MM-DD'), '"+InsertData.kindOfRepair+"')";
        preparedStatemen = conn.prepareStatement(statement);
        preparedStatemen.executeQuery();

    }

    public void modifyRepair() throws SQLException {
        String statement;

        statement = "UPDATE \"SYSTEM\".\"wykonana_naprawa\" SET \"status\" = '" + ModifyData.status + "', DATA_NAPRAWY = TO_DATE('" + ModifyData.repairDate + "', 'YYYY-MM-DD') WHERE \"wykonana_naprawa\".\"id_wykonanej_naprawy\" = " + ModifyData.repairID + "";
        System.out.println("modyfikacja :" + statement);
        System.out.println("naprawa id : " + ModifyData.repairID);
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.executeQuery();

        preparedStatement.close();
    }
}
