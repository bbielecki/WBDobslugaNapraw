package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Bartłomiej on 21.01.2017.
 */
public class ServiceRepairController implements Initializable{

    @FXML
    TableView serviceTable;
    @FXML
    Button refreshButton, clearButton;
    @FXML
    DatePicker repairDate;
    @FXML
    DatePicker afterDatePicker;
    @FXML
    ChoiceBox repairStatusChoiceBox;
    @FXML
    TextField kindOfRepairText;


    String quotation = "\"";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            showServiceHistory();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        repairStatusChoiceBox.setItems(FXCollections.observableArrayList("oczekujaca", "wykonana", "anulowana"));
        repairStatusChoiceBox.setTooltip(new Tooltip("select repair status"));

    }

    public void showServiceHistory() throws SQLException {
        int colNum = 0;
        serviceTable.getColumns().clear();

        SQLcommands sqLcommands = new SQLcommands();
        ArrayList<String> columnName = new ArrayList<>(sqLcommands.getColumnsNamesService());

        colNum = 9;

        for (int i = 0; i < colNum; i++) {
            final int j = i;
            TableColumn col = new TableColumn(columnName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            serviceTable.getColumns().addAll(col);
        }
        ArrayList<String> requirements = new ArrayList<>();
        String req = "";
        String statement = "select \"wykonana_naprawa\".\"id_wykonanej_naprawy\",\"wykonana_naprawa\".CZYNNOSC,\"wykonana_naprawa\".\"status\", \"model\".\"nazwa_modelu\",\"model\".PRODUCENT, \"model\".KRAJ_POCHODZENIA, \"wykonana_naprawa\".\"calkowity_koszt_naprawy\", \"wykonana_naprawa\".DATA_NAPRAWY, \"wykonana_naprawa\".\"id_przegladu\" from \"wykonana_naprawa\" join \"usluga_przegladu\" on  \"wykonana_naprawa\".\"id_przegladu\" = \"usluga_przegladu\".\"id_przegladu\" join \"pozycja_plan_serwisowy\" on \"usluga_przegladu\".\"ID_POZYCJI_PLANU\"=\"pozycja_plan_serwisowy\".\"id_pozycji_planu\" join \"model\" on \"pozycja_plan_serwisowy\".\"nazwa_modelu\"=\"model\".\"nazwa_modelu\" WHERE  \"pozycja_plan_serwisowy\".\"nazwa_modelu\" = \n"+ "'"+ SelectedVehicle.nazwa_modelu+"'";

        if((repairDate.getValue() != null) && (afterDatePicker.getValue() == null)){
            System.out.println(repairDate.getValue());
            req = quotation+"wykonana_naprawa"+quotation+"."+quotation+"DATA_NAPRAWY"+quotation+"='"+repairDate.getValue().toString()+"'";
            requirements.add(req);
        }
        if(afterDatePicker.getValue() != null ){
            System.out.println(afterDatePicker.getValue());
            req = quotation+"wykonana_naprawa"+quotation+"."+quotation+"DATA_NAPRAWY"+quotation+">'"+afterDatePicker.getValue().toString()+"'";
            requirements.add(req);
        }
        if( !repairStatusChoiceBox.getSelectionModel().isEmpty()){
            req = quotation+"wykonana_naprawa"+quotation+"."+quotation+"status"+quotation+"='"+repairStatusChoiceBox.getSelectionModel().getSelectedItem().toString()+"'";
            requirements.add(req);
        }
        if( !kindOfRepairText.getText().isEmpty()){
            req = quotation+"wykonana_naprawa"+quotation+"."+quotation+"CZYNNOSC"+quotation+"='"+kindOfRepairText.getText()+"'";
            requirements.add(req);
        }

        ObservableList<ObservableList> table = sqLcommands.getHistoryTable(9,requirements, statement);

        serviceTable.setItems(table);
    }

    public void clearAction(){
        repairStatusChoiceBox.getSelectionModel().clearSelection();
        kindOfRepairText.clear();
        repairDate.setValue(null);
        afterDatePicker.setValue(null);

        refreshButton.fire();

    }

    public void onAfterDateSelection(){
        repairDate.setValue(null);
    }

    public void refreshAction() throws SQLException {
        showServiceHistory();
    }

}
