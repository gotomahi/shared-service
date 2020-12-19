package com.mgtechno.shared;

import com.mgtechno.shared.rest.RequestHandler;
import com.mgtechno.shared.rest.Route;
import com.sun.net.httpserver.HttpServer;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;
import static com.mgtechno.shared.rest.RestConstant.FORWARD_SLASH;

public class Application {
    public static Properties props;
    public DataSource dataSource;
    public static Application application;

    protected static void loadConfig(String env) throws IOException {
        props = new Properties();
        props.load(Application.class.getClassLoader().getResourceAsStream( env + "/application.properties"));
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

    public static Connection getConnection()throws SQLException{
        return application.dataSource.getConnection();
    }

    public static void close(Connection con, PreparedStatement ps, ResultSet rs){
        application.closeCon(con, ps, rs);
    }

    public static void close(Connection con, CallableStatement cs, ResultSet rs){
        application.closeCon(con, cs, rs);
    }

    private void closeCon(Connection con, PreparedStatement ps, ResultSet rs){
        try{
            if(con != null){
                con.close();
            }
            if(ps != null){
                ps.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch(Exception e){
        }
    }

    private void closeCon(Connection con, CallableStatement cs, ResultSet rs){
        try{
            if(con != null){
                con.close();
            }
            if(cs != null){
                cs.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch(Exception e){
        }
    }

}
