package com.eryushion.beachsidebuggies.helper;


import com.eryushion.beachsidebuggies.model.Constans;

import retrofit.RequestInterceptor;

/**
 * Created by eryushion1 on 4/7/16.
 */
public class SessionRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {

        request.addHeader("Authorization", "Key=" + Constans.FCM_SERVERKEY);
        request.addHeader("Accept", "application/json");
    }
}
