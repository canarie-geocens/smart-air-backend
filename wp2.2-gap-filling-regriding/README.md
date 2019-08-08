# gap-filling-regriding
This project provides a REST endpoint for getting spatiotemporal interpolated value for a specific ObservedProperty and specific time and position (latitude and longitude).

It also provides an endpoint to the spatiotemporal interpolated grid for a specific ObservedProperty and a specific time and area.

## Request parameters
Query Parameter | Description
--- | ---
ObservedPropety | The name of the ObservedPropety we want to do interpolation time
time | The time we want to get the value for
latitude | The latitude for the position we want the value for
longitude | The longitude for the position we want the value for 


Only for `GetInterpolatedGrid`:

Query Parameter | Description
--- | ---
areaRadius | The radius (in kilometers) around the latitude/longitude that we want the grid to be calculated for
gridType | The type of the grid. Can be `hex`, `square`, or `point`
gridWidth | The width for each grid in kilometers

## How to deploy
This code can be deployed as an AWS Lambda function backed by API gateway. The endpoint for getting the value is `GetInterpolatedValue` and the one for getting the grid is `GetInterpolatedGrid`.

## Backend
This API is regridding the data from a data source on S3. The data source in S3 is the aggregated version of the data in SensorThings in GeoJSON format. Each Thing has three Datastreams, humidity, temperature, and pm25. The value for all these Datastreams are recorded as the properties of GeoJSON and the Loction of that Thing would create the geometry part. Every time the air quality device generates the data, it will be identical to one row in GeoJSON format in our S3 files.

The S3 files are categorized by their phenomenonTime in folders with structure `YYYY/MM/DD/HH.jsonl`. The bucket name is configurable in the code.

