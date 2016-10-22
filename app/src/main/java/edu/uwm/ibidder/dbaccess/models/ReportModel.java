package edu.uwm.ibidder.dbaccess.models;

/**
 * Represents a report for a task (as-in, reporting bad activity)
 */
public class ReportModel {

    private String taskId;
    private String description;
    private String reporterId;
    private String reportId;

    /**
     * Default constructor for firebase
     */
    public ReportModel() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
