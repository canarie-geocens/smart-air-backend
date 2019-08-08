package com.sensorup.iot.evaluate;

import org.json.JSONObject;

public interface EvatuatorInterface {
    public boolean evaluate (String topic, JSONObject message);
}
