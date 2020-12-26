package com.mgtechno.shared;

import com.mgtechno.shared.rest.RequestHandler;
import com.mgtechno.shared.rest.Route;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.mgtechno.shared.AppConfig.appConfig;
import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;
import static com.mgtechno.shared.rest.RestConstant.FORWARD_SLASH;

public class Application {

    protected static void initServer(Route... routes) throws IOException {
        String contextPath = appConfig.getProperty("context.path");
        int port = appConfig.getIntegerProperty("server.port");
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                appConfig.getIntegerProperty("server.threadpool.maxThreads"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RequestHandler requestHandler = new RequestHandler(FORWARD_SLASH.equals(contextPath) ? EMPTY_STRING : contextPath, routes);
        server.createContext(contextPath, requestHandler);
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

}
