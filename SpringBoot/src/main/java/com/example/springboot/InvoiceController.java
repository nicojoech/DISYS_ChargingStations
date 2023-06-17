package com.example.springboot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class InvoiceController {

    //POST Mapping for starting the gathering Job
    @PostMapping("/invoices")
    public Long generateInvoice(@RequestBody Invoice invoice) throws IOException, TimeoutException {
        Long customerId = invoice.getCustomerId();

        //Send customerId to the DataCollection Dispatcher
        sendMessage("DataCollectionDispatcher", String.valueOf(customerId));


        return customerId;
    }

    //GET Mapping for returning the generated invoice, if available, else status 404
    @GetMapping("/invoices/{customerId}")

    public ResponseEntity getInvoice(@PathVariable String customerId) {

        System.out.println("Invoice abfragen für " + customerId);

        // Get the base path of the project
        Path basePath = Paths.get("").toAbsolutePath();

        // Build the file path using the base path, "PDFGenerator", "createdInvoices
        Path path = basePath.resolve("PDFGenerator").resolve("createdInvoices").resolve(customerId+".pdf");

        String filePathString = path.toString();
        System.out.println("File Path: " + filePathString);

        //set File path
        File invoiceFile = new File(filePathString);

        try {

            //Create new http header and status
            HttpHeaders headers = new HttpHeaders();
            HttpStatus status;

            //Check if Invoice pdf exists
            if (invoiceFile.exists()) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", customerId + ".pdf");

                //Get the information out of the file Attributes
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

                // Convert FileTime to LocalDateTime
                Instant instant = attributes.creationTime().toInstant();
                LocalDateTime creationTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                //Set Time Format and format the time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd.MM.yyyy");
                String formattedCreationTime = creationTime.format(formatter);

                //Set headers to return the information
                headers.set("creationTime", formattedCreationTime);
                headers.set("filePath", filePathString);
                status = HttpStatus.OK;
            }else {
                status = HttpStatus.NOT_FOUND;
            }

            //Return information about the file
            return new ResponseEntity<>(headers, status);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Method for sending Message to the Queue
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


}



