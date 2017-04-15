package com.eryushion.beachsidebuggies.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.eryushion.beachsidebuggies.model.Constans;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by eryushion1 on 29/12/16.
 */
public class FCMToken extends FirebaseInstanceIdService {
    String TAG = "FCMToken";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed_token: " + refreshedToken);

        sharedPreferences = getSharedPreferences(Constans.SHARDPREF_FILENAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Constans.TOKEN_KEY, refreshedToken);
        editor.apply();

    }
}
