package com.howtodoinjava.example.async.service;



import com.howtodoinjava.example.async.model.BankingParser;
import com.howtodoinjava.example.async.model.CurrencyInterface;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.concurrent.CompletableFuture;

@Service
public class BankUkraineService implements BankingService {

    private static final Logger logger = Logger.getLogger(BankUkraineService.class);

    @Autowired
    @Qualifier("bankUkraineParser")
    public BankingParser parser;

    @Override
    @Async
    public CompletableFuture<CurrencyInterface> getExchangeRate(String name, String date) throws MalformedURLException, SAXException, IOException, ParserConfigurationException {

        logger.debug("-------- Сервис BankUkraine запустился");
        CurrencyInterface currencyInterface = parser.getParse(name, date);
        logger.info("-------- Сервис BankUkraine отработал и возвращает данные");
                    return CompletableFuture.completedFuture(currencyInterface);
                }
            }

