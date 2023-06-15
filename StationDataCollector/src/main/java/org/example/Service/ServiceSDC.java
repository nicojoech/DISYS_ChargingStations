package org.example.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import org.example.Model.ChargeInfoSDC;
import org.example.Model.StationDB;
//import org.example.Models.ChargeInfo;
import org.example.Queue.PublisherSDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceSDC {

    public void gatherData(String portAndCustomerId) throws DocumentException {
        String[] parts = portAndCustomerId.split(",");
        String port = parts[0];
        String customerId = parts[1];
        getDataFromStation(port, customerId);
    }

    private void getDataFromStation(String port, String customerId) {
        ChargeInfoSDC info = new ChargeInfoSDC();
        List<ChargeInfoSDC> chargeInfoList = new ArrayList<>();
        StationDB stationDB = new StationDB();
        stationDB.setPORT(Integer.parseInt(port));
        String query = "SELECT id, kwh, customer_id FROM charge WHERE customer_id = ?";
        try(Connection conn = stationDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                info.setId(rs.getInt("id"));
                info.setKwh(String.valueOf(rs.getFloat("kwh")));
                info.setCustomer_id(rs.getInt("customer_id"));
                chargeInfoList.add(info);

                info = new ChargeInfoSDC();

            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        convertToJson(chargeInfoList);

    }

    private void convertToJson(List<ChargeInfoSDC> chargeInfoList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(chargeInfoList);
            System.out.println("JSON: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        PublisherSDC.send("dataGatheringQueue", json);
    }


}
