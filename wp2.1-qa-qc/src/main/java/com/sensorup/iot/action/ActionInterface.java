package com.sensorup.iot.action;

import org.json.JSONObject;

public interface ActionInterface {
    void execute(String topic, JSONObject message);
}
