package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Queue.SubscriberNumber;
import org.example.Services.DataCollectionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {

        DataCollectionService dataCollectionService = new DataCollectionService(new ArrayList<>(), new ObjectMapper());
        SubscriberNumber.receive(dataCollectionService);
    }
}