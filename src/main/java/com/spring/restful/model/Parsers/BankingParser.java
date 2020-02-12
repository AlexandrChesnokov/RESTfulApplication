package com.spring.restful.model.Parsers;

import com.spring.restful.model.Currency;

import java.io.IOException;

public interface BankingParser {

    Currency getParse(String name, String period, String response) throws IOException;
}
