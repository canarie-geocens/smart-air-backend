# sta-qa-qc
The main purpose of this project is to get SensorThings Observation MQTT feed, clean the data based on the acceptable range for the data and store it in a new refined Datastream.

For this functionality to be enabled, we need to update the properties of the sensorthings and add the acceptable range. 
Here is an example of Datastream properties:
```javascript
"properties": {
  "valid-range": {
    "min": -50,
    "max": 50
  }
}
```
Then the sta-qa-qc would pick it up automatically, create a new refined Datastream, and add the cleaned data to that Datastream from this point of time.

This project only works on real-time data and does not clean historical data.

This project can be run as a docker container. Sample command to run the project is:
```shell
docker run -e mqtt-ssl='false' -e mqtt-server='sample-sta.sensorup.com' -e sta-server='http://sample-sta.sensorup.com/v1.0' -e sta-username='{username}' -e sta-password='{password}' -e evaluation-method='minmax' -e action-type='post-to-new-ds' -d {container name}
```
