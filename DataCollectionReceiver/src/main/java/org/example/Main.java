package org.example;

import org.example.Services.IReceiverService;
import org.example.Services.MessageInfoService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {

        IReceiverService receiverService = new MessageInfoService("messageInfoQueue");
        receiverService.work();

    }
}