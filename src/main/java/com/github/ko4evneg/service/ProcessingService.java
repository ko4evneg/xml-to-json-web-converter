package com.github.ko4evneg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ko4evneg.jaxb.Envelope;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingService.class);

    public static boolean process(String xml, String destAddr, String destPort, HttpServletResponse resp, int requestId) throws IOException{
        try {
            LOGGER.info("[id=" + requestId + "] XML validation and parsing. XML body: " + System.lineSeparator() + xml);
            Envelope jsonEnvelope = parseXmlToEnvelope(xml);
            LOGGER.info("SUCCESS: [id=" + requestId + "] XML validation and parsing.");

            LOGGER.info("[id=" + requestId + "] XML conversion to JSON...");
            String jsonString = convertToJson(jsonEnvelope, resp);
            LOGGER.info("SUCCESS: [id=" + requestId + "] XML conversion to JSON. JSON body: " + System.lineSeparator() + jsonString);

            byte[] tcpPayload = createTcpPayload(jsonString);
            LOGGER.info("[id=" + requestId + "] Packaging and sending data via tcp. HEX data (size: " + tcpPayload.length +
                    "B): " + System.lineSeparator() + DatatypeConverter.printHexBinary(tcpPayload));
            tcpSend(tcpPayload, destAddr, destPort);
            LOGGER.info("SUCCESS: [id=" + requestId + "] Packaging and sending data via tcp.");
            return true;
        } catch (JAXBException e) {
            LOGGER.error("FAILURE: [id=" + requestId + "] XML validation and parsing." + System.lineSeparator() + "Cause: " + e);
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "FAILURE: [id=" + requestId + "] XML validation and parsing." + System.lineSeparator() + "Cause: " + e);
        } catch (JsonProcessingException e) {
            LOGGER.error("FAILURE: [id=" + requestId + "] XML conversion to JSON." + System.lineSeparator() + "Cause: " + e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "FAILURE: [id=" + requestId + "] XML conversion to JSON." + System.lineSeparator() + "Cause: " + e);
        } catch (IOException e) {
            LOGGER.info("FAILURE: [id=" + requestId + "] Packaging and sending data via tcp." + System.lineSeparator() + "Cause: " + e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "FAILURE: [id=" + requestId + "] Packaging and sending data via tcp." + System.lineSeparator() + "Cause: " + e);
        }
        return false;
    }

    private static Envelope parseXmlToEnvelope(String xml) throws JAXBException {
        StringReader xmlReader = new StringReader(xml);
        JAXBContext context = JAXBContext.newInstance(Envelope.class);
        return (Envelope) context.createUnmarshaller().unmarshal(xmlReader);
    }

    private static String convertToJson(Envelope envelope, HttpServletResponse resp) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(envelope);
    }

    private static void tcpSend(byte[] tcpPayload, String destAddr, String destPort) throws IOException {
        Socket socket = new Socket(destAddr, Integer.parseInt(destPort));
        OutputStream out = socket.getOutputStream();
        out.write(tcpPayload);
        out.close();
        socket.close();
    }

    private static byte[] createTcpPayload(String convertedJson) throws UnsupportedEncodingException {
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