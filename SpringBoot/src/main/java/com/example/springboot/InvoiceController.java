package com.example.springboot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class InvoiceController {

    @PostMapping("/invoices")
    public Long generateInvoice(@RequestBody Invoice invoice) throws IOException, TimeoutException {
        Long customerId = invoice.getCustomerId();

        //Invoice generieren mit anderen Services
        sendMessage("DataCollectionDispatcher", String.valueOf(customerId));


        return customerId;
    }
    @GetMapping("/invoices/{customerId}")
    public ResponseEntity getInvoice(@PathVariable String customerId) {

        System.out.println("Invoice abfragen für " + customerId);

        String invoiceDirectory = "C:\\Users\\flori\\Documents\\FH\\4. Semester SS23\\DISYS";
        String filePath = invoiceDirectory + "\\" + customerId + ".pdf";

        System.out.println(filePath);

        //Invoice abfragen
        if (!checkAvailability(filePath)) {
            System.out.println("Nicht verfügbar");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        try {

            byte[] invoiceContent = Files.readAllBytes(Paths.get(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");

            LocalDateTime creationTime = LocalDateTime.now();
            headers.set("X-Creation-Time", creationTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            System.out.println(invoiceContent);
            return new ResponseEntity<>(invoiceContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Method for sending Messages to the Queue
    public void sendMessage (String QUEUE_NAME, String message) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }


    // Check file availability every 2 seconds
    @Scheduled(fixedDelay = 2000)
    private boolean checkAvailability(String filePath) {
        File invoiceFile = new File(filePath);
        return invoiceFile.exists();
    }

}



