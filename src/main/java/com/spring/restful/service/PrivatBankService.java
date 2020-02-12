package com.spring.restful.service;


import com.spring.restful.model.*;
import com.spring.restful.model.Parsers.BankingParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


@Service
public class PrivatBankService extends BankingService {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd." + "MM." + "yyyy");

    private static final Logger logger = Logger.getLogger(PrivatBankService.class);

    public final BankingParser parser;

    public PrivatBankService(@Qualifier("privatBankParser")BankingParser parser) {
        this.parser = parser;
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String date) throws IOException {

        ArrayList<MainCurrency> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int count = getCount(date);

        for (int i = 0; i < count; i++) {
            Date date1 = java.sql.Date.valueOf(now.minusDays(i));
            String strDate = simpleDateFormat.format(date1);

            if (count == 100) {
                strDate = date;
                count = 1;
            }

            String url = "https://api.privatbank.ua/p24api/exchange_rates?json&date="+strDate;
            logger.debug("Идет запрос к юрл");
            String response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Получили ответ юрл");
            PrivatBankCurrency currency = (PrivatBankCurrency) parser.getParse(name, date, response);

            MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(),
                    currency.getCurrency(), currency.getSaleRate(), currency.getPurchaseRate());

            list.add(mainCurrency);
        }

        return CompletableFuture.completedFuture(getBest(list));
    }



}
