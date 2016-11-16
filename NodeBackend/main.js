var firebase = require('firebase');
var request = require('request');

var API_KEY = "AIzaSyBYpulheAwqBszKfW3g1lVBuxbFDCfSEvg"; // Your Firebase Cloud Server API key

firebase.initializeApp({
    serviceAccount: "iBidder-2195b871ca2b.json",
    databaseURL: "https://ibidder-a1629.firebaseio.com/"
});

var GeoFire = require('geofire');
var geoFireRef = new GeoFire(firebase.database().ref("geofire"));

function sendNotificationToUser(userKey, message, onSuccess, taskModel) {
    firebase.database().ref("users/" + userKey).once("value", function (snapshot) {
        var user = snapshot.val();
        var messengerId = user.messengerId;

        if (taskModel == null)
            taskModel = {
                taskId: null,
                taskStatus: null
            };

        request({
            url: 'https://fcm.googleapis.com/fcm/send',
            method: 'POST',
            headers: {
                'Content-Type': ' application/json',
                'Authorization': 'key=' + API_KEY
            },
            body: JSON.stringify({
                data: {
                    title: message,
                    taskId: taskModel.taskId,
                    taskStatus: taskModel.status
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

function deleteAllBidsOnTask(taskId, message) {
    firebase.database().ref("bids").orderByChild("taskId").equalTo(taskId).once("child_added", function (snapshot) {
        var bid = snapshot.val();

        firebase.database().ref("bids/" + snapshot.key).remove();

        sendNotificationToUser(bid.bidderId, message, function () {
            console.log("Send bid removal message successfully");
        });

    });
}

/*
 Check every 10 seconds to update task statuses.  This also notifies users when an auction finishes.
 */
setInterval(function () {

    var ref = firebase.database().ref("tasks/ready");

    ref.orderByChild("expirationTime").endAt(Math.floor((new Date).getTime() / 1000)).once("value", function (snapshot) {
        var data = snapshot.val();
        for (var key in data) {
            var item = data[key];
            taskToTimeout(item);
        }
    });

    ref.orderByChild("isTaskItNow").equalTo(true).once("value", function (snapshot) {
        var data = snapshot.val();

        for (var key in data) {
            var item = data[key];

            firebase.database().ref("bids").orderByChild("taskId").equalTo(item.taskId).once("value", function (snapshot) {
                var data = snapshot.val();

                if (Object.keys(data).length > 0) {
                    var firstBid = data[Object.keys(data)[0]];
                    taskToTimeout(firstBid.taskId);
                }
            });
        }
    });

}, 10 * 1000);

function taskToTimeout(item) {
    item.status = "TIMED_OUT";

    var itemEntry = firebase.database().ref("tasks/timed_out/" + key);
    itemEntry.set(item);

    var itemRemoval = firebase.database().ref("tasks/ready/" + key);
    itemRemoval.remove();

    if (item.isLocalTask)
        geoFireRef.remove(key);

    sendNotificationToUser(item.ownerId, "Your auction has finished.", function () {
        console.log("Sent auction completion message successfully.  ")
    }, item);
}

/*
 Monitor reports and update tasks and bids as necessary.
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
            deleteAllBidsOnTask(snapshot.key, "A task you have bid on has been removed due to excessive reports.");
        } else {
            taskRef.set(task);
        }

    });

});

/*
 As winners are chosen, we want to move the tasks to the accepted queue.  This also deletes the non-winner bids.
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

        sendNotificationToUser(taskWinner.winnerId, "You are the winner of a task.  ", function () {
            console.log("Sent winner selected message successfully.  ")
        }, task);

        firebase.database().ref("bids").orderByChild("taskId").equalTo(snapshot.key).once("child_added", function (snapshot) {
            var bid = snapshot.val();

            if (bid.bidderId != taskWinner.winnerId) {
                firebase.database().ref("bids/" + snapshot.key).remove();
            }
        });
    });
});

/*
 As review come in, we want to continue aggregating the reviews for a user.
 */
firebase.database().ref("reviews").orderByChild("wasRead").equalTo(false).on("child_added", function (snapshot) {
    var review = snapshot.val();

    var aggRef = firebase.database().ref("aggregatedReviews/" + review.userReviewedId);
    aggRef.once("value", function (snapshot) {
        var aggregatedReview = snapshot.val();
        if (aggregatedReview == null) {
            // make a new aggregated review
            aggregatedReview = {
                reviewScore: review.reviewScore,
                totalReviews: 1,
                userId: review.userReviewedId
            };

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

/*
 If a task is deleted, we want to remove it and all of its bids.  This also sends notifications to bidders.
 */
firebase.database().ref("tasks/ready").orderByChild("wasDeleted").equalTo(true).on("child_added", function (snapshot) {
    var task = snapshot.val();
    firebase.database().ref("tasks/ready/" + snapshot.key).remove();

    deleteAllBidsOnTask(snapshot.key, "A task you have bid on has been deleted.");
});

/*
 If a task is marked completed, we want to move it to the completed queue and notify the task doer.
 */
firebase.database().ref("tasksCompleted").orderByChild("wasRead").equalTo(false).on("child_added", function (snapshot) {
    var completed = snapshot.val();
    completed.wasRead = true;
    firebase.database().ref("tasksCompleted/" + snapshot.key).set(completed);

    var taskRef = firebase.database().ref("tasks/accepted/" + snapshot.key);
    taskRef.once("value", function (snapshot) {
        var task = snapshot.val();
        task.status = "FINISHED";

        taskRef.remove();

        firebase.database().ref("tasks/finished/" + snapshot.key).set(task);

        firebase.database().ref("taskWinners/" + snapshot.key).once("value", function (snapshot) {
            var taskWinner = snapshot.val();

            sendNotificationToUser(taskWinner.winnerId, "A task you were assigned has been marked finished.  ", function () {
                console.log("Sent task finished message successfully.  ")
            }, task);
        })

    });

});