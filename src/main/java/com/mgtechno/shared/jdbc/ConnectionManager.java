package com.mgtechno.shared.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static com.mgtechno.shared.rest.RestConstant.*;

public class ConnectionManager {
    private Properties dbProps = new Properties();
    private List<Connection> connectionPool;
    private List<Connection> usedConnections;
    private static ConnectionManager conManager;
    public synchronized static ConnectionManager getInstance() throws IOException, SQLException {
        if (conManager == null) {
            conManager = new ConnectionManager();
        }
        return conManager;
    }

    private ConnectionManager() throws SQLException, IOException {
        dbProps.load(ConnectionManager.class.getClassLoader().getResourceAsStream(FILE_DB_PROPERTIES));
        for(int i = 0; i < (Integer)dbProps.get(DB_MIN_POOL); i++){
            connectionPool.add(createConnection());
        }
    }

    public Connection getConnection() throws SQLException {
        int conSize = getConnectionSize();
        if(connectionPool.isEmpty() &&  conSize < (Integer)dbProps.get(DB_MAX_POOL)){
            connectionPool.add(createConnection());
        }
        Connection con = connectionPool.remove(0);
        usedConnections.add(con);
        return con;
    }

    private Connection createConnection() throws SQLException {
        Connection con = DriverManager.getConnection(dbProps.getProperty(DB_URL), dbProps.getProperty(DB_USER),
                dbProps.getProperty(DB_PASSWORD));
        con.setAutoCommit(false);
        return con;
    }

    private int getConnectionSize(){
        return connectionPool.size() + usedConnections.size();
    }
}
