package com.example.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HelloController {
    @FXML
    private Label invoiceText;
    @FXML
    private TextField customerId;

    @FXML
    protected void getInvoice() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/invoices/" + customerId.getText()))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    @FXML
    protected void generateInvoice() throws URISyntaxException, IOException, InterruptedException {
        try {
            //URL des Empfängers
            String url = "http://localhost:8080/invoices";

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
}