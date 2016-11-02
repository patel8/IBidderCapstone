var firebase = require('firebase');
var request = require('request');

var API_KEY = "AIzaSyBYpulheAwqBszKfW3g1lVBuxbFDCfSEvg"; // Your Firebase Cloud Server API key

firebase.initializeApp({
    serviceAccount: "iBidder-2195b871ca2b.json",
    databaseURL: "https://ibidder-a1629.firebaseio.com/"
});

var GeoFire = require('geofire');
var geoFireRef = new GeoFire(firebase.database().ref("geofire"));

function sendNotificationToUser(userKey, message, onSuccess) {
    firebase.database().ref("users/" + userKey).once("value", function (snapshot) {
        var user = snapshot.val();
        var messengerId = user.messengerId;

        request({
            url: 'https://fcm.googleapis.com/fcm/send',
            method: 'POST',
            headers: {
                'Content-Type': ' application/json',
                'Authorization': 'key=' + API_KEY
            },
            body: JSON.stringify({
                data: {
                    title: message
                },
                to: messengerId
            })
        }, function (error, response, body) {
            if (error) {
                console.error(error);
            }
            else if (response.statusCode >= 400) {
                console.error('HTTP Error: ' + response.statusCode + ' - ' + response.statusMessage);
            }
            else {
                onSuccess();
            }
        });
    });
}

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

            if (item.isLocalTask)
                geoFireRef.remove(key);

            sendNotificationToUser(item.ownerId, "Your auction has finished.", function () {
                console.log("Sent message successfully.  ")
            });
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

        if (task.reportCount >= 5) {
            taskRef.remove();
            firebase.database().ref("tasks/reported/" + report.taskId).set(task);
        } else {
            taskRef.set(task);
        }

    });

});

/*
 As winners are chosen, we want to move the tasks to the accepted queue.
 */
firebase.database().ref("taskWinners").orderByChild("wasNotified").equalTo(false).on("child_added", function (snapshot) {
    var taskWinner = snapshot.val();
    taskWinner.wasNotified = true;
    firebase.database().ref("taskWinners/" + snapshot.key).set(taskWinner);

    var taskRef = firebase.database().ref("tasks/timed_out/" + taskWinner.taskId);
    taskRef.once("value", function (snapshot) {
        var task = snapshot.val();
        task.status = "ACCEPTED";
        taskRef.remove();
        firebase.database().ref("tasks/accepted/" + taskWinner.taskId).set(task);
    });
});

/*
 As review come in, we want to continue aggregating the reviews for a user.
 */
firebase.database().ref("reviews").orderByChild("wasRead").equalTo(false).on("child_added", function (snapshot) {
    var review = snapshot.val();

    var aggRef = firebase.database().ref("aggregatedReviews/" + review.userReviewedId);
    aggRef.on("value", function (snapshot) {
        var aggregatedReview = snapshot.val();
        if (aggregatedReview == null) {
            // make a new aggregated review
            aggregatedReview.reviewScore = review.reviewScore;
            aggregatedReview.totalReviews = 1;
            aggregatedReview.userId = review.userReviewedId;
            aggRef.set(aggregatedReview);
        } else {
            aggregatedReview.reviewScore = (aggregatedReview.reviewScore * aggregatedReview.totalReviews) + review.reviewScore;
            aggregatedReview.totalReviews += 1;
            aggregatedReview.reviewScore /= aggregatedReview.totalReviews;
            aggRef.set(aggregatedReview);
        }
    });

    review.wasRead = true;
    firebase.database().ref("reviews/" + snapshot.key).set(review);
});