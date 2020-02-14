package com.spring.restful.controller;

import com.spring.restful.model.Currency;
import com.spring.restful.utils.DocxCreator;
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


    @RequestMapping(value = "/get-rate/{name}/{period}", produces = {"application/json", "application/xml"}, method = RequestMethod.GET)
    @Cacheable("currencies")
    public ResponseEntity<Currency> getExchangeRate(@PathVariable String name, @PathVariable String period) {

        logger.debug("Received a request with the following parameters - " + name + " - " + period);

        boolean isDate = false;
        String formatDate = "";

        logger.debug("Attempt to format date");
        if (!period.equals("current") & !period.equals("week") & !period.equals("month")) {
            formatDate = formatDate(period);
            isDate = true;
            logger.debug("Date formatting was successful");
        }

        List<CompletableFuture<Currency>> pojos = new ArrayList<>();

        try {
            logger.debug("The service request cycle starts");
            for (BankingService service : bankingServices) {

                Boolean isDated = isDate ? pojos.add(service.getExchangeRate(name, formatDate)) :
                        pojos.add(service.getExchangeRate(name, period));
            }

        } catch (IOException e) {
            logger.error("Error due to attempt to start method getExchangeRate", e);
        }


        Currency minRate = null;


        logger.debug("The best exchange rate search cycle starts");
        try {
            minRate = pojos.get(0).get();

            for (CompletableFuture<Currency> pojo : pojos) {
                if (Double.parseDouble(pojo.get().getSale()) < Double.parseDouble(minRate.getSale())) {
                    minRate = pojo.get();

                }
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error due to attempt to start method CompletableFuture.get()", e);
        }
        logger.info("The best exchange rate found");

        logger.debug("Docx is being created");
        DocxCreator.createDocx(minRate);
        logger.info("Docx created");

        logger.debug("Returning response");
        return new ResponseEntity<>(minRate, HttpStatus.OK);

    }


    private String formatDate(String period) {

        Date docDate = null;
        String dateFormat = "";

        try {
            docDate = format.parse(period);
            dateFormat = format.format(docDate);
        } catch (ParseException e) {
            logger.error("Parsing error, probably entered wrong date format", e);
            throw new NotFoundException();
        }
        if (docDate.after(new Date())) {
            logger.error("Date error, date entered in future time");
            throw new NotFoundException();
        }

        return dateFormat;
    }


}
