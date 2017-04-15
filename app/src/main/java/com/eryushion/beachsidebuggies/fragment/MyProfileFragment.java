package com.eryushion.beachsidebuggies.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eryushion.beachsidebuggies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by eryushion1 on 9/12/16.
 */
public class MyProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    TextView tvDisplayName, tvPhone, tvEmail, tvEdit;
    private ProgressDialog progressDialog;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myprofile, null);

        tvDisplayName = (TextView) view.findViewById(R.id.tvDisplayName);
        tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        tvEdit = (TextView) toolbar.findViewById(R.id.tvEdit);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            String uid = user.getUid();
            Log.d("Userid", uid);
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user value
                    String phoneNo = (String) dataSnapshot.child("phoneNumber").getValue();
                    String displayName = user.getDisplayName();
                    String email = user.getEmail();
                    tvPhone.setText(phoneNo);
                    tvDisplayName.setText(displayName);
                    tvEmail.setText(email);
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

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new EditProfileFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {

        if (fragment != null) {
            Log.d("LOADFRAGMENT", "LOADFRAGMENT");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack("back");
            transaction.replace(R.id.frameLayoutEdit, fragment).commit();
        }
    }

}
