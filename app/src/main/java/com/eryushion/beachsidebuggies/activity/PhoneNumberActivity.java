package com.eryushion.beachsidebuggies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eryushion.beachsidebuggies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneNumberActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mfirebaseAuth;
    String firebaseUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        final EditText edtPhone = (EditText) findViewById(R.id.edtPhone);
        final TextInputLayout inputPhone = (TextInputLayout) findViewById(R.id.inputPhone);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

        mfirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mfirebaseAuth.getCurrentUser();
        if (user != null) {
            firebaseUserId = user.getUid();
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNo = edtPhone.getText().toString();
                    if (phoneNo.equals("") || phoneNo.length() != 10) {
                        inputPhone.setError("Please Enter A Valid Phone Number");
                    } else {
                        mDatabase.child("users").child(firebaseUserId).child("phoneNumber").setValue(phoneNo);
                        Intent intent = new Intent(PhoneNumberActivity.this, MapsActivitys.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            });
        } else {
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
