package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Callback;
import jdk.internal.dynalink.linker.LinkerServices;

import java.awt.*;
import java.net.URL;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Bartłomiej on 29.12.2016.
 */
public class RepairController implements Initializable {

    @FXML Button checkRepairHistoryButton;

    @FXML Button ShowButton;

    @FXML ChoiceBox VehicleModelChoice;

    @FXML ChoiceBox statusChoiceBox;

    @FXML TableView RepairTable;

    private final String quotation = "\"";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //SQLcommands sqlCommands = new SQLcommands();

        VehicleModelChoice.setItems(FXCollections.observableArrayList("maluch", "mercedes", "fiat", "toyota"));
        VehicleModelChoice.setTooltip(new Tooltip("select vehicle model"));
        statusChoiceBox.setItems(FXCollections.observableArrayList("wolny","zajety"));
        statusChoiceBox.setTooltip(new Tooltip("select status of vehicle"));

        try {
            showVehicles();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showVehicles() throws Exception{

        RepairTable.getColumns().clear();
        int colNum = 0;

        String tableName = "pojazd";
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        SQLcommands sqLcommands = new SQLcommands();
        ArrayList<String> columnName = new ArrayList<>(sqLcommands.getColumnsNames(tableName));


        colNum = sqLcommands.getNumberOfColumns(tableName);
        for (int i = 0; i < colNum; i++) {
            final int j = i;
            TableColumn col = new TableColumn(columnName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            RepairTable.getColumns().addAll(col);
        }
        ArrayList<String> requirements = new ArrayList<>();
        String req = "";

        if(!VehicleModelChoice.getSelectionModel().isEmpty()){
            req = quotation+"nazwa_modelu"+quotation+"='"+VehicleModelChoice.getSelectionModel().getSelectedItem().toString()+"'";
            requirements.add(req);
        }
        if(!statusChoiceBox.getSelectionModel().isEmpty()){
            req = quotation+"status"+quotation+"='"+statusChoiceBox.getSelectionModel().getSelectedItem().toString()+"'";
            requirements.add(req);
        }
        ObservableList<ObservableList> table = sqLcommands.getTable(tableName,requirements,colNum);

        RepairTable.setItems(table);
        sqLcommands.conn.close();
    }

    public void clearAction(){
        VehicleModelChoice.getSelectionModel().clearSelection();
        statusChoiceBox.getSelectionModel().clearSelection();
        ShowButton.fire();
    }

    public void checkHistoryAction(){
        ObservableList<ObservableListWrapper> row = RepairTable.getSelectionModel().getSelectedItems();
        ObservableListWrapper selectedCells = row.get(0);

        if(row.isEmpty()){
            Alert dialog = new Alert(Alert.AlertType.WARNING, "Warning", ButtonType.OK,ButtonType.CANCEL);
            dialog.setHeaderText("Cannot handle operation");
            dialog.setContentText("You have to select vehile in the table");
            dialog.setResizable(true);
            dialog.getDialogPane().setPrefSize(250, 100);
            dialog.showAndWait();
        }
        else {

            SelectedVehicle.id_pojazdu = Integer.decode(selectedCells.get(0).toString());
            SelectedVehicle.status = selectedCells.get(1).toString();
            SelectedVehicle.nazwa_modelu = selectedCells.get(2).toString();
            SelectedVehicle.id_typu_pojazdu = Integer.decode(selectedCells.get(3).toString());

            System.out.println("SelectedVehicle.id_pojazdu = " + SelectedVehicle.id_pojazdu);
            System.out.println("SelectedVehicle.id_typu_pojazdu = " + SelectedVehicle.id_typu_pojazdu);
            System.out.println("SelectedVehicle.nazwa_modelu = " + SelectedVehicle.nazwa_modelu);


            try {
                    Parent repairHistory = FXMLLoader.load(getClass().getResource("RepairHistory.fxml"));
                    Scene repairHistoryScene = new Scene(repairHistory);
                    Stage repairHistoryStage = new Stage();
                    repairHistoryStage.setTitle("Vehicle Repair History");
                    repairHistoryStage.setScene(repairHistoryScene);
                    repairHistoryStage.setMinWidth(650);
                    repairHistoryStage.setMinHeight(540);
                    repairHistoryStage.setOnCloseRequest(event -> checkRepairHistoryButton.setDisable(false));
                    repairHistoryStage.show();
                    checkRepairHistoryButton.setDisable(true);


            } catch (Exception e) {
                e.getMessage();
            }

        }

    }
}
