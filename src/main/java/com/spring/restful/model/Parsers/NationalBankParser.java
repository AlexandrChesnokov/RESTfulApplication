package com.spring.restful.model.Parsers;

import com.spring.restful.model.MainCurrency;
import com.spring.restful.model.NBCurrency;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class NationalBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(NationalBankParser.class);



    @Override
    public NBCurrency getParse(String name, String date, String response) {

        logger.debug("Запустился парсер NationalBankParser");
        NBCurrency nationalBankPOJO = new NBCurrency();

        List<MainCurrency> list = new ArrayList<>();
        LocalDate localDateTime = LocalDate.now();

        logger.debug("Запускается обращение к URL");



        JSONArray array = new JSONArray(response);
        logger.debug("Запускается цикл поиска лучшего курса - " + name);
        for (int k = 0; k < array.length(); k++) {

            JSONObject object = (JSONObject) array.get(k);

            if (object.get("cc").equals(name)) {

                nationalBankPOJO.setCc(object.get("cc").toString());
                nationalBankPOJO.setRate(object.get("rate").toString());
                nationalBankPOJO.setExchangedate(object.get("exchangedate").toString());
                logger.debug("Курс найден, добавление в список");
                return nationalBankPOJO;

            }
        }


        return null;

    }




}
