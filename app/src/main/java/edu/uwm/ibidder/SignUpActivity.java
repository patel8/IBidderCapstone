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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class SignUpActivity extends AppCompatActivity{

    EditText userName;
    EditText firstName;
    EditText lastName;
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
        initializeAllWidgets();
    }

    private void initializeAllWidgets() {
        firstName = (EditText) findViewById(R.id.editTextFirstName);
        lastName = (EditText) findViewById(R.id.editTextLastName);
        password = (EditText) findViewById(R.id.editTextPasswordSignUp);
        confirmPassword = (EditText) findViewById(R.id.editTextConfirmPasswordSignUp);
        email = (EditText) findViewById(R.id.editTextEmailSignUp);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        signUp = (Button) findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpandLoginUser();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Init all Widgets

    }

    private void SignUpandLoginUser() {

        String Email = email.getText().toString().trim();
        String Password = password.getText().toString();
        String ConfirmPassword = confirmPassword.getText().toString();

        if(!Password.equals(ConfirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Password and Confirm password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String PhoneNumber = phoneNumber.getText().toString();
        //// TODO: 10/11/2016 Figure out how to validate Phone Number

        SignUpUser(Email, Password);
    }

    private void LoginUser(final String email, final String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Navigate user to Home Screen
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    UserModel userModelInfo = new UserModel();
                    userModelInfo.setEmail(email);
                    //TODO: Don't re-read these from the UI in case the user changes them while sign-up is processing
                    userModelInfo.setFirstName(firstName.getText().toString());
                    userModelInfo.setPhoneNumber(phoneNumber.getText().toString());
                    userModelInfo.setFirstName(firstName.getText().toString());
                    userModelInfo.setLastName(lastName.getText().toString());

                    /**
                     * Todo - Call Method in DB class to Register user with userModelInfo object. This method should return true/ false. True if successful else false.
                     */
                    UserAccessor userAccessor = new UserAccessor();
                    if(userAccessor.createUser(userModelInfo))
                        startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                    progressDialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login failed!! From SiGN Up", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in. Please wait!!");
        progressDialog.show();
    }

    private void SignUpUser(final String email, final String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "New User Has been Created." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            LoginUser(email, password);
                        } else {
                            Toast.makeText(SignUpActivity.this, "ERROR CREATING SIGN UP." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
