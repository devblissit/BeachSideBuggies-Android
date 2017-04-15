package com.eryushion.beachsidebuggies.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.activity.MapsActivitys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by eryushion1 on 9/12/16.
 */
public class EditProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private EditText edtName, edtEmail, edtPhone;
    private String strName, strEmail, strPhone;
    private TextInputLayout inputName, inputEmail, inputPhone;
    RelativeLayout mainLayout;
    FirebaseUser user;
    String currentDispName, currentPhone, currentEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, null);
        mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);


        edtName = (EditText) view.findViewById(R.id.edtName);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);
        inputName = (TextInputLayout) view.findViewById(R.id.inputLastName);
        inputPhone = (TextInputLayout) view.findViewById(R.id.inputPhone);
        inputEmail = (TextInputLayout) view.findViewById(R.id.inputEmail);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception ignored) {

                }
                getActivity().onBackPressed();
            }
        });

        TextView tvDone = (TextView) toolbar.findViewById(R.id.tvDone);

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception ignored) {

                }
                userUpdate();

            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();


        if (user != null) {

            final String uid = user.getUid();
            mDatabase.child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            currentPhone = (String) dataSnapshot.child("phoneNumber").getValue();
                            currentDispName = user.getDisplayName();
                            currentEmail = user.getEmail();
                            edtName.setText(currentDispName);
                            edtPhone.setText(currentPhone);
                            edtEmail.setText(currentEmail);

                   /* mDatabase.child(uid).child("phone").setValue(strPhone);
                    mDatabase.child(uid).child("displayname").setValue(strName);*/
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();

                        }
                    });
        } else {
            progressDialog.dismiss();
        }


        return view;
    }


    private void userUpdate() {

        if (validate()) {
            progressDialog.show();
            if (!currentPhone.equals(strPhone)) {
                mDatabase.child("users").child(user.getUid()).child("phoneNumber").setValue(strPhone);
            }
            //Log.d("strName", strName);
            if (!currentDispName.equals(strName)) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(strName)
                        .build();
                assert user != null;
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("DISPLAYNAME", user.getDisplayName());
                                } else {
                                    Log.d("getException()", task.getException().getMessage());
                                }
                            }
                        });

            }
            if (!currentEmail.equals(strEmail)) {
                user.updateEmail(strEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("onComplete", "User email address updated.");
                                }
                            }
                        });
            }
            Snackbar.make(mainLayout, "Update Successfully", Snackbar.LENGTH_LONG).show();
            ((MapsActivitys) getActivity()).fragmentClassItems = "";
            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            progressDialog.dismiss();
        }


    }


    private boolean validate() {
        boolean valid = true;
        strName = edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPhone = edtPhone.getText().toString();


        if (strName.equals("")) {
            inputName.setError("Please Enter Name");
            valid = false;
        } else if (strEmail.equals("")) {
            inputEmail.setError("Please Enter Email");
            valid = false;
        } else if (strPhone.equals("")) {
            inputEmail.setError("Please Enter Phoneno");
            valid = false;
        }

        return valid;
    }

}
