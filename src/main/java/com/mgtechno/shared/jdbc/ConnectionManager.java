package com.mgtechno.shared.jdbc;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.rest.RestConstant.*;

public class ConnectionManager {
    private static final Logger LOG = Logger.getLogger(ConnectionManager.class.getCanonicalName());
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
        if(connectionPool.isEmpty() && usedConnections.size() == maxPool){
            List<Connection> closedCons = new ArrayList<>();
            for(Connection conn : usedConnections) {
                if (conn.isClosed()) {
                    closedCons.add(conn);
                }
            }
            usedConnections.removeAll(closedCons);
            if(usedConnections.size() == maxPool) {
                throw new SQLException("All connections are busy, not availble");
            }
        }
        if(connectionPool.isEmpty() &&  conSize < maxPool){
            connectionPool.add(createConnection());
        }
        Connection con = connectionPool.remove(0);
        usedConnections.add(con);
        return con;
    }

    public void close(Connection con, PreparedStatement pstmt, ResultSet rs){
        try{
            if(con != null){
                usedConnections.remove(con);
                connectionPool.add(con);
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to close connections", e);
        }
    }

    public void close(Connection con, CallableStatement cs, ResultSet rs){
        try{
            if(con != null){
                usedConnections.remove(con);
                connectionPool.add(con);
            }
            if(cs != null){
                cs.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Failed to close connections", e);
        }
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
