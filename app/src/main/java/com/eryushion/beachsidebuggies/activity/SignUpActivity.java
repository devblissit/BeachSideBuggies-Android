package com.eryushion.beachsidebuggies.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.model.Constans;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignUpActivity extends AppCompatActivity {

    String TAG = "SIGNUP";
    private EditText edtFirstName, edtLastName, edtEmail, edtPhone, edtPassword, edtConfPassword;
    private TextInputLayout inputFirstName, inputLastName, inputEmail, inputPhone, inputPassword, inputConfPassword;
    private String strFirstName = "", strLastName = "", strEmail = "", strPhone = "", strPassword = "", strConfPassword = "";
    private Button btnCreate;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mfbAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    ScrollView scrollView;
    //private FirebaseDatabase mFirebaseInstance;
    CallbackManager mCallbackManager;
    String fcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        mfbAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfPassword = (EditText) findViewById(R.id.edtConfPassword);

        inputFirstName = (TextInputLayout) findViewById(R.id.inputFirstName);
        inputLastName = (TextInputLayout) findViewById(R.id.inputLastName);
        inputPhone = (TextInputLayout) findViewById(R.id.inputPhone);
        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);
        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);
        inputConfPassword = (TextInputLayout) findViewById(R.id.inputConfPassword);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("fcmToken", fcmToken);
        progressDialog = new ProgressDialog(this);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    registerUser();
                }
            }
        });


        mCallbackManager = CallbackManager.Factory.create();

        Button fbLogin = (Button) findViewById(R.id.button_fb_login);

        final LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {


                    progressDialog.dismiss();
                    Snackbar.make(scrollView, "Successfully registered", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, MapsActivitys.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mfbAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mfbAuth.removeAuthStateListener(mAuthListener);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mfbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {

                            Snackbar.make(scrollView, "Authentication Failed.", Snackbar.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }

                    }
                });

    }

    /* @Override
     public void onStart() {
         super.onStart();
         mAuth.addAuthStateListener(mAuthListener);
     }

     @Override
     public void onStop() {
         super.onStop();
         if (mAuthListener != null) {
             mAuth.removeAuthStateListener(mAuthListener);
         }
     }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CALL", "onDestroy");
    }


    private void registerUser() {
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                String displayName = strFirstName + " " + strLastName;
                                Log.d("DISPLAYNAME", displayName);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                        .Builder()
                                        .setDisplayName(displayName)
                                        .build();
                                assert user != null;
                                user.updateProfile(profileUpdates);
                                Log.d("DISPLAYNAME", user.getDisplayName());

                                DatabaseReference reference = mDatabase.child("users").child(user.getUid());
                                reference.child("phoneNumber").setValue(strPhone);
                                if (!fcmToken.equals("")) {
                                    reference.child("fcmToken").setValue(fcmToken);
                                }
                                // mDatabase.child("users").child(user.getUid()).child("displayname").setValue(displayName);
                                Snackbar.make(scrollView, "Successfully registered", Snackbar.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this, MapsActivitys.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                Snackbar.make(scrollView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                        }
                        //checking if success
                        progressDialog.dismiss();
                    }
                });
    }


    private boolean validate() {
        boolean valid = true;
        strFirstName = edtFirstName.getText().toString();
        strLastName = edtLastName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPhone = edtPhone.getText().toString();
        strPassword = edtPassword.getText().toString();
        strConfPassword = edtConfPassword.getText().toString();

        if (strFirstName.equals("")) {
            inputFirstName.setError("Please Enter First Name");
            valid = false;
        } else if (strEmail.equals("")) {
            inputEmail.setError("Please Enter Email");
            valid = false;
        } else if (strPhone.equals("") || strPhone.length() != 10) {
            inputPhone.setError("Please Enter Valid Phone Number");
            valid = false;
        } else if (strPassword.equals("")) {
            inputPassword.setError("Please Enter Password");
            valid = false;
        } else if (strConfPassword.equals("") && !strConfPassword.equals(strPassword)) {
            inputConfPassword.setError("Passwords Do Not Match");
            valid = false;
        }
        return valid;
    }
}
