package com.spring.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@JsonIgnoreProperties( ignoreUnknown = true)
public class PrivatBankCurrency implements Currency {

   private final String bank = "PrivatBank";
   private String currency;
   private String saleRate;
   private String purchaseRate = "unknown";
   private String date;

   private PrivatBankCurrency[] exchangeRate;

    @Override
    public String getSale() {
        return saleRate;
    }
}
