package com.spring.restful.controller;


import com.spring.restful.model.CurrencyInterface;
import com.spring.restful.service.BankingService;
import com.spring.restful.exception.NotFoundException;
import com.spring.restful.model.Currency;

import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class MainController {

    @Autowired
    public List<BankingService> bankingServices;

    private static final Logger logger = Logger.getLogger(MainController.class);


    @RequestMapping(value = "/get-rate/{name}/{date}",  produces = { "application/json", "application/xml" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @Cacheable("currencies")
    public @ResponseBody ResponseEntity<CurrencyInterface> getExchangeRate(@PathVariable String name, @PathVariable String date ) {

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



         List<CompletableFuture<CurrencyInterface>> pojos = new ArrayList<>();

         try {
             logger.debug("Начинается цикл запросов к сервисам");
             for (BankingService service : bankingServices) {
                 if (isDate) {
                     pojos.add(service.getExchangeRate(name, formatData));
                 } else {
                     pojos.add(service.getExchangeRate(name, date));
                 }
             }
         } catch (InterruptedException e) {
             logger.error("InterruptedException");
         } catch (MalformedURLException e) {
             logger.error("MalformedURLException");
         } catch (ParserConfigurationException e) {
             logger.error("ParserConfigurationException");
         } catch (SAXException e) {
             logger.error("SAXException");
         } catch (IOException e) {
             logger.error("IOException");
         }


        CurrencyInterface minRate = null;
        logger.debug("Запускается цикл поиска лучшего курса");
         try {
             minRate = pojos.get(0).get();

             for (CompletableFuture<CurrencyInterface> pojo : pojos) {

                 if (Double.parseDouble(pojo.get().getSale()) < Double.parseDouble(minRate.getSale())) {
                        minRate = pojo.get();
                    }
             }
         } catch (ExecutionException e) {
             logger.error("ExecutionException");
         } catch (InterruptedException e) {
             logger.error("InterruptedException");
         }
        logger.info("Лучшая валюта найдена");



        logger.debug("Создается docx");
         createDocx(minRate);
        logger.info("Документ создан");
        logger.debug("Возвращается ответ");
         return new ResponseEntity<>(minRate, HttpStatus.OK);

    }

    public void createDocx(CurrencyInterface currencyInterface) {

        FileOutputStream out = null;
        XWPFDocument document = new XWPFDocument();
        Currency currency = (Currency) currencyInterface;
        logger.debug("Запускается процесс создания и заполнения docx файла");
        try {
            out = new FileOutputStream(new File(System.getProperty("user.dir").concat("/currency.docx")));

            XWPFParagraph bankParagraph = document.createParagraph();
            bankParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runBank = bankParagraph.createRun();
            runBank.setText("BANK NAME - " + currency.getBank());
            runBank.setBold(true);

            XWPFParagraph nameParagraph = document.createParagraph();
            nameParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runName = nameParagraph.createRun();
            runName.setText("CURRENCY NAME - " + currency.getExchangeName());
            runName.setBold(true);

            XWPFParagraph dateParagraph = document.createParagraph();
            dateParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runDate = dateParagraph.createRun();
            runDate.setText("DATE - " + currency.getDate());
            runDate.setBold(true);

            XWPFParagraph rateParagraph = document.createParagraph();
            rateParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runRate = rateParagraph.createRun();
            runRate.setText("SALE RATE - " + currency.getSaleRate());
            runRate.setBold(true);

            XWPFParagraph rate2Paragraph = document.createParagraph();
            rate2Paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runRate2 = rate2Paragraph.createRun();
            runRate2.setText("BUY RATE - " + currency.getPurchaseRate());
            runRate2.setBold(true);


            document.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            logger.error("Файл не найден");
        } catch (IOException e) {
            logger.error("IOException");
        }


    }

}
