package org.example;

import org.example.Queue.SubscriberDCD;
import org.example.Service.ServiceDCD;

public class Main {
    public static void main(String[] args) throws Exception {
        ServiceDCD serviceDCD = new ServiceDCD();
        SubscriberDCD.receive(serviceDCD);
    }
}