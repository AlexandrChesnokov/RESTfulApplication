package com.howtodoinjava.example.async.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class PrivatBankParser implements BankingParser{

    private static final Logger logger = Logger.getLogger(PrivatBankParser.class);


    @Override
    public Currency getParse(String name, String date)  {

        logger.debug("Запустился парсер NationalBankParser");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd." + "MM." + "yyyy");
        List<Currency> list = new ArrayList<>();
        LocalDate localDateTime = LocalDate.now();

        int counter = 1;

        try {
            if (date.equals("current")) {
                counter = 1;
            }
            else if (date.equals("week")){
                counter = 7;
            } else if (date.equals("month")) {
                counter = 31;
            } else {
                counter = 100;
            }
        } catch (NullPointerException e) {
            counter = 1;
        }

        for (int i = 0; i < counter; i++) {
            Date date1 = java.sql.Date.valueOf(localDateTime.minusDays(i));
            String strDate = simpleDateFormat.format(date1);
            ObjectMapper mapper = new ObjectMapper();

            if (counter == 100) {
                strDate = date;
                counter-=99;
            }

            URL url = null;
            logger.debug("Запускается обращение к URL");
            try {
                url = new URL("https://api.privatbank.ua/p24api/exchange_rates?json&date="+strDate);
            } catch (MalformedURLException e) {
                logger.error("MalformedURLException");
            }
            Currency privatBankPOJOs = null;
            try {
                privatBankPOJOs = mapper.readValue
                            (url, Currency.class);
            } catch (IOException e) {
                logger.error("Ошибка парсинга URL по адресу - " + url);
            }

            Currency[] privatBankPOJO1 = privatBankPOJOs.getExchangeRate();

            logger.debug("Запускается цикл поиска лучшего курса - " + name);
            for (Currency privatBankPOJOx : privatBankPOJO1) {

                try {

                    if (privatBankPOJOx.getExchangeName().equals(name)) {
                        privatBankPOJOx.setDate(privatBankPOJOs.getDate());
                        privatBankPOJOx.setBank("PrivatBank");
                        logger.debug("Курс найден, добавление в список");
                        list.add(privatBankPOJOx);
                    }
                } catch (NullPointerException e)  {
                    logger.error("NullPointerException -> пропуск безымянной валюты");
                }
            }
        }

        Currency minRate = list.get(0);
        logger.debug("Запускается цикл поиска лучшего курса из списка");
        for (Currency rate : list) {
            if (Double.parseDouble(rate.getSaleRate()) < Double.parseDouble(minRate.getSaleRate())) {
                minRate = rate;
            }
        }

        logger.info("Курс найден, возвращение результата");
        return minRate;

    }

}
