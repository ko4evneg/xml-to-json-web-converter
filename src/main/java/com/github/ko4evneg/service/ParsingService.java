package com.github.ko4evneg.service;

import com.github.ko4evneg.jaxb.Envelope;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

public class ParsingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingService.class);

    public static void parseXml(String xml, HttpServletResponse resp) {
        LOGGER.info("Start XML processing, XML body: " + System.lineSeparator() + xml);
        try (BufferedReader xmlReader = new BufferedReader(new StringReader(xml))) {
            JAXBContext context = JAXBContext.newInstance(Envelope.class);
            Envelope envelope = (Envelope) context.createUnmarshaller().unmarshal(xmlReader);
        } catch (JAXBException e) {
            int errorNumber = new Random().nextInt(100000);
            LOGGER.error("[xml error id: " + errorNumber + "] XML parsing error occurred: " + e);
            try {
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "[xml error id: " + errorNumber + "]Failed XML parsing, please check XML is well-formed. (Cause: " + e + ")");
            } catch (IOException ioException) {
                LOGGER.error("Replying with failed XML error page has failed itself: " + e);
            }
        } catch (IOException e) {
            LOGGER.error("Can't read xml parameter: " + e);
        }
    }
}