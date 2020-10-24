package com.mgtechno.shared;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

public class FileContentReader {
    public Properties loadProps(String filePath){
        Properties props = new Properties();
        Reader reader = null;
        try{
            reader = new BufferedReader(new FileReader("application.properties"));
            props.load(reader);
        } catch (Exception e) {
        }finally {
            closeReader(reader);
        }
        return props;
    }
    private void closeReader(Reader reader){
        if(reader != null){
            try {
                reader.close();
            }catch (Exception e1){}
        }
    }
}
