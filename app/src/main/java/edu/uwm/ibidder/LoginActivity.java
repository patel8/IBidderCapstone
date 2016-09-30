package edu.uwm.ibidder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    //Facebook Login Widgets and Variables
    LoginButton buttonFacebookLogin;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        intializeAllWidgets();
        ResiterOnClickListener();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        intializeAllWidgets();
    }

    // All private Helper Methods

    /**
     * Initialize All Wigets
     */
    public void intializeAllWidgets() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);


        buttonFacebookLogin = (LoginButton) findViewById(R.id.facebookLogin);
        callbackManager = CallbackManager.Factory.create();

        buttonFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

    }

    /**
     * This Method is used to register all On click Listener
     * NOTE: MAKE SURE YOU ADD WIDGET TO onClick().
     */
    public void ResiterOnClickListener(){
        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }
    // Register All On Click Listener
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.buttonLogin:
                LoginUser();
                break;
            case R.id.textViewSignUp:
                // // TODO: 9/28/2016  User have clicked on Generic Sign Up Button. 
                break;
            default:
                Toast.makeText(this, "Unregistered Widget"+view.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This Method Allow user Login using Traditional Email - Password Login.
     */
    private void LoginUser(){
        // Login the user
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in. Please wait!!");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          //Navigate user to Home Screen
                          Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                          progressDialog.dismiss();
                          startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                      }
                      else{
                          Toast.makeText(getApplicationContext(), "Login failed!! Please try again", Toast.LENGTH_SHORT).show();
                          progressDialog.dismiss();
                      }
                    }
                });
    }

}