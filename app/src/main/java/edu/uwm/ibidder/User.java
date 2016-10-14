package edu.uwm.ibidder;

/**
 * This class will represnt user information.
 * Created by Sagar on 10/13/2016.
 */

public class User {

    private String name;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;

    public User(){}

    public User(String name, String phoneNumber, String email, String firstName, String lastName) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
