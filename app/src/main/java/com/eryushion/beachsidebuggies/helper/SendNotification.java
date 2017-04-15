package com.eryushion.beachsidebuggies.helper;


import com.eryushion.beachsidebuggies.model.Constans;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by eryushion1 on 31/12/16.
 */
public class SendNotification
{
    public static void sendNotification(String toToken, String titleMessage, String bodyMessage) {
        try {
            RestAdapter adapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(new SessionRequestInterceptor())
                    .setEndpoint(Constans.FCM_SITEURL)
                    .build();
            SendNotificationAPI api = adapter.create(SendNotificationAPI.class);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", titleMessage);
            jsonObject.put("body", bodyMessage);

            JSONObject object = new JSONObject();
            object.put("to", toToken);
            object.put("priority", "high");
            object.put("notification", jsonObject);

            TypedInput input = new TypedByteArray("application/json", object.toString().getBytes("UTF-8"));

            api.sendNotification(input, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
