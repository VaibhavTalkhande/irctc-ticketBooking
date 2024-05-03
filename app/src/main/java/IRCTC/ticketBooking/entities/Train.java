package IRCTC.ticketBooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.sql.Time;
import java.util.List;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)//ignores any unknown properties in the JSON file
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)//converts the camelCase to snake_case
@Builder//builder pattern lombok is used to create the object of the class
public class Train {
    private String trainId;
    private String trainNo;
    private List<List<Integer>> seats;
    private Map<String, String> stationTime;
    private List<String> stations;
    public Train(){}
    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, String> stationTime, List<String> stations){
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTime = stationTime;
        this.stations = stations;
    }
    public String getTrainInfo(){
        return String.format("Train ID: %s, Train No: %s, Stations: %s, Seats: %s, Station Time: %s",trainId,trainNo,stations,seats,stationTime);
    }
    public List<String> getStations() {
        return this.stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public List<List<Integer>> getSeats() {
        return this.seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }

    public String getTrainId() {
        return this.trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }
    public String getTrainNo() {
        return this.trainNo;
    }
    public  void setTrainNo(String trainNo){
        this.trainNo = trainNo;
    }
    public Map<String, String> getStationTime() {
        return this.stationTime;
    }
    public void setStationTime(Map<String, String> stationTime) {
        this.stationTime = stationTime;
    }
}
