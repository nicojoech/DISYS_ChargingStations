package org.example.Queue;

import com.rabbitmq.client.*;
import org.example.Services.DataCollectionService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SubscriberNumber {
    private final static String queueName = "messageInfoQueue";

    //https://www.rabbitmq.com/tutorials/tutorial-one-java.html - default setup of sender and receiver
    public static void receive(DataCollectionService dataCollectionService) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                //receives number of messages that should be waited for
                String message = new String(body, "UTF-8");
                try {
                    dataCollectionService.gatherData(message);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(message + " message(s) have to be received");
            }
        };

        channel.basicConsume(queueName, true, consumer);

    }
}
