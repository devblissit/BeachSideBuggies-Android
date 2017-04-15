package com.eryushion.beachsidebuggies.helper;


import com.eryushion.beachsidebuggies.model.Constans;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 * Created by eryushion1 on 30/12/16.
 */
public interface SendNotificationAPI {

    @POST(Constans.FCM_SENDURL)
    void sendNotification(@Body TypedInput input, Callback<Response> response);
}
