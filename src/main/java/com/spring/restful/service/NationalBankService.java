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
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


@Service
public class NationalBankService extends BankingService {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy" + "MM" + "dd");

    private static final Logger logger = Logger.getLogger(NationalBankService.class);

    public final BankingParser parser;

    public NationalBankService(@Qualifier("nationalBankParser")BankingParser parser) {
        this.parser = parser;
    }

    @Value("${nationalBank.url}")
    private  String urlValue;


    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String period, boolean isDate)  {

        logger.debug("NationalBankService started");

        ArrayList<MainCurrency> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int count = getCount(period);

        for(int i = 0; i < count; i++) {
            Date date = java.sql.Date.valueOf(now.minusDays(i));
            String strDate = isDate ? formatDate(period) : simpleDateFormat.format(date);
            strDate+= "&amp;json";
            String url = urlValue + strDate;

            String response = null;

            try {
                logger.debug("Submit request URL");
                response = ReaderFromUrl.readContentFromUrl(url);
                logger.debug("Response received");
            } catch (IOException e) {
                logger.error("Failed to get response from URL", e);
                return null;
            }

            NBCurrency currency = null;

            try {
                logger.debug("Parser is being called");
                currency = (NBCurrency) parser.getParse(name, response);
            } catch (IOException e) {
                logger.error("Parsing error", e);
            }
            logger.debug("The parser has completed work");

            MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getExchangedate(),
                    currency.getCc(), currency.getRate(), currency.getPurchaseRate());

            list.add(mainCurrency);
        }

        return CompletableFuture.completedFuture(getBest(list));
    }

    private String formatDate(String period) {
        String dateStr = period;
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy" + "MM" +"dd");
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date dateCheck = null;
        try {
            dateCheck = oldDateFormat.parse(dateStr);
           String strDate = newDateFormat.format(dateCheck);

           return strDate;

        } catch (ParseException e) {
            logger.error("Parsing date error", e);
            return null;
        }
    }
}
