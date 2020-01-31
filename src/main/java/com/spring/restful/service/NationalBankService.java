package com.spring.restful.service;


import com.spring.restful.model.BankingParser;
import com.spring.restful.model.CurrencyInterface;
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
public class NationalBankService implements BankingService {

    private static final Logger logger = Logger.getLogger(NationalBankService.class);

    @Autowired
    @Qualifier("nationalBankParser")
    public BankingParser parser;

    @Override
    @Async
    public CompletableFuture<CurrencyInterface> getExchangeRate(String name, String date) throws ParserConfigurationException, SAXException, IOException {
        CurrencyInterface bankingPOJO = null;
        logger.debug("-------- Сервис NationalBank запустился");
       bankingPOJO =  parser.getParse(name, date);

        logger.info("-------- Сервис NationalBank отработал и возвращает данные");
        return CompletableFuture.completedFuture(bankingPOJO);
    }
}
