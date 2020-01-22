package com.howtodoinjava.example.async.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class Currency implements CurrencyInterface {

    private String bank;
    @JsonAlias("exchangedate")
    private String date;      //дата
    @JsonAlias({"currency", "cc", "char3", "currencyCodeA"})
    private String exchangeName;
    @JsonAlias({"rate", "rateSell"})
    private String saleRate;
    @JsonAlias("rateBuy")
    private String purchaseRate = "unknown";

    private Currency[] exchangeRate;

    @Override
    public String getSale() {
        return saleRate;
    }
}
