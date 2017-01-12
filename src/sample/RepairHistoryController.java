package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bartłomiej on 08.01.2017.
 */
public class RepairHistoryController implements Initializable {
    @FXML
    TableView historyTable;
    @FXML
    DatePicker repairDate;
    @FXML
    ChoiceBox repairStatusChoiceBox;
    @FXML
    ChoiceBox kindOfRepairChoiceBox;
    @FXML
    Button refreshButton;

    SelectedVehicle selectedVehicle;

    SQLcommands sqLcommands;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedVehicle = new SelectedVehicle();
        sqLcommands = new SQLcommands();


    }
}
