package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.ChargeInfo;
import org.example.Queue.Publisher;
import org.example.Queue.SubscriberData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DataCollectionService {

    private List<ChargeInfo> chargeInfoList;
    private ObjectMapper mapper;

    public DataCollectionService(List<ChargeInfo> list, ObjectMapper mapper){
        this.chargeInfoList = list;
        this.mapper = mapper;
    }

    public void gatherData(String input) throws IOException, TimeoutException {
        SubscriberData.receive(Integer.parseInt(input), this);
    }

    public void writeMsgToList(String msg) throws JsonProcessingException {
        List<ChargeInfo> chargeInfoOneMessage = mapper.readValue(msg, new TypeReference<>() {});
        chargeInfoList.addAll(chargeInfoOneMessage);

    }

    public void formatAndPublish() throws JsonProcessingException {
        String output = mapper.writeValueAsString(chargeInfoList);
        Publisher.send("completeDataQueue", output);
        chargeInfoList.clear();
    }
}
