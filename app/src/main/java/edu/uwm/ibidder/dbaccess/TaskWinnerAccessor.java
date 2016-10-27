package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.dbaccess.models.TaskWinnerModel;

/**
 * Handles firebase interactions for task winners
 */
public class TaskWinnerAccessor extends BaseAccessor {

    TaskWinnerAccessor() {
        super();
    }

    /**
     * Used to pick the winner of the task.  Also kicks off the moving of the task to the "accepted" state.
     *
     * @param taskWinnerModel The model for the task winner
     */
    public void CreateTaskWinner(TaskWinnerModel taskWinnerModel) {
        DatabaseReference ref = database.getReference("taskWinners/");
        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(taskWinnerModel);
    }

    /**
     * Returns a query to get TaskWinner objects by a winner's user id
     *
     * @param winnerId the userId of the winner
     */
    public Query GetTaskWinnersByWinnerIdQuery(String winnerId) {
        DatabaseReference ref = database.getReference("taskWinners/");
        return ref.orderByChild("winnerId").equalTo(winnerId);
    }

}
