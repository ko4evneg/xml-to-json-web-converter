package com.github.ko4evneg;

import com.github.ko4evneg.http.XmlParserServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Properties;

public class Bootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void start() {
        Path propertiesPath = Path.of("").toAbsolutePath().resolve("config.property");
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(propertiesPath.toString())) {
            LOGGER.info("Loading properties file: " + propertiesPath);
            properties.load(inputStream);
            LOGGER.info("SUCCESS: Loading properties file");
        } catch (IOException e) {
            LOGGER.error("Can't load properties file (cause: " + e + ")");
            throw new RuntimeException("Can't load properties file (cause: " + e + ")");
        }

        try {
            LOGGER.info("Parsing properties");
            int httpPort = Integer.parseInt(properties.getProperty("http.port"));
            int destPort = Integer.parseInt(properties.getProperty("tcp.dest.port"));
            InetAddress destAddr = InetAddress.getByName(properties.getProperty("tcp.dest.addr"));
            LOGGER.info(String.format("SUCCESS: Parsing properties. [http.port = %d], [tcp.dest.port = %d], " +
                    "[tcp.dest.addr = %s]", httpPort, destPort, destAddr.getHostAddress()));

            LOGGER.info("Starting Jetty server on port " + httpPort);
            Server server = new Server();
            ServerConnector serverConnector = new ServerConnector(server);
            serverConnector.setPort(httpPort);
            server.setConnectors(new Connector[]{serverConnector});
            ServletContextHandler servletContextHandler = new ServletContextHandler();
            servletContextHandler.addServlet(XmlParserServlet.class, "/");
            server.setHandler(servletContextHandler);
            server.start();
            LOGGER.info("SUCCESS: Starting Jetty server on port " + httpPort);
        } catch (NumberFormatException e) {
            LOGGER.error("Incorrect port property format (cause: " + e + ")");
            throw new RuntimeException("Incorrect port property format (cause: " + e + ")");
        } catch (UnknownHostException e) {
            LOGGER.error("Incorrect tcp.dest.addr property format (cause: " + e + ")");
            throw new RuntimeException("Incorrect tcp.dest.addr property format (cause: " + e + ")");
        } catch (Exception e) {
            LOGGER.error("Jetty server caught an exception (cause: " + e + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        start();
    }
}
