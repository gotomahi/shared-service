package com.mgtechno.shared.jdbc;

import com.mgtechno.shared.KeyValue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaoService {
    private static final Logger LOG = Logger.getLogger(DaoService.class.getCanonicalName());
    public static final DaoService daoService = new DaoService();

    protected ThreadLocal<Connection> localConn = new ThreadLocal<>();
    protected DataSource dataSource;
    private PersistenceService persistenceService = new PersistenceService();
    private FinderService finderService = new FinderService();

    public void init(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void beginTransaction() {
        try {
            localConn.set(dataSource.getConnection());
            localConn.get().setAutoCommit(false);
        }catch (SQLException e){
            LOG.log(Level.SEVERE, "failed to start transaction" + e);
        }
    }

    public void commit(){
        try {
            localConn.get().commit();
            localConn.get().close();
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to commit" + e);
        }
    }

    public void rollback(){
        try {
            localConn.get().rollback();
            localConn.get().close();
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to rollback" + e);
        }
    }

    public <B> B persist(B bean)throws Exception{
        try {
            bean = persistenceService.persist(localConn.get(), bean);
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to save bean " + bean.getClass().getName(), e);
            throw e;
        }
        return bean;
    }

    public <B> B load(Class<B> clazz, List<KeyValue> criteria)throws Exception{
        B bean = null;
        try {
            List<B> beans = finderService.load(localConn.get(), clazz, criteria);
            bean = !beans.isEmpty() ? beans.get(0) : null;
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to load bean " + clazz.getName() + " with criteria " + criteria, e);
            throw e;
        }
        return bean;
    }

    public <B> List<B> findByCriteria(Class<B> clazz, List<KeyValue> criteria)throws Exception{
        List<B> beans = null;
        try {
            beans = finderService.load(localConn.get(), clazz, criteria);
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to find" + clazz.getName() + " with criteria " + criteria, e);
            throw e;
        }
        return beans;
    }

    public <B> List<B> findByQuery(String query, List<KeyValue> criteria, Class<B> resultClass)throws Exception{
        try{
            return finderService.findByQuery(localConn.get(), query, criteria, resultClass);
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to find query" + query + " with criteria " + criteria, e);
            throw e;
        }
    }

    public <B> List<B> findByQuery(String query, List<KeyValue> criteria)throws Exception{
        try{
            return finderService.findByQuery(localConn.get(), query, criteria);
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to find query" + query + " with criteria " + criteria, e);
            throw e;
        }
    }

    public void executeStoredProc(String query, List<KeyValue> criteria)throws Exception{
        try{
            persistenceService.executeStoredProc(localConn.get(), query, criteria);
        }catch (Exception e){
            throw e;
        }
    }

}
