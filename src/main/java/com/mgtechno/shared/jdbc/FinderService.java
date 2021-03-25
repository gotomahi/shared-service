package com.mgtechno.shared.jdbc;

import com.mgtechno.shared.Entity;
import com.mgtechno.shared.KeyValue;
import com.mgtechno.shared.util.CollectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FinderService {
    private static final Logger LOG = Logger.getLogger(FinderService.class.getName());

    protected <B> List<B> load(Connection con, Class<B> clazz, List<KeyValue> criteria)throws Exception{
        List<B> beans = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(selectQuery(clazz, criteria));
            if (CollectionUtil.isNotEmpty(criteria)) {
                int i = 1;
                for (KeyValue kv : criteria) {
                    ps.setObject(i++, kv.getValue());
                }
            }
            rs = ps.executeQuery();
            while(rs.next()){
                B bean = clazz.getDeclaredConstructor().newInstance();
                Field[] fields = bean.getClass().getDeclaredFields();
                for(Field field: fields){
                    if(field.getAnnotation(MappedBy.class) != null || field.getAnnotation(Ignore.class) != null){
                        continue;
                    }
                    field.setAccessible(true);
                    Object value = null;
                    Class type = field.getType();
                    if(type.getName().equals(Date.class.getName())){
                        value = rs.getDate(field.getName());
                    }else if(type.getName().equals(byte[].class.getName()) && rs.getBlob(field.getName()) != null){
                        value = rs.getBlob(field.getName()).getBinaryStream().readAllBytes();
                    }else{
                        value = rs.getObject(field.getName(), type);
                    }
                    field.set(bean, value);
                }

                List<Field> mappedFields = Arrays.stream(fields)
                        .filter(field -> field.getAnnotation(MappedBy.class) != null)
                        .collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(mappedFields)){
                    for(Field field: mappedFields){
                        field.setAccessible(true);
                        MappedBy mappedBy = field.getAnnotation(MappedBy.class);
                        Field mapField = Arrays.stream(fields)
                                .filter(f -> f.getName().equals(mappedBy.property())).findFirst().get();
                        mapField.setAccessible(true);
                        List<KeyValue> mappedCriteria = new ArrayList<>();
                        mappedCriteria.add(new KeyValue(mappedBy.reference(), mapField.get(bean)));
                        if(Arrays.stream(field.getType().getInterfaces()).anyMatch(it -> it.getName().equals(Entity.class.getName()))){
                            List childBeans = load(con, field.getType(), mappedCriteria);
                            field.set(bean, childBeans.get(0));
                        }else if(field.getType().isAssignableFrom(List.class)){
                            Type genType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                            List childBeans = load(con, (Class)genType, mappedCriteria);
                            field.set(bean, childBeans);
                        }
                    }
                }
                beans.add(bean);
            }
        }finally {
            close(ps, rs);
        }
        return beans;
    }

    protected <B> List<B> findByQuery(Connection con, String query, List<KeyValue> criteria)throws Exception{
        return findByQuery(con, query, criteria, null);
    }

    protected <B> List<B> findByQuery(Connection con, String query, List<KeyValue> criteria, Class<B> resultClass)throws Exception{
        List<B> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(query);
            if(CollectionUtil.isNotEmpty(criteria)){
                int i = 1;
                for(KeyValue kv : criteria){
                    ps.setObject(i++, kv.getValue());
                }
            }
            rs = ps.executeQuery();
            while(rs.next()){
                B resultBean = (B)resultClass.getDeclaredConstructor().newInstance();
                Field[] fields = resultClass.getDeclaredFields();
                for(Field field: fields){
                    field.setAccessible(true);
                    field.set(resultBean, rs.getObject(field.getName()));
                }
                result.add(resultBean);
            }
        }finally {
            close(ps, rs);
        }
        return result;
    }

    private<B> String selectQuery(Class<B> clazz, List<KeyValue> criteria)throws Exception{
        StringBuilder query = new StringBuilder("select * from ");
        query.append(clazz.getSimpleName());
        if(CollectionUtil.isNotEmpty(criteria)){
            query.append(" where ");
            int j = 0;
            for(KeyValue kv : criteria){
                query.append(j++ > 0 ? " and " : "").append(kv.getKey()).append("=?");
            }
        }
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
}
