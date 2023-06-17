package com.example.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {

    //Textfield and Textoutput Field in the frontend
    @FXML
    private Label invoiceText;
    @FXML
    private TextField customerId;

    //Method for starting the data gathering Job with REST POST
    @FXML
    protected void generateInvoice() throws URISyntaxException, IOException, InterruptedException {
        try {
            //If there are letters in the input, break
            if(customerId.getText().matches(".*\\D.*")){
                invoiceText.setText("Only Numbers allowed");
                customerId.setText("");
                return;
            }

            //URL from springboot Application
            String url = "http://localhost:8081/invoices";

            // Create a Connection
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // POST-Method set
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Set Customer-ID as JSON-Format
            String requestBody = "{\"customerId\":\"" + customerId.getText() + "\"}";

            // activate Output-Mode of the connection
            connection.setDoOutput(true);

            // Send the Request Body
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // read answer from Springboot
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print response and set in the field
            System.out.println("Response Code: " + responseCode);

            System.out.println("Generating Invoice for Customer Id: " + response.toString());
            invoiceText.setText("Generating Invoice for Customer Id: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Timeline timeline;

    //Method for getting the generated invoice, if available, with GET
    @FXML
    protected void getInvoice() throws URISyntaxException, IOException, InterruptedException {

        //If there are letters in the input, break
        if(customerId.getText().matches(".*\\D.*")){
            invoiceText.setText("Only Numbers allowed");
            customerId.setText("");
            return;
        }

        //Creating new Timeline for checking every 2 seconds, if pdf is available
       timeline = new Timeline(
                new KeyFrame(Duration.seconds(2) , event -> {

                    try {
                        //start Method to handle get the Invoice
                        handleGetInvoice();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Does it as long as there is no pdf
        timeline.play();

    }

    //Method to get the Invoice with GET
    protected void handleGetInvoice() throws URISyntaxException, IOException, InterruptedException{
        //Build new GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/invoices/" + customerId.getText()))
                .GET()
                .build();

        //save Response
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        //Get the creation time and file path and status code information out of the response header
        String creationTime = response.headers().firstValue("creationTime").orElse(null);
        String filePath = response.headers().firstValue("filePath").orElse(null);
        int status = response.statusCode();
        System.out.println("Returnstatus: " + status);
        System.out.println(creationTime);
        System.out.println(filePath);

        //If status code is 200 (Invoice available), show path and time and stop timeline, else show status code
        if (status == 200){
            invoiceText.setText("Path: \n" + filePath + "\nCreated at: \n" + creationTime + "\nReturn status: \n" + status);
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filePath);
            timeline.stop();
        }else {
            invoiceText.setText("Return status: " + status);
        }
    }

}