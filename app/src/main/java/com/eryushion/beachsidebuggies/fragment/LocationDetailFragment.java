package com.eryushion.beachsidebuggies.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.activity.MapsActivitys;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("ValidFragment")
public class LocationDetailFragment extends Fragment implements OnMapReadyCallback {


    private Place mPlace;
    String from = "";
    String TAG = "PLACES ";
    public GoogleMap gMap;
    LatLng latLng;

    public LocationDetailFragment(Place place, String from) {
        this.mPlace = place;
        this.from = from;
    }

    private MapsActivitys activity() {
        return (MapsActivitys) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_location_detail, null);

        Button btnTakeMe = (Button) view.findViewById(R.id.btnTakeMe);
        TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        TextView tvWebsite = (TextView) view.findViewById(R.id.tvWebsite);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);


        if (from.equals("pickup"))
        {
            btnTakeMe.setText("Pick me up here");
        } else {
            btnTakeMe.setText("Take me here");
        }
        if (mPlace != null)
        {
            if (mPlace.getAddress() != null) {
                tvAddress.setText(mPlace.getAddress().toString());
            }
            if (mPlace.getWebsiteUri() != null) {
                tvWebsite.setText(String.valueOf(mPlace.getWebsiteUri()));
            }
            if (mPlace.getPhoneNumber() != null) {
                tvPhone.setText(mPlace.getPhoneNumber().toString());
            }

            MapView mapView = (MapView) view.findViewById(R.id.map_detail);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
            latLng = mPlace.getLatLng();

            Log.d(TAG + "PLACE_NAME", String.valueOf(mPlace.getName()));
            Log.d(TAG + "PLACE_ADDRESS", String.valueOf(mPlace.getAddress()));
            Log.d(TAG + "PLACE_WEBSITE", String.valueOf(mPlace.getWebsiteUri()));
            Log.d(TAG + "PLACE_RATING", String.valueOf(mPlace.getRating()));
        }
        btnTakeMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (from.equals("pickup"))
                {
                    activity().pickUpPlace = mPlace;
                    if (activity().addresses != null) {
                        activity().addresses.clear();
                    }
                    activity().tvPickup.setText(mPlace.getName().toString());
                    activity().pickUpLocation(mPlace);
                    activity().latlngPickup = latLng;


                } else {
                    activity().dropOffPlace = mPlace;
                    activity().dropoffMarker(mPlace);
                    activity().tvDropOff.setText(mPlace.getName().toString());
                }
                activity().fragmentClassItems = "";

            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mPlace.getName().toString()));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

}
