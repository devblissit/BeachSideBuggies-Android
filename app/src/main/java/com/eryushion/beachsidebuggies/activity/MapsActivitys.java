package com.eryushion.beachsidebuggies.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.fragment.MyProfileFragment;
import com.eryushion.beachsidebuggies.fragment.SearchLocationFragment;
import com.eryushion.beachsidebuggies.helper.Permission;
import com.eryushion.beachsidebuggies.helper.SendNotification;
import com.eryushion.beachsidebuggies.model.Constans;
import com.eryushion.beachsidebuggies.model.RiderModel;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MapsActivitys extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener ,
        GoogleMap.OnPolygonClickListener {

    String TAG = "MAPSACTIVITY. ";
    public String fragmentClassItems = "";
    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public List<Address> addresses;
    public Marker markerPickup, markerDrop;
    public LatLng latlngPickup,lastSavedLatLng;
    public Place pickUpPlace, dropOffPlace;
    String[] data = {"Home", "My Profile", "Logout"};
    ImageView imgDrawerMenu, imgBack, imgDriver;
    RelativeLayout rlDrawer;
    public TextView tvPickup, tvSetPickupLocation, tvMinus, tvNumOfPassenger, tvPlus, tvDropOff, tvRequestRide, tvCancelRide, tvDriverName;
    EditText edtDriverNote;
    LinearLayout llDropOff, llReqDriver;
    RelativeLayout rlDriverDetail;
    ArrayAdapter<String> adapter;
    DrawerLayout drawer;
    public ListView navList;
    public MarkerOptions markerOptPickup;
    SupportMapFragment mapFragment;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String firebaseUserId = "", phoneNumber = "", pickupAddress = "", dropoffLocation = "", rideNotes = "";
    double pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude;
    int numberOfRiders = 1;
    int status = 0;
    int requestLimit = 0;
    long PickUpCount = 0;
    String rideId = "", driverName = "", driverFcmToken = "";
    boolean requestingRide = false, logout = false, setPickup = false;
    SharedPreferences sharedPreferences;
    ImageView imageMarker;
    ArrayList<LatLng> poly=new ArrayList<>();
    LatLng current_LatLng;
    private int zero;
    private int one;
    private int two;
    private int statusCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_map);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.drawer);
        imgDrawerMenu = (ImageView) findViewById(R.id.imgDrawerMenu);
        imgDriver = (ImageView) findViewById(R.id.imgDriver);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imageMarker = (ImageView) findViewById(R.id.imageMarker);
        rlDrawer = (RelativeLayout) findViewById(R.id.rlDrawer);
        tvPickup = (TextView) findViewById(R.id.tvPickup);
        tvSetPickupLocation = (TextView) findViewById(R.id.tvSetPickupLocation);
        tvMinus = (TextView) findViewById(R.id.tvMinus);
        tvNumOfPassenger = (TextView) findViewById(R.id.tvNumOfPassenger);
        tvNumOfPassenger.setText(String.valueOf(numberOfRiders));
        tvPlus = (TextView) findViewById(R.id.tvPlus);
        tvDropOff = (TextView) findViewById(R.id.tvDropOff);
        tvCancelRide = (TextView) findViewById(R.id.tvCancelRide);
        tvRequestRide = (TextView) findViewById(R.id.tvRequestRide);
        tvDriverName = (TextView) findViewById(R.id.tvDriverName);
        edtDriverNote = (EditText) findViewById(R.id.edtDriverNote);
        llDropOff = (LinearLayout) findViewById(R.id.llDropOff);
        llReqDriver = (LinearLayout) findViewById(R.id.llReqDriver);
        rlDriverDetail = (RelativeLayout) findViewById(R.id.rlDriverDetail);
        lastSavedLatLng=new LatLng(30.328494002870688,-81.4081697165966);
        imgDrawerMenu.setOnClickListener(this);
        tvPickup.setOnClickListener(this);
        tvSetPickupLocation.setOnClickListener(this);
        tvDropOff.setOnClickListener(this);
        tvMinus.setOnClickListener(this);
        tvPlus.setOnClickListener(this);
        tvRequestRide.setOnClickListener(this);
        tvCancelRide.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        rlDriverDetail.setOnClickListener(this);
        FirebaseAuth mfirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(Constans.SHARDPREF_FILENAME, MODE_PRIVATE);
        user = mfirebaseAuth.getCurrentUser();
        System.out.println("USERRRR"+user.toString());
        if (user != null) {
            firebaseUserId = user.getUid();
            mDatabase.child("users")
                    .child(firebaseUserId)
                    .child("phoneNumber")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChangePhoneNum");
                            phoneNumber = dataSnapshot.getValue(String.class);
                            // Log.d("phoneNumber", phoneNumber);
                            if (phoneNumber == null || phoneNumber.equals("")) {
                                Intent intent = new Intent(MapsActivitys.this, PhoneNumberActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            mDatabase.child("users").child(firebaseUserId).child("fcmToken")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  Log.d(TAG + " TOKENCNG", dataSnapshot.toString());
                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//                            Log.d(TAG + "Token", refreshedToken);
                            if (refreshedToken != null) {
//                                fcmToken = sharedPreferences.getString(Constans.TOKEN_KEY, "");
                                mDatabase.child("users").child(firebaseUserId).child("fcmToken").setValue(refreshedToken);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.child("pickupPoints").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null)
                    {
//                        long count = dataSnapshot.child("status").getChildrenCount();
//                        System.out.println("nooo"+count);
                        PickUpCount = dataSnapshot.getChildrenCount();
                        System.out.println("PickUpCount"+PickUpCount);

                        for(DataSnapshot snapShot:dataSnapshot.getChildren())
                        {
                            switch(snapShot.child("status").getValue(Integer.class)){ //This statement is seeing what "category" is.
                                case 0:
                                    ++zero;
                                    break;
                                case 1:
                                    ++one;
                                    break;
                                case 2:
                                    ++two;
                                    break;
                            }
                        }

                        statusCount = zero;
                        zero = 0;
                        System.out.println("statusCount"+" "+statusCount);


                    }
                }

                @Override
                public void  onCancelled(DatabaseError databaseError) {

                }
            });
           /* mDatabase.child("pickupPoints").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null)
                    {
                        PickUpCount = dataSnapshot.getChildrenCount();
                        System.out.println("PickUpCount"+PickUpCount);

                    }
                }

                @Override
                public void  onCancelled(DatabaseError databaseError) {

                }
            });*/
            //System.out.println("aaaaa"+status0);
            mDatabase.child("requestLimit").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null){
                        requestLimit = dataSnapshot.getValue(Integer.class);
                        System.out.println("requestLimit"+requestLimit);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                if (!requestingRide) {
                    if (pos == 0) {
                        fragmentClassItems = "";
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        drawer.closeDrawer(rlDrawer);
                    } else if (pos == 1) {
                        loadFragmentItem(new MyProfileFragment());
                    } else if (pos == 2) {
                        logoutDialog();
                    }


                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivitys.this);
                    alertDialog.setTitle("Cancel Ride");
                    alertDialog.setMessage("First cancel ride");
                    alertDialog.setIcon(R.drawable.ic_beach);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                }
                drawer.closeDrawer(rlDrawer);
            }
        });
    }

    public void requestingDriver() {
        tvCancelRide.setVisibility(View.VISIBLE);
        llReqDriver.setVisibility(View.VISIBLE);
        llDropOff.setVisibility(View.GONE);
        tvSetPickupLocation.setVisibility(View.GONE);
        tvPickup.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
    }

    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }
    private Calendar fromTime;
    private Calendar toTime;
    private Calendar currentTime;


    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.imgDrawerMenu:
                drawer.openDrawer(rlDrawer);
                break;

            case R.id.tvPickup:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Permission.askForPermission(MapsActivitys.this, Manifest.permission.ACCESS_FINE_LOCATION, Constans.PERMISSION_LOCATION);
                } else {
                    if (addresses != null) {
                        loadFragmentItem(new SearchLocationFragment("pickup"));
                    }
                }
                break;

            case R.id.tvDropOff:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Permission.askForPermission(MapsActivitys.this, Manifest.permission.ACCESS_FINE_LOCATION, Constans.PERMISSION_LOCATION);
                } else {
                    loadFragmentItem(new SearchLocationFragment("dropOff"));
                }
                break;

            case R.id.tvSetPickupLocation:
                if (addresses != null) {

                    llDropOff.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.VISIBLE);
                    tvPickup.setClickable(false);
                    tvPickup.setEnabled(false);
                    tvSetPickupLocation.setVisibility(View.GONE);
                    if (markerDrop == null && mCenterLatLong!=null) {
                        setPickup = true;
                        imageMarker.setVisibility(View.GONE);
                        markerOptPickup = new MarkerOptions();
                        markerOptPickup.position(new LatLng(mCenterLatLong.latitude, mCenterLatLong.longitude));
                        //   markerOptPickup.title(place.getName().toString());
                        //MAP PIN ICON
                        markerOptPickup.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
                        if (markerPickup == null) {
                            markerPickup = mMap.addMarker(markerOptPickup);
                        }
                        else
                            {
                            markerPickup.remove();
                            markerPickup = mMap.addMarker(markerOptPickup);
                        }
                    }
                }
                break;

            case R.id.imgBack:

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                Log.d("LOG", String.valueOf(day));

                llDropOff.setVisibility(View.GONE);
                tvPickup.setClickable(true);
                tvPickup.setEnabled(true);
                if (markerDrop == null) {
                    setPickup = false;

                    imageMarker.setVisibility(View.VISIBLE);
                    markerPickup.remove();
                    if (!setPickup) {
                        mCenterLatLong = mMap.getCameraPosition().target;
                        try {
                            Location mLocation = new Location("");
                            mLocation.setLatitude(mCenterLatLong.latitude);
                            mLocation.setLongitude(mCenterLatLong.longitude);
                            geoCoderMove(mCenterLatLong.latitude, mCenterLatLong.longitude);

                            Log.d("POSI", "Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                tvSetPickupLocation.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.GONE);
                break;
            case R.id.tvMinus:

                if (numberOfRiders != 1) {
                    numberOfRiders = numberOfRiders - 1;
                    tvNumOfPassenger.setText(String.valueOf(numberOfRiders));
                }
                break;
            case R.id.tvPlus:
                if (numberOfRiders != 11) {
                    numberOfRiders = numberOfRiders + 1;
                    tvNumOfPassenger.setText(String.valueOf(numberOfRiders));
                }
                break;
            case R.id.tvRequestRide:
                System.out.println("valueeee"+statusCount);
                rideNotes = edtDriverNote.getText().toString();
                if (new LatLng(pickupLatitude,pickupLongitude) != null) {
                    if (dropOffPlace != null) {
                        Log.d("LOG", String.valueOf(new LatLng(pickupLatitude,pickupLongitude)));
                        Log.d("LOG", String.valueOf(dropOffPlace.getLatLng()));
                        if (new LatLng(pickupLatitude,pickupLongitude).equals(dropOffPlace.getLatLng())) {
                            alertDialog("", "Please choose different Pickup and DropOff location");
                        } else {
                            if(statusCount<requestLimit){
                                requestRide();

                            }
                           else{
                                showAlert(getResources().getString(R.string.request_limit_title),getResources().getString(R.string.request_limit_exceeded));
                                System.out.println("NotAllowed"+requestLimit);
                            }
                        }
                    } else {
                        alertDialog("", "Please choose destination");

                    }
                } else if (!addresses.isEmpty()) {
                    if (dropOffPlace != null) {
                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        if (dropOffPlace.getLatLng().equals(latLng)) {
                            alertDialog("", "Please choose different Pickup and DropOff location");
                        } else {

                            if(statusCount < requestLimit){
                                requestRide();


                            }
                            else{

                                showAlert(getResources().getString(R.string.request_limit_title),getResources().getString(R.string.request_limit_exceeded));
                                System.out.println("NotAllowed"+requestLimit);
                            }
//                            requestRide();
                        }
                    } else {
                        alertDialog("", "Please choose destination");
                    }
                }
                break;
            case R.id.tvCancelRide:
                //status= 0 remove from pickuppoints
                // status =1 ,remove from pickuppoints ,& remove from loggedInDrivers->acceptedRides->rideID
                // status 2 then no delete  message ="Can't Cancel Ride After Pickup"
                cancelRide();
                break;
        }

    }

    private boolean checkDay()
    {
        boolean validTime;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Log.d("LOG", String.valueOf(day));
/**-     -----------------------------------------------------------24 HOURS FORMAT--*/
        switch (day) {
            case Calendar.SUNDAY:
                if ((hour >= 11 && hour < 24) || hour == 0 || (hour == 1 && minute < 30)) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.MONDAY:
                if (hour >= 15 && hour < 24) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.TUESDAY:
                if (hour >= 15 && hour < 24) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.WEDNESDAY:
                if (hour >= 15 && hour < 24) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.THURSDAY:
                if (hour >= 15 && hour < 24) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.FRIDAY:
                if ((hour >= 15 && hour < 24) || hour == 0 || (hour == 1 && minute < 30))  {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            case Calendar.SATURDAY:
                if ((hour >= 11 && hour < 24) || hour == 0 || (hour == 1 && minute < 30)) {
                    validTime = true;
                }else{
                    validTime = false;
                }
                break;
            default:
                validTime = false;
                break;
        }

        return validTime;
    }

    private void showAlert(String title,String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    private void cancelRide() {
        //if (mDatabase.child("pickupPoints").child(rideId).)
        if (status == 0) {
            mDatabase.child("pickupPoints")
                    .child(rideId)
                    .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (!driverFcmToken.equals("") && driverFcmToken != null) {

                                SendNotification.sendNotification(driverFcmToken, "Rides Info", "Ride cancel by Customer");
                            }
                           cancelled();
                        }
                    });
        } else if (status == 1) {
            mDatabase.child("loggedInDrivers")
                    .child(driverName)
                    .child("acceptedRides")
                    .child(rideId)
                    .removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            mDatabase.child("pickupPoints")
                                    .child(rideId)
                                    .removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                            if (!driverFcmToken.equals("") && driverFcmToken != null) {
                                                SendNotification.sendNotification(driverFcmToken, "Rides Info", "Ride cancel by Customer");
                                            }
                                          cancelled();
                                        }
                                    });
                        }
                    });
        } else if (status == 2) {
            Snackbar.make(drawer, "Can't Cancel Ride After Pickup", Snackbar.LENGTH_LONG).show();
        }
        //addUserChangeListener();
    }

    private void cancelled() {
        requestingRide = false;
        rlDriverDetail.setVisibility(View.GONE);
        tvCancelRide.setVisibility(View.GONE);
        llReqDriver.setVisibility(View.GONE);
        tvSetPickupLocation.setVisibility(View.GONE);
        llDropOff.setVisibility(View.VISIBLE);
        tvPickup.setVisibility(View.VISIBLE);
        imgBack.setVisibility(View.VISIBLE);
    }

    private void alertDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivitys.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_beach);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!requestingRide) {
            if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                finish();
            } else {
                fragmentClassItems = "";
                super.onBackPressed();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivitys.this);
            alertDialog.setTitle("Cancel Ride");
            alertDialog.setMessage("Your ride is cancel");
            alertDialog.setIcon(R.drawable.ic_beach);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        }
    }

    private void loadFragmentItem(Fragment fragment) {

        if (!fragmentClassItems.equals(fragment.getClass().getName())) {
            Log.d("LOADFRAGMENT", "LOADFRAGMENT");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, fragment);
            fragmentClassItems = fragment.getClass().getName();
            //  Log.d("FragmentClass", fragmentClass);
            transaction.addToBackStack(fragmentClassItems);
            transaction.commit();
        }
    }

    public void logoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivitys.this);
        alertDialog.setTitle("Confirm Logout...");
        alertDialog.setMessage("Are you sure you want Logout?");
        alertDialog.setIcon(R.drawable.ic_beach);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d("TAG", "onAuthStateChanged:signdOut:");
                if (user != null) {
                    logout = true;
                    Log.d("UseridLOGOUT", firebaseUserId);
                    mDatabase.child("users").child(firebaseUserId).child("fcmToken").setValue("");

                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(MapsActivitys.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

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

    private LatLng mCenterLatLong;

    @Override
    protected void onStart() {
        super.onStart();
        //   Permission.askForPermission(MapsActivitys.this, Manifest.permission.ACCESS_COARSE_LOCATION, Constans.PERMISSION_LOCATION);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //
        mMap = googleMap;

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (!setPickup) {
                    //mMap.clear();
                    mCenterLatLong = mMap.getCameraPosition().target;
                    try {
                        Location mLocation = new Location("");
                        mLocation.setLatitude(mCenterLatLong.latitude);
                        mLocation.setLongitude(mCenterLatLong.longitude);
                        geoCoderMove(mCenterLatLong.latitude, mCenterLatLong.longitude);
//                    startIntentService(mLocation);

                        Log.d("POSI", "Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission.askForPermission(MapsActivitys.this, Manifest.permission.ACCESS_COARSE_LOCATION, Constans.PERMISSION_LOCATION);
            Permission.askForPermission(MapsActivitys.this, Manifest.permission.ACCESS_FINE_LOCATION, Constans.PERMISSION_LOCATION);
        } else {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setPadding(10, 10, 10, 10);
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
    }


    public void dropoffMarker(Place place) {

        dropoffLocation = place.getAddress().toString();
        dropoffLatitude = place.getLatLng().latitude;
        dropoffLongitude = place.getLatLng().longitude;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place.getLatLng());
        markerOptions.title(place.getName().toString());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (markerDrop == null) {
            markerDrop = mMap.addMarker(markerOptions);
        } else {
            markerDrop.remove();
            markerDrop = mMap.addMarker(markerOptions);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerDrop.getPosition());
        if (markerPickup != null) {
            builder.include(markerPickup.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels / 2;
        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }


    public void pickUpLocation(Place place) {

        pickupAddress = place.getAddress().toString();
        Log.d("PickupAddress", pickupAddress);
        pickupLatitude = place.getLatLng().latitude;
        pickupLongitude = place.getLatLng().longitude;
        if (markerDrop != null) {
            markerOptPickup = new MarkerOptions();
            markerOptPickup.position(place.getLatLng());
            markerOptPickup.title(place.getName().toString());
            markerOptPickup.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
            if (markerPickup == null) {
                markerPickup = mMap.addMarker(markerOptPickup);
            } else {
                markerPickup.remove();
                markerPickup = mMap.addMarker(markerOptPickup);
            }
        }


        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 19f));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (markerDrop == null) {
           /* Location mLocation = new Location("");
            mLocation.setLatitude(pickupLatitude);
            mLocation.setLongitude(pickupLongitude);
            Log.d("POSI", "Lat : " + pickupLatitude + "," + "Long : " + pickupLongitude);*/
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f));
        } else {
            builder.include(place.getLatLng());
            builder.include(markerDrop.getPosition());
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels / 2;
            int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);

        }
    }

    String placeName = "";

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)
                    .zoom(14f)
                    .tilt(70)
                    .build();

            mMap.setMyLocationEnabled(true);
            //  mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            current_LatLng=new LatLng(location.getLatitude(),location.getLongitude());
            System.out.println("currentLATLONG"+current_LatLng);
            Log.d("SERVICE", "Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            double crtLat = location.getLatitude();
            double crtLng = location.getLongitude();
            geoCoder(crtLat, crtLng);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        markerOptPickup = new MarkerOptions();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        poly=new ArrayList<>();
        poly.add(new LatLng(30.3327,-81.4112));
        poly.add(new LatLng(30.3234,-81.4131));
        poly.add(new LatLng(30.3074,-81.4198));
        poly.add(new LatLng(30.2893,-81.4205));
        poly.add( new LatLng(30.2680,-81.4182));
        poly.add( new LatLng(30.2676,-81.3836));
        poly.add( new LatLng(30.3410,-81.3953));
        poly.add(new LatLng(30.3402,-81.4037));
        poly.add( new LatLng(30.3329,-81.4020));
        poly.add( new LatLng(30.3327,-81.4112));

        if (mLastLocation != null) {

            changeMap(mLastLocation);
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .addAll(poly));

            polygon.setStrokeWidth(3.0f);
            polygon.setStrokeColor(getResources().getColor(R.color.polygon_stroke_color));
            polygon.setFillColor(getResources().getColor(R.color.polygon_fill_color));
// Store a data object with the polygon, used here to indicate an arbitrary type.


            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 14.0f));

            // Set listeners for click events.
            mMap.setOnPolygonClickListener(this);

         /*   double crtLat = mLastLocation.getLatitude();
            double crtLng = mLastLocation.getLongitude();

            LatLng currentLocation = new LatLng(crtLat, crtLng);
            latlngPickup = currentLocation;

            markerOptPickup.position(currentLocation);
            markerOptPickup.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));*/
           /* markerOptPickup.draggable(true);

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    geoCoderMove(marker.getPosition().latitude, marker.getPosition().longitude);


                }
            });*/
            //geoCoder(crtLat, crtLng);

        }
    }
    private void geoCoder(double crtLat, double crtLng) {
        Geocoder geocoder = new Geocoder(MapsActivitys.this, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(crtLat, crtLng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null) {

                pickupLatitude = crtLat;
                pickupLongitude = crtLng;
                Log.d("pickupLatitudes", String.valueOf(pickupLatitude));
                Log.d("pickuplongitudes", String.valueOf(pickupLongitude));

            }

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    try {

                        Place place = likelyPlaces.get(0).getPlace();
                        Log.d("pickupLatitude", String.valueOf(place.getLatLng().latitude));
                        Log.d("pickuplongitude", String.valueOf(place.getLatLng().longitude));
                        pickupAddress = String.valueOf(place.getAddress());
                        Log.d("PLACEADDRESS", pickupAddress);
                        placeName = String.valueOf(place.getName());
                        Log.d("placeName", placeName);
                        // markerOptPickup.title(placeName);
                       // tvPickup.setText(place.getName());
                        likelyPlaces.release();
                    } catch (Exception ignore) {

                    }
                }
            });

            // markerPickup = mMap.addMarker(markerOptPickup);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(crtLat, crtLng), 15.0f));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void geoCoderMove(double crtLat, double crtLng) {
        Log.e("scroll",crtLat+" "+crtLng);
        Geocoder geocoder = new Geocoder(MapsActivitys.this, Locale.getDefault());
        if (!isPointInPolygon(new LatLng(crtLat,crtLng),poly)){
            Log.e("scrollIf",crtLat+" "+crtLng);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastSavedLatLng, 14.0f));
            try {
                addresses = geocoder.getFromLocation(lastSavedLatLng.latitude, lastSavedLatLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
               if (addresses!=null){
                   String address = addresses.get(0).getAddressLine(0);
                   tvPickup.setText(address+"");
                   pickupAddress=address;
                   pickupLatitude = lastSavedLatLng.latitude;
                   pickupLongitude = lastSavedLatLng.longitude;
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      else{
            try {
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                addresses = geocoder.getFromLocation(crtLat, crtLng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses != null) {

                    pickupLatitude = crtLat;
                    pickupLongitude = crtLng;
                    lastSavedLatLng=new LatLng(crtLat,crtLng);
                    Log.d("pickupLatitudes", String.valueOf(pickupLatitude));
                    Log.d("pickuplongitudes", String.valueOf(pickupLongitude));
                    Address address = addresses.get(0);
                    Log.d("placeName", address.getAddressLine(0));
                    placeName = String.valueOf(address.getAddressLine(0));
                    Log.d("placeName", placeName);
                    // markerOptPickup.title(placeName);
                    tvPickup.setText(placeName);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        if (i != 1) {
                            sb.append(address.getAddressLine(i)).append(",");
                        }
                    }
                    sb.append(address.getCountryName());
                    pickupAddress = sb.toString();
                    Log.d("pickupAddress", pickupAddress);
                }

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(crtLat, crtLng), 15.0f));


            } catch (IOException e) {
                e.printStackTrace();
            }
      }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void requestRide() {

/*        Log.d("pickupLatitude", String.valueOf(pickupLatitude));
        Log.d("pickupLongitude", String.valueOf(pickupLongitude));
        Log.d("pickupAddress", String.valueOf(pickupAddress));
        Log.d("dropoffLatitude", String.valueOf(dropoffLatitude));
        Log.d("dropoffLongitude", String.valueOf(dropoffLongitude));
        Log.d("dropoffLocation", String.valueOf(dropoffLocation));*/

      //current_LatLng = new LatLng(30.3327, -81.4112);

        Log.e("locinPolygon",isPointInPolygon(current_LatLng,poly)+" "+pickupAddress+" "+pickupLatitude+" "+pickupLongitude);
        if (!isPointInPolygon(current_LatLng,poly)){
            showAlert(getResources().getString(R.string.hours_alert_title),getResources().getString(R.string.area_alert_msg));
        }else if (!isPointInPolygon(new LatLng(pickupLatitude,pickupLongitude),poly)){
            showAlert(getResources().getString(R.string.hours_alert_title),getResources().getString(R.string.pickup_alert_msg));
    }else if (!isPointInPolygon(new LatLng(dropoffLatitude,dropoffLongitude),poly)){
            showAlert(getResources().getString(R.string.hours_alert_title),getResources().getString(R.string.drop_alert_msg));
        }else if (!checkDay()){
            showAlert(getResources().getString(R.string.hours_alert_title),getResources().getString(R.string.hours_alert_msg));
        }
        else{
            try {
                rideId = String.valueOf(System.currentTimeMillis());
                Log.d("timeS", rideId);

                Calendar c = Calendar.getInstance();
                //System.out.println("Current time => "+c.getTime());

                //@SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy hh:mm a",Locale.getDefault());
                String formattedDate = df.format(c.getTime());
                Log.d("CurrentTime", formattedDate);

                if (user != null) {
                    requestingRide = true;
                    Log.d(TAG, "REQUEST_SEND");
                    RiderModel riderModel = new RiderModel();
                    riderModel.setRideID(rideId);
                    riderModel.setUserID(firebaseUserId);
                    riderModel.setUsername(user.getDisplayName());
                    riderModel.setPhoneNumber(phoneNumber);
                    riderModel.setPickupAddress(pickupAddress);
                    riderModel.setPickupLatitude(pickupLatitude);
                    riderModel.setPickupLongitude(pickupLongitude);
                    riderModel.setDropoffLatitude(dropoffLatitude);
                    riderModel.setDropoffLongitude(dropoffLongitude);
                    riderModel.setDropoffLocation(dropoffLocation);
                    riderModel.setNumberOfRiders(numberOfRiders);
                    riderModel.setRideNotes(rideNotes);
                    riderModel.setStatus(status);
                    riderModel.setRequestTime(formattedDate);
                    if (user.getPhotoUrl() != null) {
                        riderModel.setPicture(String.valueOf(user.getPhotoUrl()));
                    } else {
                        riderModel.setPicture("");
                    }
                    // Log.d("RiDERMODEL", String.valueOf(riderModel));

                    mDatabase.child("pickupPoints").child(rideId).setValue(riderModel);
                    requestingDriver();
                    try {
                        addUserChangeListener();
                    } catch (Exception ignored) {

                    }

                }
            } catch (Exception ignored) {

            }
        }
    }

    private void addUserChangeListener() {

        mDatabase.child("pickupPoints")
                .child(rideId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {

                            if (dataSnapshot.getValue() != null) {

                                /*requestLimit = dataSnapshot.child("requestLimit").getValue(Integer.class);
                                System.out.println("REQUESTTTTT"+requestLimit);*/
                                status = dataSnapshot.child("status").getValue(Integer.class);

                                System.out.println("statussss"+status);

                                //status=0 request,  status=1 drive accepted, status=2 pickup drive
                                if (status == 1) {
                                    String driverID = dataSnapshot.child("driverID").getValue(String.class);
                                    driverName = dataSnapshot.child("driverName").getValue(String.class);
                                    // String driverPicture = dataSnapshot.child("driverPicture").getValue(String.class);

                                    if (driverID != null) {
                                        mDatabase.child("users")
                                                .child(driverID)
                                                .child("fcmToken")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        try {
                                                            Log.d(TAG + " TOKENNGE", dataSnapshot.toString());
                                                            if (dataSnapshot.getValue() != null) {
                                                                driverFcmToken = dataSnapshot.getValue(String.class);
                                                            }
                                                        } catch (Exception ignored) {

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                    Log.d("driverID", String.valueOf(driverID));
                                    try {
                                        if (driverName != null) {
                                            tvDriverName.setText(driverName);
                                        }
                                    } catch (Exception ignored) {

                                    }


                                    llReqDriver.setVisibility(View.GONE);
                                    rlDriverDetail.setVisibility(View.VISIBLE);
                                }
                            } else {
                                //  Log.d(TAG, "NOTFOUND");
                                //Snackbar.make(drawer, "Your ride is Cancel", Snackbar.LENGTH_LONG).show();

                                finish();
                                startActivity(getIntent());
                            }
                        } catch (Exception ignored) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        // Log.d(TAG, "Failed to read user", error.toException());
                    }
                });
    }

    /*@SuppressLint("PackageManagerGetSignatures")
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("PackageName=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("KeyHash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(MapsActivitys.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constans.PERMISSION_LOCATION:

                    mapFragment.getMapAsync(this);
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.setPadding(10, 10, 10, 10);
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    break;
            }
        } else {
            // Log.d("PEMISSION", "DENIDE");
        }
    }

    @Override
    public void onPolygonClick(Polygon polygon) {

        Log.e("areaPolygon",polygon.getPoints()+"");

    }

}
