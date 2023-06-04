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
    @FXML
    private Label invoiceText;
    @FXML
    private TextField customerId;

    //Method for starting the gathering Job with POST
    @FXML
    protected void generateInvoice() throws URISyntaxException, IOException, InterruptedException {
        try {
            //URL des Empfängers
            String url = "http://localhost:8081/invoices";

            // Erstelle eine Verbindung
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // POST-Methode
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Kunden-ID im JSON-Format übergeben
            String requestBody = "{\"customerId\":\"" + customerId.getText() + "\"}";

            // Aktiviere den Output-Modus der Verbindung
            connection.setDoOutput(true);

            // Sende den Request Body
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Antwort von Springboot
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Antwort ausgeben
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

       timeline = new Timeline(
                new KeyFrame(Duration.seconds(5) , event -> {

                    try {
                        handleGetInvoice();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Wiederhole unendlich oft
        timeline.play();

    }

    protected void handleGetInvoice() throws URISyntaxException, IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/invoices/" + customerId.getText()))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String creationTime = response.headers().firstValue("creationTime").orElse(null);
        String filePath = response.headers().firstValue("filePath").orElse(null);
        int status = response.statusCode();
        System.out.println("Returnstatus: " + status);
        System.out.println(creationTime);
        System.out.println(filePath);

        if (status == 200){
            invoiceText.setText("Path: \n" + filePath + "\nCreated at: \n" + creationTime + "\nReturn status: \n" + status);
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filePath);
            timeline.stop();
        }else {
            invoiceText.setText("Return status: " + status);
        }
    }

}