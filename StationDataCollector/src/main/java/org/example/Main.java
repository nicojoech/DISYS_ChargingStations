package org.example;

import org.example.Service.ServiceSDC;
import org.example.Queue.SubscriberSDC;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws Exception {
        ServiceSDC serviceSDC = new ServiceSDC();
        SubscriberSDC.receive(serviceSDC);
    }
}