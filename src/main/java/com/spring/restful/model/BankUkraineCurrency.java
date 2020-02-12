package com.spring.restful.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class BankUkraineCurrency implements Currency {

    private final String bank = "BankUkraine";
    private String char3;
    private String date;
    private String rate;
    private String purchaseRate = "unknown";


    @Override
    public String getSale() {
        return rate;
    }
}
