package com.sensorup.iot.evaluate;

import com.google.common.collect.ImmutableMap;
import com.sensorup.iot.action.PostToNewDsAction;
import org.json.JSONObject;

import java.util.Map;

public class Evaluator {
    private static Map<String, Class> evaluatorClassMap = ImmutableMap.<String, Class>builder()
            .put("minmax", MinMaxEvaluator.class)
            .build();

    public static boolean evaluate(String evaluationType, String topic, JSONObject message) throws IllegalAccessException, InstantiationException {

        return ((EvatuatorInterface)evaluatorClassMap.get(evaluationType).newInstance()).evaluate(topic, message);

    }
}
