package com.github.ko4evneg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class ProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingService.class);

    public static void parseXml(String xml, HttpServletResponse resp) {
        LOGGER.info("Start XML parsing, XML body: " + System.lineSeparator() + xml);
        try (BufferedReader xmlReader = new BufferedReader(new StringReader(xml))) {
            JAXBContext context = JAXBContext.newInstance(Envelope.class);
            Envelope envelope = (Envelope) context.createUnmarshaller().unmarshal(xmlReader);
            LOGGER.info("SUCCESS: XML parsing. Starting conversion to JSON");
            String convertedJson = convertToJson(envelope, resp);
            if (convertedJson != null) {
                LOGGER.info("SUCCESS: conversion to JSON");
                //send to socket code
            }
        } catch (JAXBException e) {
            int errorNumber = new Random().nextInt(100000);
            LOGGER.error("[xml error id: " + errorNumber + "] XML parsing error occurred: " + e);
            try {
                //todo: verify error code
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                        "[xml error id: " + errorNumber + "]Failed XML parsing, please check XML is well-formed. (Cause: " + e + ")");
            } catch (IOException ioException) {
                LOGGER.error("Replying with failed XML error page has failed itself: " + e);
            }
        } catch (IOException e) {
            LOGGER.error("Can't read xml parameter: " + e);
        }
    }

    private static String convertToJson(Envelope envelope, HttpServletResponse resp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        int errorNumber = new Random().nextInt(100000);
        try {
            return mapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            LOGGER.error("[convert error id: " + errorNumber + "] JSON conversion error occured: " + e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "[convert error id: " + errorNumber + "] Failed to convert XML to JSON (Cause: " + e);
            return null;
        }
    }
}