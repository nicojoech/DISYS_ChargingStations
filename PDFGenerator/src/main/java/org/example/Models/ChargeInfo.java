package org.example.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChargeInfo {

    @JsonProperty("id")
    private int id;
    @JsonProperty("kwh")
    private String kwh;
    @JsonProperty("customer_id")
    private int customer_id;

    public ChargeInfo(){}

    public ChargeInfo(int id, String kwh, int customer_id){
        this.id = id;
        this.kwh = kwh;
        this.customer_id = customer_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKwh() {
        return kwh;
    }

    public void setKwh(String kwh) {
        this.kwh = kwh;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
}
