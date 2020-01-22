package com.howtodoinjava.example.async.model;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface BankingParser {

    CurrencyInterface getParse(String name, String date) throws IOException, SAXException, MalformedURLException, ParserConfigurationException;
}
