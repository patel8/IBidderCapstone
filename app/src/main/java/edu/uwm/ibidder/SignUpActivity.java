package edu.uwm.ibidder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    /* Member variables */
    EditText email;
    EditText password;
    EditText confirmPassword;
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    Button signUp;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initWidgets();
        registerOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void registerOnClickListener(){
        signUp.setOnClickListener(this);
        firstName.setOnClickListener(this);
    }

    public void initWidgets(){
        firstName = (EditText) findViewById(R.id.editTextFirstName);
        lastName = (EditText) findViewById(R.id.editTextLastName);
        password = (EditText) findViewById(R.id.editTextPasswordSignUp);
        confirmPassword = (EditText) findViewById(R.id.editTextConfirmPasswordSignUp);
        email = (EditText) findViewById(R.id.editTextEmailSignUp);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        signUp = (Button) findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.editTextFirstName:
                Toast.makeText(SignUpActivity.this, "We know who you are.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSignUp:
                signUpUser();
                break;
            default:
                break;
        }
    }

    private boolean formFilled(String pass, String conpass, String email, String first, String last, String phone){
        //Log.i("BDH", "-----------formFilled: my_email: "+email+ " my_pw: "+ pass + " my_conpw: " + conpass + " firstname: "+first + " lastname: "+ last + " Phone: "+phone);
        return !(pass.isEmpty() || conpass.isEmpty() || email.isEmpty() || first.isEmpty() || last.isEmpty() || phone.isEmpty());
    }

    private void signUpUser(){
        String pw = "", cpw = "", my_email ="", fname = "", lname = "", phone = "";
        boolean isValid = false;

        /* Getting field data */
        if(email != null) {
            my_email = email.getText().toString();
        }
        if(password != null) {
            pw = password.getText().toString();
        }
        if(confirmPassword != null) {
            cpw = confirmPassword.getText().toString();
        }
        if(firstName != null){
            fname = firstName.getText().toString();
        }
        if(lastName != null){
            lname = lastName.getText().toString();
        }
        if(phoneNumber != null){
            phone = phoneNumber.getText().toString();
        }

        /* Validating */
        if(!formFilled(pw, cpw, my_email, fname, lname, phone)){
            Toast.makeText(SignUpActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
        } else if(!pw.equals(cpw)){
            Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
        } else if(!phone.matches("\\d{10}")){
            Toast.makeText(SignUpActivity.this, "Invalid Phone Number (###-###-####)", Toast.LENGTH_LONG).show();
        }
        else {
            isValid = true;
        }

        if(isValid) {
            Toast.makeText(SignUpActivity.this, "Successful Sign Up!", Toast.LENGTH_LONG).show();
            // TODO: Create user, put user in db, return to LoginActivity.
        }
    }

    private void SignUpandLoginUser() {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString();
        String ConfirmPassword = confirmPassword.getText().toString();

        if(!Password.equals(ConfirmPassword))
            Toast.makeText(SignUpActivity.this, "Password and Confirm password does not match", Toast.LENGTH_SHORT).show();

        String PhoneNumber = phoneNumber.getText().toString();
        //// TODO: 10/11/2016 Figure out how to validate Phone Number

        //String UserName = userName.getText().toString();
        //TODO Figure out how to validate the user name before registering the user.

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in. Please wait!!");
        progressDialog.show();
        createUser(Email, Password);
    }
    public void createUser(final String email, final String password) {

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ibidder-a1629.firebaseio.com/");

        DatabaseReference child = rootRef.child("user");
        final HashMap<String, String> map1 = new HashMap<>();
        map1.put("rsvp", "going");
        map1.put("email", email);
        map1.put("event", password);
        rootRef.push().setValue(map1);

    }
}
