package com.spring.restful.utils;

import com.spring.restful.model.Currency;
import com.spring.restful.model.MainCurrency;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocxCreator {

    private static final Logger logger = Logger.getLogger(DocxCreator.class);


    public static void createDocx(Currency currencyInterface) {


        XWPFDocument document = new XWPFDocument();
        MainCurrency currency = (MainCurrency) currencyInterface;

        try (FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir").concat("/currency.docx")))) {

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

        } catch (IOException e) {
            logger.error("Error creating document", e);
        }


    }
}
