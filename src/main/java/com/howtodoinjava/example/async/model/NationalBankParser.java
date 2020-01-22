package com.howtodoinjava.example.async.model;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class NationalBankParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(NationalBankParser.class);

    @Override
    public Currency getParse(String name, String date) {

        logger.debug("Запустился парсер NationalBankParser");
        Currency nationalBankPOJO = new Currency();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy" + "MM" + "dd");
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

            if (counter == 100) {
               String strDate1 = date;
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy" + "MM" +"dd");
                SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date2 = null;
                try {
                     date2 = oldDateFormat.parse(strDate1);
                    strDate = newDateFormat.format(date2);
                    counter-=99;
                } catch (ParseException e) {

                }

            }
            BufferedReader reader = null;

            logger.debug("Запускается обращение к URL");
            try  {
                URL url = new URL("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=" + strDate + "&amp;json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                JSONArray array = new JSONArray(response.toString());
                logger.debug("Запускается цикл поиска лучшего курса - " + name);
                for (int k = 0; k < array.length(); k++) {

                    JSONObject object = (JSONObject) array.get(k);

                    if (object.get("cc").equals(name)) {

                        nationalBankPOJO.setExchangeName(object.get("cc").toString());
                        nationalBankPOJO.setSaleRate(object.get("rate").toString());
                        nationalBankPOJO.setDate(object.get("exchangedate").toString());
                        nationalBankPOJO.setBank("NationalBank");
                        logger.debug("Курс найден, добавление в список");
                        list.add(nationalBankPOJO);

                    }
                }
            } catch (IOException e) {
                logger.error("IOException");
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
