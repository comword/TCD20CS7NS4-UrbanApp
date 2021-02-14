'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
const googleapis = require('googleapis');
const ml = googleapis.google.ml('v1');
const db = admin.database()

exports.recordupdate = functions.database.ref('/real-records/{recid}').onCreate(
  async (snapshot, context) => {
    
    const key = snapshot.key;
    const record = snapshot.val();
    console.log('recordId', context.params.recid, record);
    if (record.activityID !== undefined) {
      const snapshot = await db.ref('/real-activity/'+key).once('value');
      const value = snapshot.val();
      console.log("Access activity", value);
    }
    if (record.locationId !== undefined) {
      const snapshot = await db.ref('/real-locations/'+key).once('value');
      const value = snapshot.val();
      console.log("Access location", value);
    }
    // read latest datas
    let db_access = await db.ref('/real-latest/activity').once('value');
    let str_activity = db_access.val().activity;
    let val_activity = 2
    // map string to cate in ml model
    switch (str_activity) {
      case "VEHICLE":
        val_activity = 0;
        break;
      case "BICYCLE":
        val_activity = 1;
        break;
      case "WALKING":
        val_activity = 2;
        break;
      case "RUNNING":
        val_activity = 3;
        break;
      default:
        val_activity = 2;
    }
    db_access = await db.ref('/real-latest/location').once('value');
    let val_speed = db_access.val().speed;

    db_access = await db.ref('/open-data/AQIData-latest').once('value');
    let val_aqi = db_access.val().regions["2"]["_aqih"];

    db_access = await db.ref('/open-data/DublinToday-latest').once('value');
    let val_rainfall = db_access.val()["0"]["Rainfall"];
    val_rainfall = parseFloat(val_rainfall);
    let val_hour = new Date().getHours();
    
    const instances = [[val_speed, val_activity, val_aqi, val_rainfall, val_hour]]
    console.log("ML instance", instances)
    const { credential } = await googleapis.google.auth.getApplicationDefault();
    const modelName = `projects/tcdcs-2020/models/uc_senselight`;

    const preds = await ml.projects.predict({
      auth: credential, 
      name: modelName,  
      requestBody: {
        instances
      }
    });
    db.ref('/fusion/'+key).set(preds.data['predictions'])
});

exports.predictNeedLight = functions.https.onRequest(
  async(request,response) => {
    const val_speed = request.body.speed;
    const val_activity = request.body.activity;
    const val_aqi = request.body.aqi;
    const val_rainfall = request.body.rainfall;
    const val_hour = request.body.hour;

    const instances = [[val_speed, val_activity, val_aqi, val_rainfall, val_hour]]

    const { credential } = await googleapis.google.auth.getApplicationDefault();
    const modelName = `projects/tcdcs-2020/models/uc_senselight`;

    const preds = await ml.projects.predict({
      auth: credential, 
      name: modelName,  
      requestBody: {
        instances
      }
    });
    // console.log(preds);
    response.status(200).json(preds.data['predictions']);
  }
);