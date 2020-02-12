package com.spring.restful.service;



import com.spring.restful.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class MonoBankService extends BankingService {

    private static final Logger logger = Logger.getLogger(MonoBankService.class);
    public final BankingParser parser;

    public MonoBankService(@Qualifier("monoBankParser")BankingParser parser) {
        this.parser = parser;
    }

    @Override
    @Async
    public CompletableFuture<Currency> getExchangeRate(String name, String date) throws  IOException {

        String url = "https://api.monobank.ua/bank/currency";
        logger.debug("Идет запрос к юрл");
        String response = ReaderFromUrl.readContentFromUrl(url);

        logger.debug("Получили ответ юрл");
        MonoBankCurrency currency = (MonoBankCurrency) parser.getParse(name, date, response);

        MainCurrency mainCurrency = new MainCurrency(currency.getBank(), currency.getDate(),
                currency.getCurrencyCodeA(), currency.getRateSell(), currency.getRateBuy());

        return CompletableFuture.completedFuture(mainCurrency);
    }
}



