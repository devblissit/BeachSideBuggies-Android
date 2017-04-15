package com.eryushion.beachsidebuggies.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.eryushion.beachsidebuggies.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by eryushion1 on 8/12/16.
 */
public class MoreFragment extends Fragment {

    TextView tvMyProfile, tvLogout;
    private FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more_detail, null);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        tvMyProfile = (TextView) view.findViewById(R.id.tvMyProfile);
        tvLogout = (TextView) view.findViewById(R.id.tvLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        tvMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new MyProfileFragment());
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Confirm Logout...");
                alertDialog.setMessage("Are you sure you want Logout?");
                alertDialog.setIcon(R.drawable.ic_beach);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Log.d("TAG", "onAuthStateChanged:signdOut:");
                        firebaseAuth.signOut();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        return view;
    }


    private void loadFragment(Fragment fragment) {


        if (fragment != null)
        {

            Log.d("LOADFRAGMENT", "LOADFRAGMENT");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack("back");
            transaction.replace(R.id.frameMoreDetail, fragment).commit();
        }
    }
}
