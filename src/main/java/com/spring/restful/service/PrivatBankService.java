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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class PrivatBankService extends BankingService {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd." + "MM." + "yyyy");

    private static final Logger logger = Logger.getLogger(PrivatBankService.class);

    public final BankingParser parser;


    @Value("${privatBank.url}")
    private String urlValue;


    public PrivatBankService(@Qualifier("privatBankParser")BankingParser parser) {
        this.parser = parser;
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String period)  {

        logger.debug("PrivatBankService started");

        ArrayList<MainCurrency> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int count = getCount(period);

        for (int i = 0; i < count; i++) {
            Date date = java.sql.Date.valueOf(now.minusDays(i));
            String strDate = simpleDateFormat.format(date);


            MainCurrency mainCurrency = null;
            try {
                mainCurrency = (MainCurrency) getExchangeRateByDate(name, strDate).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error due to attempt to start method CompletableFuture.get()", e);
            }


            list.add(mainCurrency);
        }

        return CompletableFuture.completedFuture(getBest(list));
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRateByDate(String name, String date) {

        String response;


        String url = urlValue + date;



        try {
            logger.debug("Submit request URL");
            response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Response received");
        } catch (IOException e) {
            logger.error("Failed to get response from URL", e);
            return null;
        }

        PrivatBankCurrency currency = null;
        try {
            logger.debug("Parser is being called");
            currency = (PrivatBankCurrency) parser.getParse(name, response);
        } catch (IOException e) {
            logger.error("Parsing error", e);
        }
        logger.debug("The parser has completed work");

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(),
                currency.getCurrency(), currency.getSaleRate(), currency.getPurchaseRate());

        return CompletableFuture.completedFuture(mainCurrency);

    }
}
