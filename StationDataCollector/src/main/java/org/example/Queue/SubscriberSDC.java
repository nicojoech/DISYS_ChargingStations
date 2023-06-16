package org.example.Queue;

import com.itextpdf.text.DocumentException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Service.ServiceSDC;

import java.nio.charset.StandardCharsets;

public class SubscriberSDC {

    private final static String queueName = "dataToCollect";
    public static void receive(ServiceSDC serviceSDC) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            try {
                serviceSDC.gatherData(message);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
