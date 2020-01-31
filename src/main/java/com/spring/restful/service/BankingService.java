package com.spring.restful.service;


import com.spring.restful.model.CurrencyInterface;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;


@Service
public interface BankingService {
       @Async
       CompletableFuture<CurrencyInterface> getExchangeRate(String name, String date) throws IOException, SAXException, ParserConfigurationException, MalformedURLException, InterruptedException;


}
