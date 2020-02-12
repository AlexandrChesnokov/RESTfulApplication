package com.spring.restful.service;


import com.spring.restful.model.*;
import com.spring.restful.model.Parsers.BankingParser;
import com.spring.restful.model.jobs.ReaderFromUrl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String date) throws IOException {

        ArrayList<MainCurrency> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int count = getCount(date);

        for(int i = 0; i < count; i++) {
            Date date1 = java.sql.Date.valueOf(now.minusDays(i));
            String strDate = simpleDateFormat.format(date1);

            if (count == 100) {
                String strDate1 = date;
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy" + "MM" +"dd");
                SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date2 = null;
                try {
                    date2 = oldDateFormat.parse(strDate1);
                    strDate = newDateFormat.format(date2);
                    count = 1;
                } catch (ParseException e) {

                }
            }

            String url = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=" + strDate + "&amp;json";
            logger.debug("Идет запрос к юрл");
            String response = ReaderFromUrl.readContentFromUrl(url);
            logger.debug("Получили ответ юрл");
            NBCurrency currency = (NBCurrency) parser.getParse(name, date, response);

            MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getExchangedate(),
                    currency.getCc(), currency.getRate(), currency.getPurchaseRate());

            list.add(mainCurrency);
        }

        return CompletableFuture.completedFuture(getBest(list));
    }
}
