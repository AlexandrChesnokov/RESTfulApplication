package com.spring.restful.model;

import java.io.IOException;

public interface BankingParser {

    Currency getParse(String name, String period, String response) throws IOException;
}
