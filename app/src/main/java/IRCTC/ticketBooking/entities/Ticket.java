package IRCTC.ticketBooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Ticket {
    private  String ticketId;
    private String userId;
    private String source;
    private String destination;
    private String dateOfTravel;
    private Train train;
    private int seatNo;
    public  Ticket(){}
    public Ticket(String ticketId,String userId,String source,String destination,String dateOfTravel,Train train,int seatNo){
        this.ticketId=ticketId;
        this.userId=userId;
        this.source=source;
        this.destination=destination;
        this.dateOfTravel=dateOfTravel;
        this.train=train;
        this.seatNo=seatNo;
    }
    public  String getTicketInfo(){
        return String.format("Ticket ID: %s to belongs to User %s from %S to %s on %s in train %s with seat no %d",ticketId,userId,source,destination,dateOfTravel,train.getTrainNo(),seatNo);
    }
    public int getSeatNo(){
        return this.seatNo;
    }
    public String getTicketId(){
        return  this.ticketId;
    }
    public void setTicketId(String ticketId){
        this.ticketId=ticketId;
    }
    public String getSource(){
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDateOfTravel(String dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }

    public String getDateOfTravel() {
        return dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
