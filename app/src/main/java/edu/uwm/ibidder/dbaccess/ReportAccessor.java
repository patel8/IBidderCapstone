package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;

import edu.uwm.ibidder.dbaccess.models.ReportModel;

/**
 * Handles the creation of user reports
 */
public class ReportAccessor extends BaseAccessor{

    /**
     * Default constructor starts with no listeners
     */
    public ReportAccessor(){
        super();
    }

    /**
     * Creates a report and returns its id.
     * @param reportToCreate The report to create
     * @return The id of the new report
     */
    public String createReport(ReportModel reportToCreate){
        DatabaseReference ref = database.getReference("reports");

        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(reportToCreate);

        return pushedRef.getKey();
    }



}
