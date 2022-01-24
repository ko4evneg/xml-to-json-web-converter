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

    public static void replyWithSuccess(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>JSON send result</title>
                    </head>
                    <body>
                        <h2>Result:</h2>
                        <p>XML parsing: OK</p>
                        <p>XML to JSON conversion: OK</p>
                        <p>TCP packet dispatch: OK</p>
                    </body>
                    </html>
                    """);
    }
}