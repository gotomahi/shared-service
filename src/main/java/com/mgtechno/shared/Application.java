package com.mgtechno.shared;

import com.mgtechno.shared.jdbc.ConnectionManager;
import com.mgtechno.shared.rest.RequestHandler;
import com.mgtechno.shared.rest.Route;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;
import static com.mgtechno.shared.rest.RestConstant.FORWARD_SLASH;

public class Application {
    public static Properties props;
    public static ConnectionManager conManager;

    protected static void loadConfig(String env) throws IOException {
        props = new Properties();
        props.load(Application.class.getClassLoader().getResourceAsStream( env + "/application.properties"));
    }

    protected static void initDBConManager(String env) throws IOException, SQLException {
        Properties dbProps = new Properties();
        dbProps.load(Application.class.getClassLoader().getResourceAsStream( env + "/db.properties"));
        conManager = new ConnectionManager(dbProps);
    }

    protected static void initServer(Route... routes) throws IOException {
        String contextPath = props.getProperty("context.path");
        int port = getIntegerProperty("server.port");
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                getIntegerProperty("server.threadpool.maxThreads"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RequestHandler requestHandler = new RequestHandler(FORWARD_SLASH.equals(contextPath) ? EMPTY_STRING : contextPath, routes);
        server.createContext(contextPath, requestHandler);
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

    public static int getIntegerProperty(String property){
        return Integer.parseInt(props.getProperty(property));
    }

    public static String getProperty(String property){
        return props.getProperty(property);
    }

    public static boolean getBooleanProperty(String property){
        return Boolean.parseBoolean(props.getProperty(property));
    }
}
