var firebase = require('firebase');
var request = require('request');

var API_KEY = "AIzaSyBYpulheAwqBszKfW3g1lVBuxbFDCfSEvg"; // Your Firebase Cloud Server API key

firebase.initializeApp({
    serviceAccount: "iBidder-2195b871ca2b.json",
    databaseURL: "https://ibidder-a1629.firebaseio.com/"
});
ref = firebase.database().ref();

//TODO: do listener stuff