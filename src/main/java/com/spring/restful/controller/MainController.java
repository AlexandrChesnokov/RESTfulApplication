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


    @RequestMapping(value = "/get-rate/{name}/{date}",  produces = { "application/json", "application/xml" }, method = RequestMethod.GET)
    @Cacheable("currencies")
    public  ResponseEntity<Currency> getExchangeRate(@PathVariable String name, @PathVariable String date ) {

        logger.debug("Получили запрос с следующими параметрами - " + name + " - " + date);
        boolean isDate = false;
        Date docDate = null;
        String formatData = "";
        if (!date.equals("current") & !date.equals("week") & !date.equals("month")) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

            try {
                docDate = format.parse(date);
                isDate = true;
                formatData = format.format(docDate);
            } catch (ParseException e) {
                logger.error("Ошибка парсинга, вероятно введен неверный формат даты");
                throw new NotFoundException();
            }
            if (docDate.after(new Date())) {
                logger.error("Ошибка даты, введена дата в будущем времени");
                throw new NotFoundException();
            }
        }

        List<CompletableFuture<Currency>> pojos = new ArrayList<>();

        try {
            logger.debug("Начинается цикл запросов к сервисам");
            for (BankingService service : bankingServices) {
                if (isDate) {
                    pojos.add(service.getExchangeRate(name, formatData));
                } else {
                    pojos.add(service.getExchangeRate(name, date));
                }
            }
        } catch (IOException e) {
            logger.error("InterruptedException" + e);
        }


        Currency minRate = null;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("Запускается цикл поиска лучшего курса");
        try {

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



}
