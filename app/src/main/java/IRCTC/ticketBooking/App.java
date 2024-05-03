/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package IRCTC.ticketBooking;

import IRCTC.ticketBooking.entities.Train;
import IRCTC.ticketBooking.entities.User;
import IRCTC.ticketBooking.services.UserBookingService;
import IRCTC.ticketBooking.utils.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {


    public static void main(String[] args) {
        System.out.println("Running IRCTC Train Booking Application");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userbookingService;
        Train trainSelectedForBooking;
        try {
            userbookingService = new UserBookingService();
            trainSelectedForBooking = new Train();
        } catch (Exception e) {
            System.out.println("Error loading user data");
            return;
        }
        while(option != 7){
            System.out.println("Choose the option");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Booking");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit");
            option = scanner.nextInt();
            switch (option){
                case 1:
                    System.out.println("Enter your name");
                    String nameToSignup = scanner.next();
                    System.out.println("Enter your password");
                    String passwordToSignup = scanner.next();
                    User userToSignUp = new User(nameToSignup, passwordToSignup, UserServiceUtil.hashPassword(passwordToSignup),new ArrayList<>(), UUID.randomUUID().toString());
                    userbookingService.signUp(userToSignUp);
                    System.out.println("User signed up successfully" );
                    break;
                case 2:
                    System.out.println("Enter your name");
                    String loginName = scanner.next();
                    System.out.println("Enter your password");
                    String loginPassword = scanner.next();
                    User userToLogin = new User(loginName, loginPassword, UserServiceUtil.hashPassword(loginPassword),new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userbookingService = new UserBookingService((userToLogin));
                    } catch (IOException e) {
                        System.out.println("Error loading user data");
                        return;
                    }
                    if(userbookingService.loginUser()) {
                        System.out.println("User logged in successfully");
                    }
                    break;
                case 3:
                    System.out.println("Fetching booking");
                    try {
                        userbookingService.fetchBookings();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 4:
                    System.out.println("Type your source station");
                    String source = scanner.next();
                    System.out.println("Type your destination station");
                    String dest = scanner.next();
                    List<Train> trains = userbookingService.getTrains(source, dest);
                    System.out.println(trains.size() + " trains found");
                    int index = 1;
                    for (Train t : trains) {
                        System.out.println("Train " + index++);
                        System.out.println("Train id : " + t.getTrainId());
                        System.out.println();
                        System.out.println("Train Time-Table");
                        System.out.println("Station : Time" );

                      for (Map.Entry<String, String> entry : t.getStationTime().entrySet()) {
                            System.out.println(entry.getKey() + " : " + entry.getValue());
                       }
                        for (List<Integer> row : t.getSeats()) {
                            for (Integer seat : row) {
                                System.out.print(seat + " ");
                            }
                            System.out.println();
                        }

                        System.out.println();
                    }
                    System.out.println("Select the train by typing the train id");
                    String trainId = scanner.next();
                    Optional<Train> selectedTrain = trains.stream().filter(train -> train.getTrainId().equals(trainId)).findFirst();
                    if(selectedTrain.isPresent()){
                        trainSelectedForBooking = selectedTrain.get();
                        System.out.println(trainSelectedForBooking.getTrainInfo());
                        System.out.println("Train selected");
                    }else{
                        System.out.println("Train not found");
                    }
                    break;
                case 5:
                    System.out.println("Select a seat out of these seats");
                    System.out.println(trainSelectedForBooking.getTrainId());
                    List<List<Integer>> seats = userbookingService.fetchSeats(trainSelectedForBooking);
                    System.out.println(seats.size() + " rows found(0->3)");
                    System.out.println(seats.get(0).size() + " columns found(0->5)");
                    for (List<Integer> row: seats){
                        for (Integer val: row){
                            System.out.print(val+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column");
                    int col = scanner.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean booked = userbookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                    if(booked.equals(Boolean.TRUE)){
                        System.out.println("Booked! Enjoy your journey");
                    }else{
                        System.out.println("Can't book this seat");
                    }
                    break;
                case 6:
                    System.out.println("Enter the ticket id to cancel");
                    String ticketId = scanner.next();
                    Boolean cancelled = userbookingService.cancelBooking(ticketId);

                    if(cancelled.equals(Boolean.TRUE)){
                        System.out.println("Ticket cancelled successfully");
                    }else{
                        System.out.println("Ticket not found");
                    }
                    break;
                default:
                    break;




            }
        }
    }
}
