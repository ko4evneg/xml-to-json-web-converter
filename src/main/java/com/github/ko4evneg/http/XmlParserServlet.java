package com.github.ko4evneg.http;

import com.github.ko4evneg.service.ProcessingService;
import com.github.ko4evneg.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

@WebServlet("/")
public class XmlParserServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParserServlet.class);
    private static final Random RANDOM = new Random();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        int requestId = getRequestId();
        String logString = String.format("[id=%s] GET request (uri=%s).", requestId, requestURI);
        LOGGER.info(logString);
        try {
            if (req.getRequestURI().equals("/")) {
                ServletUtil.replyWithSubmitForm(resp);
                LOGGER.info("SUCCESS: " + logString);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                LOGGER.error("FAILURE: " + logString + " Page not found, error 404 sent.");
            }
        } catch (IOException e) {
            LOGGER.error("FAILURE: " + logString + System.lineSeparator() + "Cause: " + e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        int requestId = getRequestId();
        String logString = String.format("[id=%s] POST request (uri=%s).", requestId, requestURI);
        LOGGER.info(logString);
        try {
            boolean successResult = ProcessingService.process(req.getParameter("xml"),
                    this.getServletContext().getAttribute("tcp.dest.addr").toString(),
                    this.getServletContext().getAttribute("tcp.dest.port").toString(),
                    resp, requestId);
            if (successResult) {
                LOGGER.info("SUCCESS: " + logString);
                ServletUtil.replyWithSuccess(resp);
            }
        } catch (IOException e) {
            LOGGER.error("FAILURE: " + logString + System.lineSeparator() + "Cause: " + e);
        }
    }

    private int getRequestId() {
        return RANDOM.nextInt(100000);
    }
}