package IRCTC.ticketBooking.services;
import java.io.IOException;
import IRCTC.ticketBooking.entities.Train;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {
    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/IRCTC/ticketBooking/localDB/trains.json";
    public TrainService() throws IOException{
        loadTrainFromFile();
    }
    private void loadTrainFromFile() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }
    public List<Train> searchTrain(String source, String destination) {
        System.out.println("Searching for trains from " + source + " to " + destination);
        List<Train> t=trainList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
        return t;
    }
    private boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();
        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        System.out.println("Source Index: " + sourceIndex + " Destination Index: " + destinationIndex);
        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }
    public void addTrain(Train newTrain) {
        Optional<Train> trainExist = trainList.stream().filter(train -> train.getTrainId().equals(newTrain.getTrainId())).findFirst();
        if (trainExist.isPresent()) {
            //if found update the train
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train updatedTrain) {
        // Find the index of the train with the same trainId
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            // If found, replace the existing train with the updated one
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            // If not found, treat it as adding a new train
            addTrain(updatedTrain);
        }
    }

    public void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
