package com.spring.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class NBCurrency implements Currency {


    private final String bank = "NationalBank";
    private String cc;
    private String exchangedate;
    private String rate;
    private String purchaseRate = "unknown";



    @Override
    public String getSale() {
        return rate;
    }




}
