package com.spring.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class MonoBankCurrency implements Currency {


    private final String bank = "MonoBank";
    private String currencyCodeA;
    private String date;
    private String rateSell;
    private String rateBuy = "unknown";





    @Override
    public String getSale() {
        return rateSell;
    }
}
