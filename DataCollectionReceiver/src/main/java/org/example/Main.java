package org.example;

import org.example.Services.DataCollectionService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {

        DataCollectionService dataCollectionService = new DataCollectionService("messageInfoQueue");
        dataCollectionService.work();

    }
}