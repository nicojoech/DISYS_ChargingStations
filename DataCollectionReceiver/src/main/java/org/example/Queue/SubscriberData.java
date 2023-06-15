package org.example.Queue;

import com.rabbitmq.client.*;
import org.example.Services.DataCollectionService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SubscriberData {
    private final static String queueName = "dataGatheringQueue";
    private static int messageCount = 0;

    public static void receive(int number, DataCollectionService dataCollectionService) throws IOException, TimeoutException {

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

                //receives data of a specific customer for a specific charging station (as Json String)
                String message = new String(body, "UTF-8");

                //writes string to a private list of the dataCollectionService
                dataCollectionService.writeMsgToList(message);
                System.out.println("Received message: " + message);
                messageCount++;

                System.out.println("Current message count: " + messageCount);

                //if all messages have been received the final list is formatted and published to the PDFService
                if (messageCount == number) {
                    dataCollectionService.formatAndPublish();
                    messageCount = 0;
                }
            }
        };

        channel.basicConsume(queueName, true, consumer);


    }
}
