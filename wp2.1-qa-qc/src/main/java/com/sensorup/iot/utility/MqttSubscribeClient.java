package com.sensorup.iot.utility;

import com.sensorup.iot.sta.qaqc.ObservationQaQc;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MqttSubscribeClient implements MqttCallbackExtended {

    private static final Logger logger = LoggerFactory
            .getLogger(MqttSubscribeClient.class);

    MqttAsyncClient mqttClient;
    ObservationQaQc observationQaQc;
    String url;
    String targetSta;

    public MqttSubscribeClient(ObservationQaQc observationQaQc, String url, String targetSta) {
        this.observationQaQc = observationQaQc;
        this.url = url;
        this.targetSta = targetSta;
        String mqttServer = url;
        try {
            String broker;
            if (Boolean.parseBoolean(PropertyReader.getInstance().getProperty("mqtt-ssl"))) {
                broker = "ssl://" + mqttServer + ":8883";
            } else {
                broker = "tcp://" + mqttServer + ":1883";
            }
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setAutomaticReconnect(true);
            if(PropertyReader.getInstance().getProperty("mqtt-username") != null) {
                connOpts.setUserName(PropertyReader.getInstance().getProperty("mqtt-username"));
            }
            if(PropertyReader.getInstance().getProperty("mqtt-password") != null ) {
                connOpts.setPassword(PropertyReader.getInstance().getProperty("mqtt-password").toCharArray());
            }

            mqttClient = new MqttAsyncClient(broker, MqttAsyncClient.generateClientId());
            mqttClient.setCallback(this);
            mqttClient.connect(connOpts).waitForCompletion();

        } catch (Exception e) {
            logger.error("Mqtt Connection cannot be established and program stopped working due to: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos) {
        try {
            mqttClient.subscribe(topic, qos);
        } catch (MqttException e) {
            logger.error("Cannot subscribe to " + topic + " due to: " + e.getMessage());
        }
    }

    public void connectionLost(Throwable arg0) {
    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
    }

    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        try {
            observationQaQc.processReceivedMessage(topic, message.toString(), this);
        } catch (Exception e) {
            logger.error("There is a problem in processing arrived message due to: " + e.getMessage());
        }
    }

    public void connectComplete(boolean b, String s) {

    }
}
