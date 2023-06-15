package org.example.Service;

import com.itextpdf.text.DocumentException;
import org.example.Model.StationsDB;
import org.example.Queue.SubscriberDCD;
import org.example.Queue.PublisherDCD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceDCD {

    public void gatherData(String customer) throws DocumentException {
        int numberOfDatabases = getNumberOfDatabases();
        int[] portNumbers = getDBPortNumbers(numberOfDatabases);
        PublisherDCD.send("messageInfoQueue", String.valueOf(numberOfDatabases));
        sendToSDC(portNumbers, customer);
    }

    private void sendToSDC(int[] portNumbers, String customer) {
        String message;
        for (int portNumber : portNumbers) {
            message = String.valueOf(portNumber) + "," + customer;
            PublisherDCD.send("dataToCollect", message);
        }
    }

    private int[] getDBPortNumbers(int numberOfDatabases) {
        int[] portNumbers = new int[numberOfDatabases];
        String url;
        int index = 0;
        try (Connection connection = StationsDB.getConnection()) {
            String query = "SELECT DISTINCT db_url FROM station";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                url = resultSet.getString("db_url");
                url = url.replace("localhost:", "");
                portNumbers[index] = Integer.parseInt(url);
                index++;
            }
        } catch (SQLException e) {
            // Handle any potential exceptions here
            e.printStackTrace();
        }
        return portNumbers;
    }

    private int getNumberOfDatabases() {
        int numberOfDB = 0 ;
        try (Connection connection = StationsDB.getConnection()) {
            String query = "SELECT COUNT(DISTINCT db_url) AS num_databases FROM station";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                numberOfDB = resultSet.getInt("num_databases");
            }
        } catch (SQLException e) {
            // Handle any potential exceptions here
            e.printStackTrace();
        }
        return numberOfDB;
    }


}
