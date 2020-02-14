package com.spring.restful.model.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.restful.model.PrivatBankCurrency;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class PrivatBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(PrivatBankParser.class);


    @Override
    public PrivatBankCurrency getParse(String name, String period, String response) {

        logger.debug("Parser started - PrivatBankParser");
        ObjectMapper mapper = new ObjectMapper();


        PrivatBankCurrency privatBankCurrency = null;
        try {
            logger.debug("Parsing response in progress");
            privatBankCurrency = mapper.readValue
                    (response, PrivatBankCurrency.class);
        } catch (IOException e) {
            logger.error("Error parsing response", e);
        }

        PrivatBankCurrency[] currencies = privatBankCurrency.getExchangeRate();
        logger.debug("Currency search cycle starts - " + name );
        for (PrivatBankCurrency currency : currencies) {
            if (currency.getCurrency() != null) {
                if (currency.getCurrency().equals(name)) {
                    currency.setDate(privatBankCurrency.getDate());
                    logger.debug("Currency found, returning result");
                    return currency;

                }
            }
        }

        return null;

    }
}
