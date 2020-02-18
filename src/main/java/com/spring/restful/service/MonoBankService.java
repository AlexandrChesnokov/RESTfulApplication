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
public class MonoBankService extends BankingService {

    private static final Logger logger = Logger.getLogger(MonoBankService.class);
    public final BankingParser parser;

    @Value("${monoBank.url}")
    private String url;

    public MonoBankService(@Qualifier("monoBankParser")BankingParser parser) {
        this.parser = parser;
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String period)  {

        logger.debug("MonoBankService started");

        String response = null;
        try {
            logger.debug("Submit request URL");
            response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Response received");
        } catch (IOException e) {
            logger.error("Failed to get response from URL", e);
            return null;
        }


        MonoBankCurrency currency = null;
        try {
            logger.debug("Parser is being called");
            currency = (MonoBankCurrency) parser.getParse(name, response);
        } catch (IOException e) {
            logger.error("Parsing error", e);
        }
        logger.debug("The parser has completed work");

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(),
                currency.getCurrencyCodeA(), currency.getRateSell(), currency.getRateBuy());

        return CompletableFuture.completedFuture(mainCurrency);
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRateByDate(String name, String date) {

        String response = null;
        try {
            logger.debug("Submit request URL");
            response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Response received");
        } catch (IOException e) {
            logger.error("Failed to get response from URL", e);
            return null;
        }


        MonoBankCurrency currency = null;
        try {
            logger.debug("Parser is being called");
            currency = (MonoBankCurrency) parser.getParse(name, response);
        } catch (IOException e) {
            logger.error("Parsing error", e);
        }
        logger.debug("The parser has completed work");

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(),
                currency.getCurrencyCodeA(), currency.getRateSell(), currency.getRateBuy());

        return CompletableFuture.completedFuture(mainCurrency);
    }
}



