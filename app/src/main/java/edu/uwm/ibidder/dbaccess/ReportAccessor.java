package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;

import edu.uwm.ibidder.dbaccess.listeners.ReportCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReportModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Handles the creation of user reports
 */
public class ReportAccessor extends BaseAccessor {

    /**
     * Default constructor starts with no listeners
     */
    public ReportAccessor() {
        super();
    }

    /**
     * Creates a report and returns its id.  Automatically sets the reportId field.
     *
     * @param reportToCreate The report to create
     * @return The id of the new report
     */
    public String createReport(ReportModel reportToCreate) {
        DatabaseReference ref = database.getReference("reports");

        DatabaseReference pushedRef = ref.push();
        reportToCreate.setReportId(pushedRef.getKey());
        pushedRef.setValue(reportToCreate);

        return pushedRef.getKey();
    }

    /**
     * Gets all the reports for a task.  Sends them once one-by-one to the ReportCallbackListener
     *
     * @param taskId                 The task id to get reports for
     * @param reportCallbackListener The listener to send the bids to
     */
    public void getReportsByTaskId(String taskId, final ReportCallbackListener reportCallbackListener) {
        DatabaseReference ref = database.getReference("reports");
        ref.orderByChild("taskId").equalTo(taskId).addListenerForSingleValueEvent(reportCallbackListener);
    }

    /**
     * Gets all the reports for a reporter.  Sends them once one-by-one to the ReportCallbackListener
     *
     * @param reporterId             The reporter id to get reports for
     * @param reportCallbackListener The listener to send the bids to
     */
    public void getReportsByReporterId(String reporterId, final ReportCallbackListener reportCallbackListener) {
        DatabaseReference ref = database.getReference("reports");
        ref.orderByChild("reporterId").equalTo(reporterId).addListenerForSingleValueEvent(reportCallbackListener);
    }

}
