package com.spring.restful.service;



import com.spring.restful.model.*;
import com.spring.restful.model.Parsers.BankingParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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



    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String date) throws  IOException {

        logger.debug("BankUkraineService start");

        String url = "http://bank-ua.com/export/currrate.xml";

        logger.debug("Идет запрос к юрл");
        String response = ReaderFromUrl.readContentFromUrl(url);
        logger.debug("Получили ответ юрл");

        logger.debug("Идет запрос к парсеру");
        BankUkraineCurrency currency = (BankUkraineCurrency) parser.getParse(name, date, response);
        logger.debug("Получили ответ парсера");

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(), currency.getChar3(),
                currency.getRate(), currency.getPurchaseRate());


        return CompletableFuture.completedFuture(mainCurrency);

    }

}

