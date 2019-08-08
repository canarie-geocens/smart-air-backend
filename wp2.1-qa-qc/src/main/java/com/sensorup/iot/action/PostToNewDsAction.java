package com.sensorup.iot.action;

import com.sensorup.iot.utility.PropertyReader;
import com.sensorup.iot.utility.STAHttpClient;
import org.json.JSONObject;

public class PostToNewDsAction implements ActionInterface {

    public void execute(String topic, JSONObject message) {
        long dsId = Long.parseLong(topic.substring(topic.indexOf("(") + 1, topic.indexOf(")")));
        String ds = STAHttpClient.getInstance().doGet(PropertyReader.getInstance().getProperty("sta-server") + "/Datastreams(" + dsId + ")/Thing/Datastreams?$filter=name eq '" + dsId + "-refined'").get("response").toString();
        long refinedDsId = -1;
        if (new JSONObject(ds).getJSONArray("value").length() == 0) {
            refinedDsId = createRefinedDatastresm(message);
        } else {
            refinedDsId = new JSONObject(ds).getJSONArray("value").getJSONObject(0).getLong("@iot.id");
        }
        String url = PropertyReader.getInstance().getProperty("sta-server") + "/Datastreams(" + refinedDsId + ")/Observations";
        message.remove("@iot.id");
        STAHttpClient.getInstance().doPost(message.toString(), url);
    }

    private long createRefinedDatastresm(JSONObject observation) {
        long obsId = Long.parseLong(observation.getString("@iot.selfLink").substring(observation.getString("@iot.selfLink").indexOf("(") + 1, observation.getString("@iot.selfLink").indexOf(")")));
        JSONObject ds = new JSONObject(STAHttpClient.getInstance().doGet(PropertyReader.getInstance().getProperty("sta-server") + "/Observations(" + obsId + ")/Datastream?$expand=Thing($select=id),Sensor($select=id),ObservedProperty($select=id)").get("response").toString());
        String url = ds.getString("@iot.selfLink").substring(0, ds.getString("@iot.selfLink").indexOf("("));
        ds.remove("name");
        ds.put("name", ds.getLong("@iot.id") + "-refined");
        ds.remove("Observations@iot.navigationLink");
        ds.remove("@iot.selfLink");
        ds.remove("@iot.id");
        ds.remove("properties");
        String refinedDsSelfLink = STAHttpClient.getInstance().doPost(ds.toString(), url).get("response").toString();
        return Long.parseLong(refinedDsSelfLink.substring(refinedDsSelfLink.indexOf("(") + 1, refinedDsSelfLink.indexOf(")")));
    }
}
