package sample;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Bartłomiej on 30.12.2016.
 */
public class VehicleTable {

    private final SimpleStringProperty VehicleID;
    private final SimpleStringProperty VehicleModel;
    private final SimpleStringProperty VehicleStatus;

    public VehicleTable(SimpleStringProperty vehicleID, SimpleStringProperty vehicleModel, SimpleStringProperty vehicleStatus) {
        VehicleID = vehicleID;
        VehicleModel = vehicleModel;
        VehicleStatus = vehicleStatus;
    }

    public String getVehicleID() {
        return VehicleID.get();
    }

    public SimpleStringProperty vehicleIDProperty() {
        return VehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.VehicleID.set(vehicleID);
    }

    public String getVehicleModel() {
        return VehicleModel.get();
    }

    public SimpleStringProperty vehicleModelProperty() {
        return VehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.VehicleModel.set(vehicleModel);
    }

    public String getVehicleStatus() {
        return VehicleStatus.get();
    }

    public SimpleStringProperty vehicleStatusProperty() {
        return VehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.VehicleStatus.set(vehicleStatus);
    }
}
