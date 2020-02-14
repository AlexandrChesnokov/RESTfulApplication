package com.spring.restful.service;



import com.spring.restful.model.*;
import com.spring.restful.model.parsers.BankingParser;
import com.spring.restful.utils.ReaderFromUrl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.io.IOException;

import java.util.concurrent.CompletableFuture;

@Service
public class BankUkraineService extends BankingService {

    private static final Logger logger = Logger.getLogger(BankUkraineService.class);

    public final BankingParser parser;

    public BankUkraineService(@Qualifier("bankUkraineParser")BankingParser parser) {
        this.parser = parser;
    }

    @Value("${bankUkraine.url}")
    private String url;


    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String period) {

        logger.debug("BankUkraineService started");

        String response = null;
        try {
            logger.debug("Submit request URL");
            response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Response received");
        } catch (IOException e) {
            logger.error("Failed to get response from URL", e);
            return null;

        }


        BankUkraineCurrency currency = null;
        try {
            logger.debug("Parser is being called");
            currency = (BankUkraineCurrency) parser.getParse(name, period, response);
        } catch (IOException e) {
            logger.error("Parsing error", e);
        }
        logger.debug("The parser has completed work");

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(), currency.getChar3(),
                currency.getRate(), currency.getPurchaseRate());


        return CompletableFuture.completedFuture(mainCurrency);

    }

}

