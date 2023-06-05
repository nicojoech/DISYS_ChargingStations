package org.example.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.ChargeInfo;
import org.example.Queue.Publisher;
import org.example.Queue.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DataCollectionService {

    private final String inQueueDispatcher;
    private final String inQueueCollector;

    public DataCollectionService(String inQueueDispatcher, String inQueueCollector){
        this.inQueueDispatcher = inQueueDispatcher;
        this.inQueueCollector = inQueueCollector;
    }
    public void work() throws IOException, TimeoutException {
        while (true) {
            //String input = Subscriber.receive(inQueueDispatcher);
            System.out.println("DataGatherJob received");
            gatherData("3");
            break;
        }
    }

    public void gatherData(String input) throws IOException, TimeoutException {

        int messages = Integer.parseInt(input);
        int doneMessages = 0;

        List<ChargeInfo> listToPublish = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        while (doneMessages < messages){
            //String jsonInput = Subscriber.receive(inQueueCollector);
            String jsonInput = "[{\"id\":1,\"kwh\":\"50.4\",\"customer_id\":2},{\"id\":2,\"kwh\":\"10.8\",\"customer_id\":1},{\"id\":3,\"kwh\":\"13.7\",\"customer_id\":2}]";
            List<ChargeInfo> chargeInfoList = mapper.readValue(jsonInput, new TypeReference<>() {});
            listToPublish.addAll(chargeInfoList);

            doneMessages++;
        }

        String output = mapper.writeValueAsString(listToPublish);
        Publisher.send("completeDataQueue", output);


        //System.out.println(output);

        /*for (int i = 0; i < listToPublish.size(); i++){
            System.out.println(listToPublish.get(i).getKwh());
        }*/

    }
}
