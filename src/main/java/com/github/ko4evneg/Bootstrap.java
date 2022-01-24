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

//IMPORTANT: For correct test in IDEA, run configuration should have working directory set to $projectdir/target/classes directory.
public class Bootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void start() {
        Path propertiesPath = Path.of("").toAbsolutePath().resolve("config.property");
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(propertiesPath.toString())) {
            LOGGER.info("Loading properties file: " + propertiesPath);
            properties.load(inputStream);
            LOGGER.info("SUCCESS: Loading properties file.");
        } catch (IOException e) {
            LOGGER.error("FAILURE: Loading properties file." + System.lineSeparator() + "Cause: " + e);
            throw new RuntimeException("FAILURE: Loading properties file." + System.lineSeparator() + "Cause: " + e);
        }

        try {
            LOGGER.info("Parsing properties...");
            int httpPort = Integer.parseInt(properties.getProperty("http.port"));
            int destPort = Integer.parseInt(properties.getProperty("tcp.dest.port"));
            InetAddress destAddr = InetAddress.getByName(properties.getProperty("tcp.dest.addr"));
            LOGGER.info(String.format("SUCCESS: Parsing properties. {http.port = %d}, {tcp.dest.port = %d}, " +
                    "{tcp.dest.addr = %s}", httpPort, destPort, destAddr.getHostAddress()));
            LOGGER.info("Starting Jetty server on port: " + httpPort);
            Server server = new Server();
            ServerConnector serverConnector = new ServerConnector(server);
            serverConnector.setPort(httpPort);
            server.setConnectors(new Connector[]{serverConnector});
            ServletContextHandler servletContextHandler = new ServletContextHandler();
            servletContextHandler.addServlet(XmlParserServlet.class, "/");
            servletContextHandler.setAttribute("tcp.dest.port", destPort);
            servletContextHandler.setAttribute("tcp.dest.addr", destAddr.getHostAddress());
            server.setHandler(servletContextHandler);
            server.start();
            LOGGER.info("SUCCESS: Starting Jetty server on port: " + httpPort);
        } catch (NumberFormatException e) {
            LOGGER.error("FAILURE: Parsing properties. Incorrect port property format." + System.lineSeparator() + "Cause: " + e);
            throw new RuntimeException("FAILURE: Parsing properties. Incorrect port property format." + System.lineSeparator() + "Cause: " + e);
        } catch (UnknownHostException e) {
            LOGGER.error("FAILURE: Parsing properties. Incorrect tcp.dest.addr property format." + System.lineSeparator() + "Cause: " + e);
            throw new RuntimeException("FAILURE: Parsing properties. Incorrect tcp.dest.addr property format." + System.lineSeparator() + "Cause: " + e);
        } catch (Exception e) {
            LOGGER.error("FAILURE: Starting Jetty server on port." + System.lineSeparator() + "Cause: " + e);
        }
    }

    public static void main(String[] args) {
        start();
    }
}
