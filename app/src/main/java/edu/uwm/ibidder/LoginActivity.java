package edu.uwm.ibidder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView forgotPassword;

    //Facebook Login Widgets and Variables
    LoginButton buttonFacebookLogin;
    CallbackManager callbackManager;
    SignInButton buttonGoogleSignIn;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(LoginActivity.this);
        setContentView(R.layout.activity_login);
        intializeAllWidgets();
        ResiterOnClickListener();

      // Check if any user is Logged in. If yes, then Go to Profile Activity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    Toast.makeText(LoginActivity.this, " User ID : "+ user.getUid() + " Name "+ user.getDisplayName()+ " Email: "+ user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {

                }
                // ...
            }
        };

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        intializeAllWidgets();
        firebaseAuth.addAuthStateListener(mAuthListener);
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
        forgotPassword = (TextView) findViewById(R.id.textForgotPassword);


        callbackManager = CallbackManager.Factory.create();
        buttonFacebookLogin = (LoginButton) findViewById(R.id.facebookLogin);


        buttonFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        buttonGoogleSignIn = (SignInButton) findViewById(R.id.googleSignInButton);

    }

    /**
     * This Method is used to register all On click Listener
     * NOTE: MAKE SURE YOU ADD WIDGET TO onClick().
     */
    public void ResiterOnClickListener(){
        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        buttonGoogleSignIn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);


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
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.textForgotPassword:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.googleSignInButton:
                GoogleSignIn();
                break;
            default:
                break;
        }
    }

    /**
     * This Method Allow user Login using Traditional Email - Password Login.
     */
    private void LoginUser(){
        // Login the user
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        //Make sure that Email/Password is Not Empty
        if(email.trim().equals("") || password.equals(""))
        {
            Toast.makeText(LoginActivity.this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loggin in! Please Wait");
        progressDialog.setTitle("Google Sign in");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            Toast.makeText(LoginActivity.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void GoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}