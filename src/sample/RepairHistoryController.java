package sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;
import static sample.SelectedVehicle.status;

/**
 * Created by Bartłomiej on 08.01.2017.
 */
public class RepairHistoryController implements Initializable {
    @FXML
    TableView historyTable;
    @FXML
    DatePicker repairDate;
    @FXML
    DatePicker afterDatePicker;
    @FXML
    ChoiceBox repairStatusChoiceBox;
    @FXML
    TextField kindOfRepairText;
    @FXML
    Button refreshButton;
    @FXML Button showServiceHistoryButton;
    @FXML Button clearButton;

    @FXML Label enterLabel, infoLabel;
    @FXML TextField enterCodeText;
    @FXML Button enterButton;

    @FXML TextField insertKindText, insertPriceText;
    @FXML ChoiceBox insertRepairStatusChoice;
    @FXML DatePicker insertRepairDate;
    @FXML Button insertButton;
    @FXML Label insertInfo;
    @FXML Button refreshButton1;

    @FXML Label modifyDateLabel, modifyStatusLabel, enterModifyLabel, modifyInfoLabel;
    @FXML Button enterModifyButton, modifyButton;
    @FXML TextField enterModifyText;
    @FXML ChoiceBox modifyStatusChoice;
    @FXML DatePicker modifyDate;


    String password = "abc";

    SQLcommands sqLcommands;

    private final String quotation = "\"";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        insertKindText.setVisible(false);
        insertPriceText.setVisible(false);
        insertRepairDate.setVisible(false);
        insertRepairStatusChoice.setVisible(false);
        insertButton.setVisible(false);
        refreshButton1.setVisible(false);

        modifyButton.setVisible(false);
        modifyDate.setVisible(false);
        modifyDateLabel.setVisible(false);
        modifyStatusChoice.setVisible(false);
        modifyStatusLabel.setVisible(false);


        sqLcommands = new SQLcommands();
        try {
            showHistory();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        repairStatusChoiceBox.setItems(FXCollections.observableArrayList("oczekujaca", "wykonana", "anulowana"));
        repairStatusChoiceBox.setTooltip(new Tooltip("select repair status"));

        insertRepairStatusChoice.setItems(FXCollections.observableArrayList("oczekujaca", "wykonana", "anulowana"));
        insertRepairStatusChoice.setTooltip(new Tooltip("select repair status"));

        modifyStatusChoice.setItems(FXCollections.observableArrayList("oczekujaca", "wykonana", "anulowana"));
        modifyStatusChoice.setTooltip(new Tooltip("select repair status"));

    }

    public void showHistory() throws SQLException {
        int colNum = 0;
        historyTable.getColumns().clear();

        SQLcommands sqLcommands = new SQLcommands();
        ArrayList<String> columnName = new ArrayList<>(sqLcommands.getColumnsNamesHistory());

        colNum = 6;

        for (int i = 0; i < colNum; i++) {
            final int j = i;
            TableColumn col = new TableColumn(columnName.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            historyTable.getColumns().addAll(col);
        }

        ArrayList<String> requirements = new ArrayList<>();
        String req = "";

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

        String statement = "select \"wykonana_naprawa\".\"id_wykonanej_naprawy\", \"wykonana_naprawa\".\"CZYNNOSC\", \"wykonana_naprawa\".\"status\", \"pojazd\".\"nazwa_modelu\", \"wykonana_naprawa\".\"calkowity_koszt_naprawy\", \"wykonana_naprawa\".\"DATA_NAPRAWY\" from \"wykonana_naprawa\" join \"naprawa\" on  \"wykonana_naprawa\".\"id_naprawy\" = \"naprawa\".\"id_naprawy\" join \"pojazd\" on  \"pojazd\".\"id_pojazdu\" = \"naprawa\".\"id_pojazdu\" where \"pojazd\".\"id_pojazdu\" = " + SelectedVehicle.id_pojazdu;

        ObservableList<ObservableList> table = sqLcommands.getHistoryTable(colNum, requirements, statement);

        historyTable.setItems(table);
    }

    public void onAfterDateSelection(){
        repairDate.setValue(null);
    }

    public void clearFiltrAction(){
        repairStatusChoiceBox.getSelectionModel().clearSelection();
        kindOfRepairText.clear();
        repairDate.setValue(null);
        afterDatePicker.setValue(null);

        refreshButton.fire();
    }

    public void refreshAction() throws SQLException {
        showHistory();
    }

    public void showServiceHistory(){


        try {
            Parent repairHistory = FXMLLoader.load(getClass().getResource("ServiceRepair.fxml"));
            Scene repairHistoryScene = new Scene(repairHistory);
            Stage repairHistoryStage = new Stage();
            repairHistoryStage.setTitle("Service History");
            repairHistoryStage.setScene(repairHistoryScene);
            repairHistoryStage.setMinWidth(900);
            repairHistoryStage.setMinHeight(540);
            repairHistoryStage.setOnCloseRequest(event -> showServiceHistoryButton.setDisable(false));
            repairHistoryStage.show();
            showServiceHistoryButton.setDisable(true);


        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void enterAction(){
        if(enterCodeText.getText().equals(password) || enterModifyText.getText().equals(password)){
            insertKindText.setVisible(true);
            insertPriceText.setVisible(true);
            insertRepairDate.setVisible(true);
            insertRepairStatusChoice.setVisible(true);
            insertButton.setVisible(true);
            refreshButton1.setVisible(true);

            enterCodeText.setVisible(false);
            enterButton.setVisible(false);
            enterLabel.setVisible(false);
            infoLabel.setVisible(false);

            enterModifyButton.setVisible(false);
            enterModifyLabel.setVisible(false);
            enterModifyText.setVisible(false);
           //modifyInfoLabel.setVisible(false);

            modifyDateLabel.setVisible(true);
            modifyDate.setVisible(true);
            modifyDateLabel.setVisible(true);
            modifyStatusLabel.setVisible(true);
            modifyStatusChoice.setVisible(true);
            modifyButton.setVisible(true);
            modifyInfoLabel.setVisible(true);
            modifyInfoLabel.setText(null);

        }
        else {
            infoLabel.setText("wrong password");
            modifyInfoLabel.setText("wrong password");
        }
    }

    public void modifyAction() throws SQLException {
        ModifyData modifyData = new ModifyData();
        boolean modify = false;
        ObservableList<ObservableListWrapper> row = historyTable.getSelectionModel().getSelectedItems();
        ObservableListWrapper selectedCells = row.get(0);

        if(!modifyStatusChoice.getSelectionModel().isEmpty()){
            System.out.println("jest status");
            ModifyData.status = modifyStatusChoice.getSelectionModel().getSelectedItem().toString();
            modify = true;
        }
        if(modifyDate.getValue()!=null){
            System.out.println("jest data");
            ModifyData.repairDate = modifyDate.getValue().toString();
            modify = true;
        }
        if(row.isEmpty()){
            modifyInfoLabel.setText("Please, select a repair");
            System.out.println("nie zaznaczono");
            modify = false;
        }
        if(!row.isEmpty()){
            modifyInfoLabel.setText("");
            System.out.println("ok");
            modify = true;
            ModifyData.repairID = Integer.decode(selectedCells.get(0).toString());
            System.out.println(ModifyData.repairID);
        }

        if(modify){
            sqLcommands.modifyRepair();
        }
    }

    public void insertAction() throws SQLException {

        String info = "";
        int price = 0;
        boolean insert = true;

        if(!insertKindText.getText().isEmpty()){
            InsertData.kindOfRepair=insertKindText.getText();
        }else {
            info = "Kind of repair not provided";
            insert = false;
        }
        if(!insertPriceText.getText().isEmpty()){
            try {
                price = Integer.decode(insertPriceText.getText());
            }catch (Exception e){
                info = "Price of repair - wrong walue";
                insert = false;
            }
            InsertData.repairPrice = price;
        }else {
            info = "Price of repair not provided";
            insert = false;
        }
        if(insertRepairDate.getValue()!=null){
            InsertData.repairDate = insertRepairDate.getValue().toString();
        }
        else {
            info = "Date not provided";
            insert = false;
        }
        if(!insertRepairStatusChoice.getSelectionModel().isEmpty()){
            InsertData.status = insertRepairStatusChoice.getSelectionModel().getSelectedItem().toString();
        }
        else {
            info = "Status not provided";
            insert = false;
        }
        insertInfo.setText(info);

        if(insert)
            sqLcommands.insertRepair();

        insertInfo.setText("Record has been inserted!");

    }
}
