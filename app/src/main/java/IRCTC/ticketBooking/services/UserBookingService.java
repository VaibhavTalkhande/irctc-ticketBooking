package IRCTC.ticketBooking.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import IRCTC.ticketBooking.entities.Ticket;
import IRCTC.ticketBooking.entities.Train;
import IRCTC.ticketBooking.entities.User;
import IRCTC.ticketBooking.utils.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public class UserBookingService{

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private User user;

    private final String USER_FILE_PATH = "app/src/main/java/IRCTC/ticketBooking/localDB/users.json";
    private final String TRAIN_FILE_PATH = "app/src/main/java/IRCTC/ticketBooking/localDB/trains.json";
    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
      //  System.out.println("Loading user data from file");
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
      //  System.out.println(userList);
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings() throws IOException {
        if (this.user == null) {
            System.out.println("User not logged in");
            return;
        }
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            System.out.println("Fetching bookings for user: " + userFetched.get().getName());
            System.out.println("Bookings: " + userFetched.get().getTicketsBooked().size());
            userFetched.get().printTickets();
        }
        else{
            System.out.println("User not found");
        }
    }

    public Boolean cancelBooking(String ticketId){
        if (this.user == null) {
            System.out.println("User not logged in");
            return Boolean.FALSE;
        }
        Optional<User> userFetched = userList.stream().filter(user->user.getName().equals(this.user.getName()) && UserServiceUtil.checkPassword(this.user.getPassword(), user.getHashedPassword())).findFirst();
       if (userFetched.isPresent()){
           List<Ticket> tickets = userFetched.get().getTicketsBooked();
           Optional<Ticket> ticketToCancel = tickets.stream().filter(ticket->ticket.getTicketId().equals(ticketId)).findFirst();
           try {
               List<Train> trainfile = objectMapper.readValue(new File(TRAIN_FILE_PATH), new TypeReference<List<Train>>() {});
               Optional<Train> train = trainfile.stream().filter(train1 -> train1.getTrainId().equals(ticketToCancel.get().getTrain().getTrainId())).findFirst();
               if (train.isPresent()){
                     List<List<Integer>> seats = train.get().getSeats();
                     for (int i = 0; i < seats.size(); i++) {
                          for (int j = 0; j < seats.get(i).size(); j++) {
                            if (seats.get(i).get(j) == ticketToCancel.get().getSeatNo()) {
                                 seats.get(i).set(j, 0);
                            }
                          }
                     }
                     train.get().setSeats(seats);
                     objectMapper.writeValue(new File(TRAIN_FILE_PATH), trainfile);
               }
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
           //tickets.removeIf(ticket -> tickets.getTicketId().equals(ticketId));
           if(ticketToCancel.isPresent()){
               tickets.remove(ticketToCancel.get());
                userFetched.get().setTicketsBooked(tickets);
                updateUser(userFetched.get());
                return Boolean.TRUE;
           }
           else {
               return Boolean.FALSE;
           }
       }

        return Boolean.FALSE;
    }

    public List<Train> getTrains(String source, String destination){
        if (this.user == null) {
            System.out.println("User not logged in");
            return new ArrayList<>();
        }
        try{
            TrainService trainService = new TrainService();
            System.out.println("Fetching trains");
            return trainService.searchTrain(source, destination);
        }catch(IOException ex){
            System.out.println("Error fetching trains");
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        if (this.user == null) {
            System.out.println("User not logged in");
            return Boolean.FALSE;
        }
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            int seatId = UUID.randomUUID().hashCode();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, seatId);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter the date of travel in the format yyyy-mm-dd");
                    String dateOfTravel = scanner.next();
                    System.out.println("Enter the source station");
                    String source = scanner.next();
                    System.out.println("Enter the destination station");
                    String destination = scanner.next();
                    Ticket ticket = new Ticket(UUID.randomUUID().toString(), user.getUserId(), source, destination, dateOfTravel, train,seatId);
                    System.out.println("Ticket booked: " + ticket.getTicketInfo());
                    user.getTicketsBooked().add(ticket);
//                    System.out.println(user.getTicketsBooked().contains(ticket));
//                    System.out.println(user.getUserId());
                    updateUser(this.user);
                    //updateUserTickets(user);
                    System.out.println("Seat booked successfully");
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }

    }
    public void updateUser(User user){
        System.out.println("Updating user");
        OptionalInt index = IntStream.range(0, userList.size())
                .filter(i -> userList.get(i).getName().equalsIgnoreCase(user.getName()))
                .findFirst();
        try{
            if (index.isPresent()) {
                userList.set(index.getAsInt(), user);
                System.out.println("User updated");
            } else {
                userList.add(user);
            }
            saveUserListToFile();
        }catch (IOException ex){
                System.out.println("Error updating user tickets" +ex.getMessage());
            }

    }

}