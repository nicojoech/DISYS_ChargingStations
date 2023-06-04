package org.example.Services;

import org.example.Queue.Subscriber;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageInfoService implements IReceiverService {

    private final String inQueue;

    public MessageInfoService(String inQueue){
        this.inQueue = inQueue;
    }

    @Override
    public void work() {
        System.out.println("Message received");
    }

    public void start() throws IOException, TimeoutException {
        while (true){
            String input = Subscriber.receive(inQueue);
            System.out.println("Message received");






        }

    }



}
