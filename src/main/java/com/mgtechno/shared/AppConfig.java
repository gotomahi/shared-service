package com.mgtechno.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppConfig {
    public static final AppConfig appConfig = new AppConfig();

    private Properties props = new Properties();
    private Map<String, Properties> propFileMap = new HashMap<>();

    public void init(String env)throws Exception{
        props.load(Application.class.getClassLoader().getResourceAsStream( env + "/application.properties"));
    }

    public void loadProps(String file)throws Exception{
        Properties props = new Properties();
        props.load(Application.class.getClassLoader().getResourceAsStream( file + ".properties"));
        propFileMap.put(file, props);
    }

    public Properties getProperties(String file){
        return propFileMap.get(file);
    }

    public int getIntegerProperty(String property){
        return Integer.parseInt(props.getProperty(property));
    }

    public String getProperty(String property){
        return props.getProperty(property);
    }

    public String getProperty(String property, String defValue){
        return props.getProperty(property, defValue) ;
    }

    public boolean getBooleanProperty(String property){
        return Boolean.parseBoolean(props.getProperty(property));
    }
}
