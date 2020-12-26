package com.mgtechno.shared.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.jdbc.DaoService.daoService;

public class ServiceHandler implements InvocationHandler {
    private static final Logger LOG = Logger.getLogger(ServiceHandler.class.getName());
    private Object service;

    public static Object newInstance(Object service) {
        return java.lang.reflect.Proxy.newProxyInstance(
                service.getClass().getClassLoader(),
                service.getClass().getInterfaces(),
                new ServiceHandler(service));
    }

    private ServiceHandler(Object service) {
        this.service = service;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result = null;
        try {
            daoService.beginTransaction();
            result = m.invoke(service, args);
            daoService.commit();
        } catch (Exception e) {
            daoService.rollback();
            LOG.log(Level.SEVERE, "failed to method invokation ", e);
        }
        return result;
    }
}
