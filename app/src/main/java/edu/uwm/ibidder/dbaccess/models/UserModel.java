package edu.uwm.ibidder.dbaccess.models;

/**
 * This class will represnt user information.
 * Created by Sagar on 10/13/2016.
 */

public class UserModel {

    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String userId;
    private String messengerId; //field used by messenger- you don't have to set this yourself.
    private int bidsCompleted = 0; //Number of tasks this user has finished (handled by backend)
    private int tasksCompleted = 0; //Number of tasks this user has created that were finished (handled by backend)

    public UserModel() {
    }

    /**
     * Returns true if there are no missing fields
     *
     * @return True if there ar eno missing fields.
     */
    public boolean validate() {
        return phoneNumber != null && firstName != null && lastName != null && userId != null;
    }

    public UserModel(String phoneNumber, String email, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(String messengerId) {
        this.messengerId = messengerId;
    }

    public int getBidsCompleted() {
        return bidsCompleted;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }
}
