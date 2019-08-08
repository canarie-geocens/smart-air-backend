package com.sensorup.iot.action;


import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Action {

    private static Map<String,Class> actionClassMap = ImmutableMap.<String, Class>builder()
            .put("post-to-new-ds", PostToNewDsAction.class)
            .build();

    public static void execute(String actionType, String topic, JSONObject message) throws IllegalAccessException, InstantiationException {
        ((ActionInterface)actionClassMap.get(actionType).newInstance()).execute(topic,message);
    }
}

