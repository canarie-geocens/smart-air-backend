package com.sensorup.iot.utility;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;
import java.util.NoSuchElementException;


public class PropertyReader {

    private static PropertyReader propertyReader;
    private CompositeConfiguration config;


    public static PropertyReader getInstance() {
        if(propertyReader == null) {
            propertyReader = new PropertyReader();
            propertyReader.config = new CompositeConfiguration();
            try {
                propertyReader.config.addConfiguration(new PropertiesConfiguration("config.properties"));

            } catch (ConfigurationException e) {
                //do nothing
            }
        }
        return propertyReader;
    }



    public String getProperty(String propertyName){
        if(System.getenv().containsKey(propertyName)){
            return System.getenv(propertyName);
        }
        else if (!config.isEmpty()) {
            try {
                String result = config.getProperty(propertyName).toString();
                if (result.equals("")) {
                    return null;
                }
                return result;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
        return null;
    }

}
