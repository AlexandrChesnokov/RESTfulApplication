package com.spring.restful.model.parsers;

import com.spring.restful.model.Currency;

import java.io.IOException;

public interface BankingParser {

    Currency getParse(String name, String response) throws IOException;
}
