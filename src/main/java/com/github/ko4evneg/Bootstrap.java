package com.github.ko4evneg;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Properties;

public class Bootstrap {
    //TODO: Exception logging
    public static void start() throws Exception {
        //Load properties
        Path propertiesPath = Path.of("").toAbsolutePath().resolve("config.property");
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(propertiesPath.toString())) {
            properties.load(inputStream);
        } catch (IOException e) {
            //TODO: logging
            throw new RuntimeException("Can't load properties file." + System.lineSeparator() + e);
        }

        int httpPort;
        int destPort;
        InetAddress destAddr;
        try {
            httpPort = Integer.parseInt(properties.getProperty("http.port"));
            destPort = Integer.parseInt(properties.getProperty("tcp.dest.port"));
            destAddr = InetAddress.getByName(properties.getProperty("tcp.dest.addr"));
        } catch (Exception e) {
            //TODO: logging
            throw new RuntimeException("Properties file does not contain all correct parameters needed." + System.lineSeparator() + e);
        }

        Server server = new Server();
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(httpPort);
        server.setConnectors(new Connector[]{serverConnector});
        server.start();
    }

    public static void main(String[] args) throws Exception {
        start();
    }
}
