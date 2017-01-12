package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Console;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.O;

/**
 * Created by Bartłomiej on 29.12.2016.
 */
public class SQLcommands {

    Connection conn;
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

    public ArrayList<String> getColumnsNames(String tableName) throws SQLException{

        ArrayList<String> colNames = new ArrayList<>();
        PreparedStatement pst = conn.prepareStatement("SELECT column_name  FROM user_updatable_columns  WHERE table_name = '"+tableName+"'");
        ResultSet rs = pst.executeQuery();

        while (rs.next()){
            colNames.add(rs.getString(1));
        }

        return colNames;
    }

    public int getRowsNumber(String taleName) throws SQLException{

        PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM "+quoatation+taleName+quoatation+"");
        ResultSet rs = pst.executeQuery();
        if(rs.next())
            return Integer.decode(rs.getString(1));
        else
            return 0;
    }

    public ObservableList<ObservableList> getTable( String tableName, ArrayList<String> requirements, int rowsNum, int colNum) throws SQLException{

        String statement;
        ObservableList<ObservableList> table = FXCollections.observableArrayList();
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

        while (rs.next()){
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i<=colNum;i++){
                row.add(rs.getString(i));
            }
            table.add(row);
        }

        return table;
    }



}
