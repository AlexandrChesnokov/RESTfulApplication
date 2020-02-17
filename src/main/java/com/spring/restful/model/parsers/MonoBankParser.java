package com.spring.restful.model.parsers;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.restful.model.MonoBankCurrency;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


@Component
public class MonoBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(MonoBankParser.class);

    @Override
    public MonoBankCurrency getParse(String name, String response)  {

        logger.debug("Parser started - MonoBankParser");
        MonoBankCurrency monoBankPOJO = new MonoBankCurrency();

            ObjectMapper mapper = new ObjectMapper();
            MonoBankCurrency[] rates = new MonoBankCurrency[0];
            try {
                logger.debug("Parsing response in progress");
                rates = mapper.readValue(response, MonoBankCurrency[].class);
            } catch (JsonProcessingException e) {
                logger.error("Error parsing response", e);
            }


            logger.debug("Currency search cycle starts - " + name );
            for (MonoBankCurrency rate : rates) {
                String currencyName = "";
                Set<java.util.Currency> currencies = java.util.Currency.getAvailableCurrencies();
                for (java.util.Currency currency : currencies) {
                    if (currency.getNumericCode() == Integer.parseInt(rate.getCurrencyCodeA())) {
                        currencyName = currency.toString();
                    }
                }

                if (currencyName.equals(name)) {

                    monoBankPOJO.setCurrencyCodeA(currencyName);

                    Date dateFormat = new Date(Long.parseLong(rate.getDate()) * 1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd." + "MM." + "yyyy");
                    String formattedDate = sdf.format(dateFormat);
                    monoBankPOJO.setDate(formattedDate);
                    monoBankPOJO.setRateSell(rate.getRateSell());
                    monoBankPOJO.setRateBuy(rate.getRateBuy());


                    logger.debug("Currency found, returning result");
                    return monoBankPOJO;
                }
            }


        monoBankPOJO.setRateSell("999999.9999");    // заглушка
        return monoBankPOJO;
    }
}
