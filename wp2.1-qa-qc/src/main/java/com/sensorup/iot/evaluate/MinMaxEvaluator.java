package com.sensorup.iot.evaluate;

import com.sensorup.iot.utility.PropertyReader;
import com.sensorup.iot.utility.STAHttpClient;
import io.netty.util.internal.StringUtil;
import org.json.JSONObject;

public class MinMaxEvaluator implements EvatuatorInterface {
    public boolean evaluate(String topic, JSONObject observation) {
        long obsId = Long.parseLong(observation.getString("@iot.selfLink").substring(observation.getString("@iot.selfLink").indexOf("(") + 1, observation.getString("@iot.selfLink").indexOf(")")));
        String dsPropertiesResponse = STAHttpClient.getInstance().doGet(PropertyReader.getInstance().getProperty("sta-server") + "/Observations(" + obsId + ")/Datastream/properties/$value").get("response").toString();

        if (StringUtil.isNullOrEmpty(dsPropertiesResponse)) {
            return false;
        }
        JSONObject dsProperties = new JSONObject(dsPropertiesResponse);
        if (!(dsProperties.has("valid-range") &&
                (dsProperties.getJSONObject("valid-range").has("min") || dsProperties.getJSONObject("valid-range").has("max")))) {
            return false;
        }
        boolean evaluationResult = true;
        if (dsProperties.getJSONObject("valid-range").has("min")) {
            evaluationResult &= observation.getDouble("result") >= dsProperties.getJSONObject("valid-range").getDouble("min");
        }
        if (dsProperties.getJSONObject("valid-range").has("max")) {
            evaluationResult &= observation.getDouble("result") <= dsProperties.getJSONObject("valid-range").getDouble("max");
        }
        return evaluationResult;
    }
}
