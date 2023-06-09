package org.example.Queue;

import com.itextpdf.text.DocumentException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Services.GeneratorService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Subscriber {

    private final static String queueName = "completeDataQueue";

    //https://www.rabbitmq.com/tutorials/tutorial-one-java.html - default setup of sender and receiver
    public static void receive(GeneratorService generatorService) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            //process starts when gathered Data is received (as JSON String)
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            try {
                generatorService.gatherData(message);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });


    }
}

