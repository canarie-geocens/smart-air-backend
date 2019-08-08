
const _ = require('lodash');
const AWS = require('aws-sdk');
const s3 = new AWS.S3({ region: 'us-west-2' });
const simpleStatistics = require('simple-statistics');
const turfInterpolate = require('@turf/interpolate').default;
const turfNearestPoint = require('@turf/nearest-point').default;
const turfPointsInPolygon = require('@turf/points-within-polygon').default;
const turfCircle = require('@turf/circle').default;

const turfHelper = require('@turf/helpers');

const zlib = require('zlib');


exports.handler = (event, context, callback) => {

    const requestPath = event.path;
    const requestparams = event.queryStringParameters;

    var gridWidth, gridType;
    var targetLatitude, targetLongitude;

    var interpolationAccuracyInKilometer = 50;

    const targetProperty = requestparams.ObservedProperty;
    const targetTime = requestparams.time;
    targetLatitude = parseFloat(requestparams.latitude);
    targetLongitude = parseFloat(requestparams.longitude);

    console.log(requestPath);
    console.log(JSON.stringify(requestparams));

    if(requestPath == '/GetInterpolatedValue'){
        gridWidth = 2;
        gridType = 'point'
        
    } else if (requestPath == '/GetInterpolatedGrid'){
        interpolationAccuracyInKilometer = requestparams.areaRadius;
        gridWidth = requestparams.gridWidth;
        gridType = requestparams.gridType;
    }

    console.log(gridType);

    readS3(targetTime).then((data) => {
        console.log("S3 finished");
        // console.log(data);

        zlib.gunzip(data.Body, function(err, unzipped) {
            console.log("unzipp ended");
            var unzippedArray = unzipped.toString().split('\n');
            var jsonArrayData = [];
            unzippedArray.forEach(function(jsonString){
                try{
                    jsonArrayData.push(JSON.parse(jsonString));
                } catch(err){
                    console.log(jsonString);
                }
            });

            console.log("splited");
        // data = data.Body.pipe(zlib.createGunzip());



            var responseBody;
            var temporalInterpolated = getTemporalInterpolatedData(jsonArrayData,targetTime,targetProperty, targetLongitude, targetLatitude, interpolationAccuracyInKilometer);
            var spatialInterpolatedGrid = getSpatialInterpolatedGrid(temporalInterpolated, targetProperty, gridWidth, gridType);
            if(requestPath == '/GetInterpolatedValue'){
                var targetPropertyValue = findTargetValueInGrid (spatialInterpolatedGrid, targetLongitude, targetLatitude, targetProperty);
                responseBody = turfHelper.point([targetLongitude, targetLatitude]);
                responseBody.properties[targetProperty]=targetPropertyValue;
                responseBody.properties.time = targetTime;
            } else if (requestPath == '/GetInterpolatedGrid'){
                responseBody = spatialInterpolatedGrid;
            }

            callback(null, {
                statusCode: 200,
                body: JSON.stringify(responseBody),
                headers: {
                    'Access-Control-Allow-Origin': '*',
                }
            });
        });
    }).catch((err) => {
        callback(null, {
            statusCode: 400,
            body: JSON.stringify({
                msg: 'Failed to calculate interpolated value!',
                casedBy: JSON.stringify(err)
            }),
            headers: {
                'Access-Control-Allow-Origin': '*',
            }
        });
    });
}

function readS3(targetTime){
    var date;
    if(!targetTime.includes("/")){
        date = new Date(Date.parse(targetTime));
    } else {
        date = new Date(Date.parse(targetTime.split('/')[0]));
    }

    var bucketName = date.toISOString().split(':')[0].replace('T','/').replace('-','/').replace('-','/').replace('-','/')+'.jsonl.gz';

    console.log(bucketName);

    var params = {
      Bucket: "refined-dataset-canada-geocens-aq-sta",
      Key: bucketName
     };

    return s3.getObject(params).promise();
}

function getTemporalInterpolatedData(arrayData, targetTime, targetProperty, targetLongitude, targetLatitude, interpolationAccuracyInKilometer){
    var temporalInterpolatedFeatureCollection = {
        type : "FeatureCollection",
	    features : []
    };
    var groupedArray = _.groupBy(arrayData,'geometry.coordinates');
    _.forEach(groupedArray, function(value, key) {
        var feature = {
            type : "Feature",
            geometry : {
			    type : value[0].geometry.type,
			    coordinates : value[0].geometry.coordinates
		    },
		    properties : {}
        };
        var timeSeries = [];
        _.forEach(value, function(featureProperties){
            if(featureProperties.properties[targetProperty]!== undefined){
                if(!targetTime.includes("/")){
                    timeSeries.push([new Date(Date.parse(featureProperties.properties.time)).getTime(),featureProperties.properties[targetProperty]]);
                } else {
                    var startTime = new Date(Date.parse(targetTime.split('/')[0])).getTime();
                    var endTime = new Date(Date.parse(targetTime.split('/')[1])).getTime();
                    var dataTime = new Date(Date.parse(featureProperties.properties.time)).getTime();
                    if(dataTime>= startTime && dataTime <= endTime){
                        timeSeries.push(featureProperties.properties[targetProperty]);
                    }
                }
            }
        });
        if(timeSeries.length >0 ){
            if(!targetTime.includes("/")){
                var interpolatedLine = simpleStatistics.linearRegressionLine(simpleStatistics.linearRegression(timeSeries));
                feature.properties[targetProperty] = interpolatedLine(new Date(Date.parse(targetTime)).getTime());
            } else {
                feature.properties[targetProperty] = simpleStatistics.mean(timeSeries);
            }
            feature.properties.time = targetTime;
        } else {
            feature.properties[targetProperty] = '';
            feature.properties.time = targetTime;
        }
        temporalInterpolatedFeatureCollection.features.push(feature);
    });
    var result=[];
    do{
        var circle = turfCircle([targetLongitude,targetLatitude], interpolationAccuracyInKilometer, {steps: 100, units: 'kilometers'});
        result = turfPointsInPolygon(temporalInterpolatedFeatureCollection, circle);
        interpolationAccuracyInKilometer = 2 * interpolationAccuracyInKilometer;
    } while (result.features.length <2);
    return result;
}


function getSpatialInterpolatedGrid(temporalInterpolatedData, targetProperty, gridWidthInKilometers, gridType){
    var options = {gridType: gridType, property: targetProperty, units: 'kilometers'};
    var interpolatedGrid = turfInterpolate(temporalInterpolatedData, gridWidthInKilometers, options);
    return interpolatedGrid;
}

function findTargetValueInGrid(grid, longitude, latitude, targetProperty){
    var interpolatedTargetValue = turfNearestPoint([longitude,latitude],grid);
    return interpolatedTargetValue.properties[targetProperty];
}
