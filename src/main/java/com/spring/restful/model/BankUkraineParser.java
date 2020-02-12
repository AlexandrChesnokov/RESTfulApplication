package com.spring.restful.model;


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
import java.io.StringBufferInputStream;


@Component
public class BankUkraineParser implements BankingParser {

    private static final Logger logger = Logger.getLogger(BankUkraineParser.class);

    @Override
    public BankUkraineCurrency getParse(String name, String period, String response) throws IOException {

        logger.debug("Запустился парсер BankUkraineParser");

        String date1 = period;
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
            doc = dBuiler.parse(new StringBufferInputStream(response));
        } catch (SAXException e) {
            logger.error("SAXException");
        }
        doc.getDocumentElement().normalize();

        BankUkraineCurrency bankUkrainePOJO = new BankUkraineCurrency();
        NodeList nList = doc.getElementsByTagName("item");
        logger.debug("Начинается цикл поиска лучшего курса - " + name );
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                if (element.getElementsByTagName("char3").item(0).getTextContent().equals(name)) {

                    bankUkrainePOJO.setDate(element.getElementsByTagName("date").item(0).getTextContent());
                    bankUkrainePOJO.setChar3(element.getElementsByTagName("char3").item(0).getTextContent());
                    String rate = String.valueOf(Double.parseDouble(element.getElementsByTagName("rate").item(0).getTextContent()) / 100);
                    bankUkrainePOJO.setRate(rate);
                    bankUkrainePOJO.setRate("999999.999");    // заглушка
                    logger.debug("Валюта нашлась, возвращение результата");
                    return bankUkrainePOJO;
                }
            }
        }
        return null;
    }
}
