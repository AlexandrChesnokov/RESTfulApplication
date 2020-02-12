package com.spring.restful.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class MainCurrency implements Currency {

    private String bank;
    @JsonAlias("exchangedate")
    private String date;      //дата
    @JsonAlias({"currency", "cc", "char3", "currencyCodeA"})
    private String exchangeName;
    @JsonAlias({"rate", "rateSell"})
    private String saleRate;
    @JsonAlias("rateBuy")
    private String purchaseRate = "unknown";



    @Override
    public String getSale() {
        return saleRate;
    }

    public MainCurrency(String bank, String date, String exchangeName, String saleRate, String purchaseRate) {
        this.bank = bank;
        this.date = date;
        this.exchangeName = exchangeName;
        this.saleRate = saleRate;
        this.purchaseRate = purchaseRate;
    }
}
