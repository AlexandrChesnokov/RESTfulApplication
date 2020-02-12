package com.spring.restful.model.Parsers;


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
    public MonoBankCurrency getParse(String name, String period, String response) throws JsonProcessingException {


        MonoBankCurrency monoBankPOJO = new MonoBankCurrency();

        if (period.equals("current")) {

            ObjectMapper mapper = new ObjectMapper();
            MonoBankCurrency[] rates = mapper.readValue(response, MonoBankCurrency[].class);

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


                    logger.info("Курс найден, возвращение результата");
                    return monoBankPOJO;
                }
            }

        }
        monoBankPOJO.setRateSell("999999.9999");    // заглушка
        return monoBankPOJO;
    }
}
