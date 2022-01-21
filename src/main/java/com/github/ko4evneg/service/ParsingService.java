package com.github.ko4evneg.service;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ParsingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingService.class);

    public static void processXml(String xml, HttpServletResponse resp){
        LOGGER.info("Start XML processing, XML body: " + System.lineSeparator() + xml);
        try {
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Failed XML");
        } catch (IOException e) {
            LOGGER.error("IOException occurred: " + e);
        }
    }
}