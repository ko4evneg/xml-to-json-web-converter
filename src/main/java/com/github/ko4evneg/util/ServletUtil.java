package com.github.ko4evneg.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtil {
    public static void replyWithSubmitForm(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>XML submit form</title>
                </head>
                <body>
                <form action="/" method="post">
                    <textarea name="xml"></textarea>
                    <input type="submit"/>
                </form>
                </body>
                </html>
                """);
    }
}