package edu.uwm.ibidder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intializeAllWidgets();
        ResiterOnClickListener();
    }

    @Override
    protected void onStop() {
        super.onStop();

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
            //    startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;
            default:
                Toast.makeText(this, "Unregistered Widget"+view.toString(), Toast.LENGTH_SHORT).show();
        }
    }
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
                      }
                      else{
                          Toast.makeText(getApplicationContext(), "Login failed!! Please try again", Toast.LENGTH_SHORT).show();
                          progressDialog.dismiss();
                      }
                    }
                })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "LOGIN FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

}