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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextForgotPassword;
    Button buttonForgotPassword;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerWidgets();
        RegisterListeners();
    }

    private void registerWidgets() {
        editTextForgotPassword = (EditText) findViewById(R.id.editTextEmailForgotPassword);
        buttonForgotPassword = (Button) findViewById(R.id.buttonForgotPassword);
    }

    private void RegisterListeners(){
        buttonForgotPassword.setOnClickListener(this);
    }
    public void onClick(View view)
    {
        if(view.getId()== R.id.buttonForgotPassword)
        {
            PasswordReset();
        }
    }

    private void PasswordReset() {
        String email = editTextForgotPassword.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
         progressBar = new ProgressDialog(this);
        progressBar.setMessage("Please Wait!");
        progressBar.setTitle("RESET PASSWORD");
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.dismiss();
                    }
                });
    }
}
