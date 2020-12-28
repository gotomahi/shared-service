package com.mgtechno.shared.jdbc;

import com.mgtechno.shared.Entity;
import com.mgtechno.shared.KeyValue;
import com.mgtechno.shared.util.CollectionUtil;

import javax.sql.rowset.serial.SerialBlob;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PersistenceService {
    private static final Logger LOG = Logger.getLogger(PersistenceService.class.getName());

    public <B> B persist(Connection con, B bean)throws Exception{
        Field[] fields = bean.getClass().getDeclaredFields();
        bean = saveOrUpdate(con, bean);
        List<Field> mappedFields = Arrays.stream(fields)
                .filter(field -> field.getAnnotation(MappedBy.class) != null)
                .collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(mappedFields)){
            for(Field field: mappedFields){
                field.setAccessible(true);
                Object mappedBean = field.get(bean);
                MappedBy mappedBy = field.getAnnotation(MappedBy.class);
                Field mapField = Arrays.stream(fields)
                        .filter(f -> f.getName().equals(mappedBy.property())).findFirst().get();
                mapField.setAccessible(true);
                if(mappedBean instanceof Entity){
                    setMappedValue(mappedBean, mappedBy.reference(), mapField.get(bean));
                    saveOrUpdate(con, mappedBean);
                }else if(mappedBean instanceof Collection){
                    for(Object mappedEntity : ((Collection)mappedBean)){
                        setMappedValue(mappedEntity, mappedBy.reference(), mapField.get(bean));
                        saveOrUpdate(con, mappedEntity);
                    }
                }
            }
        }
        return bean;
    }

    private<T> T saveOrUpdate(Connection con, T bean)throws Exception{
        Field[] fields = bean.getClass().getDeclaredFields();
        Field id = Arrays.stream(fields).filter(field -> field.getAnnotation(Id.class) != null).findFirst().get();
        id.setAccessible(true);
        boolean update = id.get(bean) != null ? true : false;
        String table = bean.getClass().getSimpleName();
        String query = update ? updateQuery(fields, table, id) : insertQuery(fields, table);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for(Field field : fields){
                field.setAccessible(true);
                if(field.getAnnotation(Id.class) != null || field.getAnnotation(MappedBy.class) != null){
                    continue;
                }
                Object value = field.get(bean);
                if(value instanceof Date){
                    value = new java.sql.Date(((Date)value).getTime());
                }else if(value instanceof byte[]){
                    value = new SerialBlob(((byte[])value));
                }
                ps.setObject(i++, value);
            }
            if(update){
                ps.setObject(i++, id.get(bean));
            }
            ps.execute();
            if(!update){
                rs = ps.getGeneratedKeys();
                if(rs.next()){
                    id.set(bean, rs.getObject(1, id.getType()));
                }
            }
        }catch (Exception e) {
            LOG.log(Level.SEVERE, "failed to save bean " + bean.getClass().getName());
            throw  e;
        }finally {
            close(ps, rs);
        }
        return bean;
    }

    public void executeStoredProc(Connection con, String query, List<KeyValue> criteria)throws Exception{
        CallableStatement cs = null;
        try{
            cs = con.prepareCall(query);
            if(CollectionUtil.isNotEmpty(criteria)) {
                for(KeyValue kv : criteria) {
                    cs.setObject(kv.getKey(), kv.getValue());
                }
            }
            cs.execute();
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Failed to save user's role", e);
            throw e;
        }finally {
            close(cs, null);
        }
    }

    private void setMappedValue(Object mappedBean, String mappedProp , Object mappedValue) throws IllegalAccessException {
        Field[] fields = mappedBean.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
        }
        Field mapField = Arrays.stream(fields)
                .filter(f -> f.getName().equals(mappedProp))
                .findFirst().get();
        mapField.setAccessible(true);
        mapField.set(mappedBean, mappedValue);
    }

    private String insertQuery(Field[] fields, String table){
        StringBuilder query = new StringBuilder("insert into ");
        query.append(table).append("( ");
        int i = 1;
        for(Field field : fields){
            if(field.getAnnotation(Id.class) != null || field.getAnnotation(MappedBy.class) != null){
                continue;
            }
            query.append(i > 1 ? ", " : "").append(field.getName());
            i++;
        }
        query.append(" ) values (");
        int j = 1;
        for (Field field : fields) {
            if(field.getAnnotation(Id.class) != null || field.getAnnotation(MappedBy.class) != null){
                continue;
            }
            query.append(j++ > 1 ? ", " : "").append("?");
        }
        query.append(")");
        return query.toString();
    }

    private String updateQuery(Field[] fields, String table, Field id){
        StringBuilder query = new StringBuilder("update ");
        query.append(table).append(" set ");
        int i = 1;
        for(Field field : fields){
            if(field.getAnnotation(Id.class) != null || field.getAnnotation(MappedBy.class) != null){
                continue;
            }
            query.append(i++ > 1 ? ", " : "").append(field.getName()).append("=?");
        }
        query.append(" where ").append(id.getName()).append("=? ");
        return query.toString();
    }

    private void close(PreparedStatement ps, ResultSet rs){
        try{
            if(ps != null){
                ps.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to close databae resources", e);
        }
    }

    private void close(CallableStatement cs, ResultSet rs){
        try{
            if(cs != null){
                cs.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch (Exception e){
            LOG.log(Level.SEVERE, "failed to close databae resources", e);
        }
    }
}
