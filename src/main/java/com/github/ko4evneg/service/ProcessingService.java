package com.github.ko4evneg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ko4evneg.jaxb.Envelope;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class ProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingService.class);

    public static String parseAndConvertXmlToJson(String xml, HttpServletResponse resp) {
        LOGGER.info("Start XML parsing, XML body: " + System.lineSeparator() + xml);
        try (BufferedReader xmlReader = new BufferedReader(new StringReader(xml))) {
            JAXBContext context = JAXBContext.newInstance(Envelope.class);
            Envelope envelope = (Envelope) context.createUnmarshaller().unmarshal(xmlReader);
            LOGGER.info("SUCCESS: XML parsing. Starting conversion to JSON");
            String convertedJson = convertToJson(envelope, resp);
            LOGGER.info("SUCCESS: XML converted to JSON. Result: " + System.lineSeparator() + convertedJson);
            return convertedJson;
        } catch (JAXBException e) {
            int errorNumber = new Random().nextInt(100000);
            LOGGER.error("[xml error id: " + errorNumber + "] XML parsing error occurred: " + e);
            try {
                //todo: verify error code
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                        "[xml error id: " + errorNumber + "]Failed XML parsing, please check XML is well-formed. (Cause: " + e + ")");
            } catch (IOException ioException) {
                LOGGER.error("Replying with failed XML error page has failed itself: " + ioException);
            }
        } catch (IOException e) {
            LOGGER.error("Can't read xml parameter: " + e);
        }
        return null;
    }

    private static String convertToJson(Envelope envelope, HttpServletResponse resp) {
        ObjectMapper mapper = new ObjectMapper();
        int errorNumber = new Random().nextInt(100000);
        try {
            return mapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            LOGGER.error("[convert error id: " + errorNumber + "] JSON conversion error occured: " + e);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "[convert error id: " + errorNumber + "] Failed to convert XML to JSON (Cause: " + e);
            } catch (IOException ioException) {
                LOGGER.error("Replying with failed XML error page has failed itself: " + ioException);
            }
        }
        return null;
    }

    public static void tcpSend(String convertedJson, String destAddr, String destPort) {
        try {
            Socket socket = new Socket(destAddr, Integer.parseInt(destPort));
            OutputStream out = socket.getOutputStream();
            byte[] packetTo = createResultOutPacket(convertedJson);
            out.write(packetTo);
            out.close();
            socket.close();
            LOGGER.error("SUCCESS: TCP sending of result data set.");
        } catch (IOException e) {
            LOGGER.error("ERROR: TCP sending of result data set: " + e);
        }
    }

    private static byte[] createResultOutPacket(String convertedJson) throws UnsupportedEncodingException {
        byte[] bytesJson = convertedJson.getBytes("UTF_16LE");
        byte[] fixedMagic = new byte[]{(byte) 0xFF, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD};
        byte[] jsonLength = convertToLittleEndian(bytesJson.length);
        byte[] resultDataToSend = new byte[8 + bytesJson.length];
        System.arraycopy(fixedMagic, 0, resultDataToSend, 0, fixedMagic.length);
        System.arraycopy(jsonLength, 0, resultDataToSend, fixedMagic.length, jsonLength.length);
        System.arraycopy(bytesJson, 0, resultDataToSend, fixedMagic.length + jsonLength.length, bytesJson.length);
        return resultDataToSend;
    }

    private static byte[] convertToLittleEndian(int num) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(num);
        return byteBuffer.array();
    }
}