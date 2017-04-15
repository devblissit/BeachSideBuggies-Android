package com.eryushion.beachsidebuggies.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.activity.MapsActivitys;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class SearchLocationFragment extends Fragment {
    PopularLocationAdapter popularLocationAdapter;
    ArrayList<LocationModel> locationList;
    String from = "";

    public SearchLocationFragment() {

    }

    public SearchLocationFragment(String from) {
        this.from = from;
    }

    private MapsActivitys activity() {
        return (MapsActivitys) getActivity();
    }

    @Nullable
    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_location, null);

        SupportPlaceAutocompleteFragment autocompleteFragment1 = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("RESULTPLACE", String.valueOf(place.getName()));

                if (from.equals("pickup")) {
                    activity().pickUpPlace = place;
                    LatLng pickupPlaceLatlng;
                    if (activity().mMap != null) {
                        try {
                            pickupPlaceLatlng = place.getLatLng();
                            activity().latlngPickup = pickupPlaceLatlng;
                            activity().pickUpLocation(place);
                            activity().tvPickup.setText(place.getName());
                            activity().addresses.clear();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    activity().dropOffPlace = place;
                    activity().dropoffMarker(place);
                    activity().tvDropOff.setText(place.getName());
                }
                activity().fragmentClassItems = "";
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        locationList = new ArrayList<>();
        addPopularLocation();

        popularLocationAdapter = new PopularLocationAdapter(getActivity(), locationList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(popularLocationAdapter);


        return view;
    }

    public void addPopularLocation() {
        LocationModel model = new LocationModel("V Pizza", "ChIJXy-J3QZJ5IgROoWMjCrN12E");
        locationList.add(model);
        model = new LocationModel("Flask & Cannon", "ChIJsTpp3QZJ5IgRPDlZ6An6aiQ");
        locationList.add(model);
        model = new LocationModel("The Pier Cantina and Sandbar", "ChIJje_3LgdJ5IgRSyWAC9OzCNQ");
        locationList.add(model);
        model = new LocationModel("1st Street Loft Art and Coffee House", "ChIJ52rv3wZJ5IgRpINEUtSphl0");
        locationList.add(model);
        model = new LocationModel("Flying Iguana", "ChIJF9ZhkDpJ5IgRLnBVM3JHfak");
        locationList.add(model);
        model = new LocationModel("Al's Pizza", "ChIJzVjAgzpJ5IgRx0MapX7YMYs");
        locationList.add(model);
        model = new LocationModel("Sliders Seafood Grille", "ChIJWZWuwDpJ5IgRp6FCx9zZ0yU");
        locationList.add(model);
        model = new LocationModel("Whit's Frozen Custard", "ChIJvQ6jCjBJ5IgRW0k13HmRzXM");
        locationList.add(model);
        model = new LocationModel("Salt Life Food Shack", "ChIJN2iSkwVJ5IgRRzxRDSXEkzM");
        locationList.add(model);
        model = new LocationModel("Mezza Restaurant & Bar", "ChIJfzBavDpJ5IgRrAcXcg46Sy4");
        locationList.add(model);
        model = new LocationModel("Lynch's Irish Pub", "ChIJ565E3gZJ5IgR-7lls0gIf6Y");
        locationList.add(model);
        model = new LocationModel("Monkey's Uncle", "ChIJCVvEPLRJ5IgRCHMihtzH4kU");
        locationList.add(model);

        model = new LocationModel("Courtyard By Marriott", "ChIJsVSG4xpJ5IgRJkKdvjTSzms");
        locationList.add(model);
        model = new LocationModel("Hampton Inn Jacksonville Beach", "ChIJ6cwDKBtJ5IgRn_XZ7BgMURc");
        locationList.add(model);
        model = new LocationModel("Fairfield Inn & Suites Jacksonville Beach", "ChIJvZnF5hpJ5IgRSntgtqDsp1k");
        locationList.add(model);


    }

    public class PopularLocationAdapter extends RecyclerView.Adapter<PopularLocationAdapter.MyViewHolder> {

        private Context mContext;
        ArrayList<LocationModel> list;

        PopularLocationAdapter(Context context, ArrayList<LocationModel> locationList) {
            this.mContext = context;
            this.list = locationList;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_popularlocation, parent, false);

            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final LocationModel model = list.get(position);
            holder.tvLocation.setText(model.getPlaceName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Places.GeoDataApi.getPlaceById(activity().mGoogleApiClient, model.getPlaceId())
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        Place myPlace = places.get(0);
                                        Log.d("Place_found: ", String.valueOf(myPlace.getName()));
                                        loadFragment(new LocationDetailFragment(myPlace, from));
                                    } else {
                                        Log.e("Place not found", "NOTFOUND");
                                    }
                                    // places.release();
                                }
                            });


                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvLocation;

            MyViewHolder(View view) {
                super(view);
                tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            }
        }
    }


    public class LocationModel {

        String placeName, placeId;

        LocationModel(String placeName, String placeId) {
            this.placeId = placeId;
            this.placeName = placeName;
        }

        String getPlaceId() {
            return placeId;
        }

        String getPlaceName() {
            return placeName;
        }
    }

    private void loadFragment(Fragment fragment) {

        if (fragment != null) {
            Log.d("LOADFRAGMENT", "LOADFRAGMENT");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack("back");
            transaction.replace(R.id.frame_search_location, fragment).commit();
        }
    }
}
