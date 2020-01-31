package com.spring.restful.model;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


@Component
public class MonoBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(MonoBankParser.class);

    @Override
    public Currency getParse(String name, String date) {

        logger.debug("Запустился парсер MonoBankParser");
        Currency monoBankPOJO = new Currency();
        if (date.equals("current")) {
        RestTemplate restTemplate = new RestTemplate();
            logger.debug("Запускается обращение к URL");
        Currency[] rates = restTemplate.getForObject("https://api.monobank.ua/bank/currency", Currency[].class);

        assert rates != null;

            logger.debug("Запускается цикл поиска лучшего курса - " + name);
        for (Currency rate : rates) {
            String name1 = "";
            Set<java.util.Currency> currencies = java.util.Currency.getAvailableCurrencies();
            for (java.util.Currency currency : currencies) {
                if (currency.getNumericCode() == Integer.parseInt(rate.getExchangeName())) {
                    name1 = currency.toString();
                }
            }

            if (name1.equals(name)) {

                monoBankPOJO.setExchangeName(name1);

                Date date1 = new Date(Long.parseLong(rate.getDate()) * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("dd." + "MM." + "yyyy");
                String formattedDate = sdf.format(date1);
                monoBankPOJO.setDate(formattedDate);

                monoBankPOJO.setSaleRate(rate.getSaleRate());
                monoBankPOJO.setPurchaseRate(rate.getPurchaseRate());
                monoBankPOJO.setBank("MonoBank");

                logger.info("Курс найден, возвращение результата");
                return monoBankPOJO;
            }
            }

        }
        monoBankPOJO.setSaleRate("999999.9999");    // заглушка
        return monoBankPOJO;
    }
}
