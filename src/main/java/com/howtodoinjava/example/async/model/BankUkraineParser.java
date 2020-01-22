package com.howtodoinjava.example.async.model;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;


@Component
public class BankUkraineParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(BankUkraineParser.class);

    @Override
    public Currency getParse(String name, String date) {

        logger.debug("Запустился парсер BankUkraineParser");

        String date1 = date;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuiler = null;
        try {
            dBuiler = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException");
        }

        Document doc = null;
        logger.debug("Запускается обращение к URL");
        try {
            doc = dBuiler.parse(new URL("http://bank-ua.com/export/currrate.xml").openStream());
        } catch (SAXException e) {
            logger.error("SAXException");
        } catch (IOException e) {
            logger.error("IOException");
        }
        doc.getDocumentElement().normalize();

        Currency bankUkrainePOJO = new Currency();
        NodeList nList = doc.getElementsByTagName("item");
        logger.debug("Начинается цикл поиска лучшего курса - " + name );
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                if (element.getElementsByTagName("char3").item(0).getTextContent().equals(name)) {

                    bankUkrainePOJO.setDate(element.getElementsByTagName("date").item(0).getTextContent());
                    bankUkrainePOJO.setExchangeName(element.getElementsByTagName("char3").item(0).getTextContent());
                    String rate = String.valueOf(Double.parseDouble(element.getElementsByTagName("rate").item(0).getTextContent()) / 100);
                    bankUkrainePOJO.setSaleRate(rate);
                    bankUkrainePOJO.setBank("bank-ua");
                    bankUkrainePOJO.setSaleRate("999999.999");    // заглушка
                    logger.debug("Валюта нашлась, возвращение результата");
                    return bankUkrainePOJO;
                }
            }
        }
        return null;
    }
}
