package com.spring.restful.controller;


import com.spring.restful.model.Currency;

import com.spring.restful.model.jobs.DocxCreator;
import com.spring.restful.service.BankingService;
import com.spring.restful.exception.NotFoundException;

import org.apache.log4j.Logger;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class MainController {

    private final List<BankingService> bankingServices;

    private static final Logger logger = Logger.getLogger(MainController.class);

    public MainController(List<BankingService> bankingServices) {
        this.bankingServices = bankingServices;
    }

    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");


    @RequestMapping(value = "/get-rate/{name}/{period}",  produces = { "application/json", "application/xml" }, method = RequestMethod.GET)
    @Cacheable("currencies")
    public  ResponseEntity<Currency> getExchangeRate(@PathVariable String name, @PathVariable String period ) {

        logger.debug("Получили запрос с следующими параметрами - " + name + " - " + period);
        boolean isDate = false;

        String formatDate = "";

        if (!period.equals("current") & !period.equals("week") & !period.equals("month")) {
           formatDate = checkValidity(period);
           isDate = true;
        }

        List<CompletableFuture<Currency>> pojos = new ArrayList<>();

        try {
            logger.debug("Начинается цикл запросов к сервисам");
            for (BankingService service : bankingServices) {

            Boolean isDated = isDate ? pojos.add(service.getExchangeRate(name, formatDate)) :
                        pojos.add(service.getExchangeRate(name, period));

            }
        } catch (IOException e) {
            logger.error("InterruptedException" + e);
        }


        Currency minRate = null;



        logger.debug("Запускается цикл поиска лучшего курса");
        try {
            System.out.println(pojos.size());
            minRate = pojos.get(0).get();

            for (CompletableFuture<Currency> pojo : pojos) {
                System.out.println(pojo.get());
            }
            for (CompletableFuture<Currency> pojo : pojos) {
                if (Double.parseDouble(pojo.get().getSale()) < Double.parseDouble(minRate.getSale())) {
                    minRate = pojo.get();

                }
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("ExecutionException", e);
        }
        logger.info("Лучшая валюта найдена");

        logger.debug("Создается docx");
        DocxCreator.createDocx(minRate);
        logger.info("Документ создан");
        logger.debug("Возвращается ответ");
        return new ResponseEntity<>(minRate, HttpStatus.OK);

    }


    private String checkValidity(String period) {

        Date docDate = null;
        String dateFormat = "";

        try {
            docDate = format.parse(period);
            dateFormat = format.format(docDate);
            } catch (ParseException e) {
                logger.error("Ошибка парсинга, вероятно введен неверный формат даты");
                throw new NotFoundException();
            }
            if (docDate.after(new Date())) {
                logger.error("Ошибка даты, введена дата в будущем времени");
                throw new NotFoundException();
            }

            return dateFormat;
    }


}
