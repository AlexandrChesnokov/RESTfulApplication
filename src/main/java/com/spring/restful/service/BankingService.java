package com.spring.restful.service;


import com.spring.restful.model.Currency;
import com.spring.restful.model.MainCurrency;
import com.spring.restful.utils.ReaderFromUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;



public abstract class BankingService {

       public abstract CompletableFuture<Currency> getExchangeRate(String name, String period, boolean isDate) throws IOException;


       int getCount(String period) {

              int counter = 1;

               if (period.equals("week")){
                     counter = 7;
              } else if (period.equals("month")) {
                     counter = 31;
              } else {
                     return counter;
              }

              return counter;
       }


       public MainCurrency getBest(ArrayList<MainCurrency> list){

              MainCurrency minRate = list.get(0);
              for (MainCurrency rate : list) {
                     if (Double.parseDouble(rate.getSaleRate()) < Double.parseDouble(minRate.getSaleRate())) {
                            minRate = rate;
                     }
              }
              return minRate;

       }
}
