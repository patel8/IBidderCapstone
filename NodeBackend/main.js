var firebase = require('firebase');
var request = require('request');

var API_KEY = "AIzaSyBYpulheAwqBszKfW3g1lVBuxbFDCfSEvg"; // Your Firebase Cloud Server API key

firebase.initializeApp({
    serviceAccount: "iBidder-2195b871ca2b.json",
    databaseURL: "https://ibidder-a1629.firebaseio.com/"
});

//TODO: do listener stuff
/*
 Check every minute to update task statuses.
 */
setInterval(function () {

    var ref = firebase.database().ref("tasks/ready");

    ref.orderByChild("expirationTime").endAt(Math.floor((new Date).getTime() / 1000)).once("value", function (snapshot) {
        var data = snapshot.val();
        for (var key in data) {
            var item = data[key];
            item.status = "TIMED_OUT";

            var itemEntry = firebase.database().ref("tasks/timed_out/" + key);
            itemEntry.set(item);

            var itemRemoval = firebase.database().ref("tasks/ready/" + key);
            itemRemoval.remove();
        }
    });

}, 60 * 1000);

/*
 Monitor reports and update tasks as necessary
 */
firebase.database().ref("reports").orderByChild("wasRead").equalTo(false).on("child_added", function (snapshot) {

    var report = snapshot.val();
    report.wasRead = true;
    firebase.database().ref("reports/" + snapshot.key).set(report);

    var taskRef = firebase.database().ref("tasks/ready/" + report.taskId);
    taskRef.once("value", function (snapshot) {
        var task = snapshot.val();
        task.reportCount = task.reportCount + 1;

        if(task.reportCount >= 5){
            taskRef.remove();
            firebase.database().ref("tasks/reported/" + report.taskId).set(task);
        } else {
            taskRef.set(task);
        }

    });

});