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
import java.util.concurrent.CompletableFuture;


@Service
public class PrivatBankService implements BankingService {

    private static final Logger logger = Logger.getLogger(PrivatBankService.class);

    @Autowired
    @Qualifier("privatBankParser")
    public BankingParser parser;

    @Override
    @Async
    public CompletableFuture<CurrencyInterface> getExchangeRate(String name, String date) throws IOException, ParserConfigurationException, SAXException, InterruptedException {

        logger.debug("-------- Сервис PrivatBank запустился");
       CurrencyInterface currencyInterface = parser.getParse(name, date);


        logger.info("-------- Сервис PrivatBank отработал и возвращает данные");
        return CompletableFuture.completedFuture(currencyInterface);
    }
}
