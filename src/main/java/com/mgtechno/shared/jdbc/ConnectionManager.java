package com.mgtechno.shared.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.mgtechno.shared.rest.RestConstant.*;

public class ConnectionManager {
    private Properties dbProps;
    private List<Connection> connectionPool = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();

    public ConnectionManager(Properties dbProps) throws SQLException, IOException {
        this.dbProps =dbProps;
        int minPool = Integer.parseInt(dbProps.get(DB_MIN_POOL).toString());
        for(int i = 0; i < minPool ; i++){
            connectionPool.add(createConnection());
        }
    }

    public Connection getConnection() throws SQLException {
        int conSize = getConnectionSize();
        int maxPool = Integer.parseInt(dbProps.get(DB_MAX_POOL).toString());
        if(connectionPool.isEmpty() &&  conSize < maxPool){
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
