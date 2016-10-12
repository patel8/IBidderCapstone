package edu.uwm.ibidder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextForgotPassword;
    Button buttonForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


    }

    @Override
    protected void onStart() {
        super.onStart();
        registerWidgets();
    }

    private void registerWidgets() {
        editTextForgotPassword = (EditText) findViewById(R.id.editTextEmailForgotPassword);
        buttonForgotPassword = (Button) findViewById(R.id.buttonForgotPassword);
    }

    public void onClick(View view)
    {
        if(view.getId()== R.id.buttonForgotPassword)
        {
            // Do password Reset using Email Address
        }
    }
}
