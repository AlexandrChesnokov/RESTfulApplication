package com.spring.restful.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class PrivatBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(PrivatBankParser.class);


    @Override
    public PrivatBankCurrency getParse(String name, String date, String response) {


        ObjectMapper mapper = new ObjectMapper();


        PrivatBankCurrency privatBankCurrency = null;
        try {
            privatBankCurrency = mapper.readValue
                    (response, PrivatBankCurrency.class);
        } catch (IOException e) {

        }

        PrivatBankCurrency[] currencies = privatBankCurrency.getExchangeRate();

        for (PrivatBankCurrency currency : currencies) {
            if (currency.getCurrency() != null) {
                if (currency.getCurrency().equals(name)) {
                    currency.setDate(privatBankCurrency.getDate());
                    return currency;

                }
            }
        }

        return null;

    }
}
