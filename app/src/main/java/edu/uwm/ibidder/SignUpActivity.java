package edu.uwm.ibidder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    EditText userName;
    EditText name;
    EditText email;
    EditText password;
    EditText confirmPassword;
    EditText phoneNumber;
    Button signUp;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Init all Widgets
        userName = (EditText) findViewById(R.id.editTextUserName);
        name = (EditText) findViewById(R.id.editTextFirstName);
        password = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword = (EditText) findViewById(R.id.editTextConfirmPasswordSingUp);
        email = (EditText) findViewById(R.id.editTextEmail);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        signUp = (Button) findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSignUp)
        {
            // Do stuff on Sign Up
            SignUpandLoginUser();


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

        String UserName = userName.getText().toString();
        //TODO Figure out how to validate the user name before registering the user.

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in. Please wait!!");
        progressDialog.show();
        createUser(UserName, Password);
    }
    public void createUser(final String username, final String password) {

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ibidder-a1629.firebaseio.com/");

        DatabaseReference child = rootRef.child("user");
        final HashMap<String, String> map1 = new HashMap<>();
        map1.put("rsvp", "going");
        map1.put("name", username);
        map1.put("event", password);
        rootRef.push().setValue(map1);

    }
}
