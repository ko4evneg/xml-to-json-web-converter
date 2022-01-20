package com.github.ko4evneg;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class Bootstrap {
    private static Server server;

    //TODO: Exception logging
    public static void start() throws Exception {
        server = new Server();
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(8080);
        server.setConnectors(new Connector[]{serverConnector});
        server.start();
    }

    public static void main(String[] args) throws Exception {
        start();
    }
}
