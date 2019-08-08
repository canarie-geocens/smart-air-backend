package com.sensorup.iot.sta.qaqc;

import com.sensorup.iot.action.Action;
import com.sensorup.iot.evaluate.Evaluator;
import com.sensorup.iot.utility.MqttSubscribeClient;
import com.sensorup.iot.utility.PropertyReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ObservationQaQc {

    private static final Logger logger = LoggerFactory
            .getLogger(ObservationQaQc.class);


    MqttSubscribeClient staMqttSubscribeClient;
    String evaluationMethod;
    String actionType;


    public ObservationQaQc() {

        staMqttSubscribeClient = new MqttSubscribeClient(this, PropertyReader.getInstance().getProperty("mqtt-server"),
                PropertyReader.getInstance().getProperty("mqtt-server"));
        staMqttSubscribeClient.subscribe("v1.0/+/Observations", 2);
        evaluationMethod = PropertyReader.getInstance().getProperty("evaluation-method");
        actionType = PropertyReader.getInstance().getProperty("action-type");
    }

    public void processReceivedMessage(String topic, String message, MqttSubscribeClient mqttSubscribeClient) {

        JSONObject messageJson;
        /* Check if the message is valid JSON */
        try {
            messageJson = new JSONObject(message);
        } catch (JSONException e) {
            logger.error("Message is not valid json. Failed to process mqtt message due to: " + e.getMessage());
            return;
        }

        /* Check if the message is valid from STA */
        if (!messageJson.has("@iot.id")) {
            return;
        }


        try {
            /* Evaluate the message and if verified, do the action on it */
            if (Evaluator.evaluate(evaluationMethod, topic, messageJson)) {
                Action.execute(actionType, topic, messageJson);
            }

        } catch (IllegalAccessException e) {
            logger.error("Cannot operate evaluation and action, possibly because of problem in config.properties, due to: "+e.getMessage());
        } catch (InstantiationException e) {
            logger.error("Cannot operate evaluation and action, possibly because of problem in config.properties, due to: "+e.getMessage());
        }


    }


    public static void main(String[] args) {
        new ObservationQaQc();
    }
}
