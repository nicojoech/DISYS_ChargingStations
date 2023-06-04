package org.example.Services;

import org.example.Queue.Subscriber;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DataCollectionService {

    private final String inQueue;

    public DataCollectionService(String inQueue){
        this.inQueue = inQueue;
    }
    public void work() throws IOException, TimeoutException {
        while (true) {
            String input = Subscriber.receive(inQueue);
            System.out.println("DataGatherJob received");
            gatherData(input);
        }
    }

    public void gatherData(String input){
        //receives from other queue (e.g. DataCollectionQueue)
    }
}
