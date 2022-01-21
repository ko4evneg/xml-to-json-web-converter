package com.github.ko4evneg.http;

import com.github.ko4evneg.service.ParsingService;
import com.github.ko4evneg.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/")
public class XmlParserServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlParserServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        LOGGER.info("GET request, uri = " + requestURI);
        try {
            if (req.getRequestURI().equals("/")) {
                ServletUtil.replyWithSubmitForm(resp);
                LOGGER.info("end submit-form: GET request, uri = " + requestURI);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                LOGGER.error("end 404-error: GET request, uri = " + requestURI);
            }
        } catch (IOException e) {
            LOGGER.error("IOException occurred: " + e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        LOGGER.info("start: POST request, uri = " + requestURI);
        ParsingService.processXml(req.getParameter("xml"), resp);
    }
}