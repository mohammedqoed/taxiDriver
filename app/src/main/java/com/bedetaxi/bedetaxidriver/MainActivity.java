package com.bedetaxi.bedetaxidriver;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.height;
import static android.R.attr.height;
import static android.R.attr.height;
import static android.R.attr.width;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {



    private MapView mapView;
    private MapboxMap map;
    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    static Firebase isAvailable;
    public static ImageView UserImage;
    public static boolean isInRequest = false;
    Menu My_Menu;
    MenuItem item;
    LatLng latLng;
    Marker mCurrLocation;
    Firebase lat;
    Firebase lng;
    private Firebase hasRequest;
    private Firebase Response;
    public static Firebase OrderID;
    private Firebase requestByOffice;
    Firebase dateTime;
    private Firebase speedFirebase;
    LatLng oldLatlag = null;
    int requestID = 0;
    public static UserDataFragment fragment;
    public static FragmentTransaction fragmentTransaction;
    private Location location;
    String userIDTwo = "e7108889-ec39-479b-af5d-1c167a027ca2";
    String userID ;
    static SharedPreferencesManager sharedPreferencesManager;
    public static   Context context;
    static ImageButton availableButton;
    public static boolean isTaxiAvailable;
    public Timer timer;
    TextView timeRecorderView;
    TextView distanceRecorderView;
   public ImageView startRecorder;
    float distanceInRecorder = 0.0f;

    private static final String URL = "http://bedetaxi.cloudapp.net/BedeService/Service1.svc"; // http://192.168.1.73/Service/MunicipalityService.svc
    String SOAP_ACTION = "http://tempuri.org/IService1/UpdateLocation";
    private static final String NAMESPACE = "http://tempuri.org/";
    public static String order;
    Timer updatingTime;
    int timeToUpdate = 0;
    Firebase trackingDate;
    boolean isRecorderRequest=false;
    Dialog dialog = null;
    public boolean status = true ;
    public static boolean taxiStatus = true;
    public String destSummary = "";
    public String pickSummary = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        timerUpdate();

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setMyLocationEnabled(true);
                buildGoogleApiClient();

            }
        });


        timeRecorderView = (TextView) findViewById(R.id.recorderView);
        distanceRecorderView = (TextView) findViewById(R.id.distance);
        startRecorder = (ImageView) findViewById(R.id.recorder);


        startRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String status = startRecorder.getText().toString();
                if(!status){
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time =&gt; "+c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    dateTime.setValue(formattedDate);
                    Toast.makeText(MainActivity.context, "شكرا لاستخدامك تطبيق بدي تكسي  ", Toast.LENGTH_LONG).show();
                    isInRequest = false;
                   // startRecorder.setText("طلب جديد");
                    status = true;
                    startRecorder.setImageResource(R.drawable.add);
                    FinishRecorder();
                    //showSummary();


                    final Dialog dialog1 = new Dialog(MainActivity.this,R.style.PauseDialog);
                    dialog1.setContentView(R.layout.popup);
                    //dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog1.setTitle("ملخص الرحلة ");


                    TextView tv_time = (TextView) dialog1.findViewById(R.id.tripTime);
                    TextView tv_dist = (TextView) dialog1.findViewById(R.id.tripDistance);

                    tv_time.setText(timeRecorderView.getText());
                    tv_dist.setText(distanceRecorderView.getText());


                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog1.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            dialog1.dismiss();


                        }
                    }, 10000);

                    timeRecorderView.setText("00:00:00");
                    distanceRecorderView.setText("0.0 كم");
                }else {

                    if (timer == null) {
                        if (isTaxiAvailable == false) {
                            showAlertDialog("التوافر", "يرجى تشغيل التوافر لتتمكن من الطلب ");
                            return;
                        }
                        startTimer();


                        FragmentManager fragmentManager = getFragmentManager();
                        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                                R.animator.fragment_slide_left_exit,
                                R.animator.fragment_slide_right_enter,
                                R.animator.fragment_slide_right_exit);
                        PlacesFragment fragment = new PlacesFragment();
                        fragmentTransaction.add(R.id.table, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        //startRecorder.setText("انهاء");
                        //startRecorder.setEnabled(false);
                        isRecorderRequest = true;
                        if (!checkNetworkConnection() || !isInternetOn(MainActivity.this)) {
                            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

                        } else {
                           // isTaxiAvailable = !isTaxiAvailable;
                            taxiStatus = false;
                            updateAvailabltyButton();

//                        new MainActivity.UpdateAvailability(isTaxiAvailable).execute();
                        }
                    }
                }
            }
        });







        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
        userID = sharedPreferencesManager.getUserID();
//        trackingDate = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/Tracking/TrackingData");

        isAvailable = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/Tracking/isAvailable");
        dateTime = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/Tracking/datetime");

        speedFirebase = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/Tracking/speed");

        lat = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/Tracking");

        Response = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/RequestDetails/Response");

//        hasRequest = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/RequestDetails/HasRequest");
//        hasRequest.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                sharedPreferencesManager.editor.putString("order",value);
//                sharedPreferencesManager.editor.commit();
//                if (value == null) {
//                    return;
//                }
//
//                if (value.equals("1")) {
//                    ShowNot("طلب", "لديك طلب واحد قيد الانتضار للموافقة");
//                    showAlertDialog();
//                }
//            }
//
//
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });



        availableButton = (ImageButton) findViewById(R.id.ib);
        isTaxiAvailable = sharedPreferencesManager.getAvailability();
        if (isTaxiAvailable){
            availableButton.setBackgroundColor(Color.parseColor("#FFCD00"));

        }else{
            availableButton.setBackgroundColor(Color.parseColor("#F60505"));

        }
        availableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkNetworkConnection() || !isInternetOn(MainActivity.this)) {
                    Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

                } else if(timer != null){
                    showAlertDialog("خطأ","لا يمكنك تعديل التوافر والعداد قيد التشغيل");

                } else{
                    isTaxiAvailable = !isTaxiAvailable;
                    taxiStatus = true;
                    updateAvailabltyButton();
//                    new MainActivity.UpdateAvailability(isTaxiAvailable).execute();
                }
            }
        });



        requestByOffice = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/RequestDetails/RequestMessage");
        requestByOffice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    order=  dataSnapshot.getValue(String.class);

                String value = dataSnapshot.getValue(String.class);
                if(value == null){
                    return;
                }
                if (value.trim().isEmpty()){
                    if (dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                        return;
                    }
                }

                try {
                    JSONObject data = new JSONObject(value);
                    final String userName =data.getString("UserName");
                    final String phone = data.getString("PhoneNumber");
                    final String PickupPoint = data.getString("PickupPoint");
                    pickSummary = data.getString("PickupPoint");
                    final String DestinationPoint = data.getString("DestinationPoint");
                    destSummary = data.getString("DestinationPoint");
                    String Notes = data.getString("Notes");
                    FragmentManager fragmentManager = getFragmentManager();
                    final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.main_dialog);

                    TextView title = (TextView) dialog.findViewById(R.id.dialog_title);

                    title.setText("لديك طلب من المكتب هل تريد قبول الطلب ");
                    Button dialog_no = (Button) dialog.findViewById(R.id.dialog_button_No);
                    dialog_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Response.setValue(0);
                        }
                    });
                    Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_button_Yes);
                    dialog_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestByOffice.setValue("");
                            Response.setValue(1);
                            //startRecorder.setEnabled(false);
                           // startRecorder.setText("انهاء");
                            status = false;
                            startRecorder.setImageResource(R.drawable.close);


                            dialog.dismiss();
                            if (!checkNetworkConnection() || !isInternetOn(MainActivity.this)) {
                                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

                            } else {
                                //isTaxiAvailable = !isTaxiAvailable;
                                taxiStatus= false;
                                updateAvailabltyButton();
//                                new MainActivity.UpdateAvailability(isTaxiAvailable).execute();
                            }


                            fragment = new UserDataFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("userName",userName );
                            bundle.putString("userPhone", phone);
                            bundle.putString("PickupPoint",PickupPoint);
                            bundle.putString("DestinationPoint",DestinationPoint);

                            fragment.setArguments(bundle);
                            fragmentTransaction.add(R.id.fragment, fragment);
                            fragmentTransaction.commitAllowingStateLoss();

                        }
                    });
                    dialog.show();



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });





//        OrderID = new Firebase("https://taxihere.firebaseio.com/Drivers/"+userID+"/RequestDetails/OrderID");
//        OrderID.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                order = dataSnapshot.getValue(String.class);
//                sharedPreferencesManager.editor.putString("order",order);
//                sharedPreferencesManager.editor.commit();
//
//                if (!isInRequest) {
//                    return;
//                }
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentTransaction = fragmentManager.beginTransaction();
//                String value = dataSnapshot.getValue(String.class);
//                List<PropertyInfo> prop = getPropertyInfo(value);
//                WebAPI api = new WebAPI(getApplicationContext(), "getOrderDetails", prop);
//                String output = api.call();
//                try {
//                    JSONArray data = new JSONArray(output);
//                    String status = data.getJSONObject(0).getString("status");
//                    if (status.equals("success")) {
//                        Response.setValue(1);
//                        MenuItem item = My_Menu.findItem(R.id.action_Done);
//                        item.setVisible(true);
//                        if (!checkNetworkConnection() || !isInternetOn(MainActivity.this)) {
//                            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            isTaxiAvailable = !isTaxiAvailable;
//                            new MainActivity.UpdateAvailability(isTaxiAvailable).execute();
//                        }
//                        JSONArray details = data.getJSONObject(0).getJSONArray("details");
//                        double userLat = Double.parseDouble(details.getJSONObject(0).getString("Pickuplat"));
//                        double userLng = Double.parseDouble(details.getJSONObject(0).getString("PickupLng"));
//
//                        map.addMarker(getMarkerOption(userLat,userLng, "USER"));
//                        String userID = details.getJSONObject(0).getString("UserID");
//                        String userName = details.getJSONObject(0).getString("UserName");
//                        String UserPhone = details.getJSONObject(0).getString("UserPhone");
//
//
//                        fragment = new UserDataFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("userName", userName);
//                        bundle.putString("userPhone", UserPhone);
//
//
//                        fragment.setArguments(bundle);
//                        fragmentTransaction.add(R.id.fragment, fragment);
//                        fragmentTransaction.commitAllowingStateLoss();
////                        try{
////                            getRoute(Position.fromCoordinates(userLng, userLat),Position.fromCoordinates(mCurrLocation.getLongitude(), location.getLatitude()));
////                        } catch (ServicesException servicesException) {
////                            servicesException.printStackTrace();
////                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), data.getJSONObject(0).getString("details"), Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });




        setActionBar();
    }

    public void FinishRecorder(){
        if (isRecorderRequest) {
            isRecorderRequest = false;
           // startRecorder.setEnabled(true);
            //startRecorder.setText("طلب جديد");
            status = true;
            startRecorder.setImageResource(R.drawable.add);
        }else{
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        distanceInRecorder = 0.0f;
       // startRecorder.setEnabled(true);
        //startRecorder.setText("طلب جديد");
        status = true;
        startRecorder.setImageResource(R.drawable.add);
        item.setVisible(false);
        if (timer != null)
        stopTimer();
        if (!checkNetworkConnection() || !isInternetOn(MainActivity.this)) {
            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();

        } else {
            //isTaxiAvailable = !isTaxiAvailable;
            taxiStatus = true;
            updateAvailabltyButton();
            availableButton.setEnabled(true);
//            new MainActivity.UpdateAvailability(isTaxiAvailable).execute();
        }
    }

    public void RecorderRun(){
        if(timer == null) {

            startTimer();

        }
    }




    @SuppressWarnings("WrongConstant")
    public void setActionBar(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(false) ;

        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_END);
        imageView.setLayoutDirection(View.TEXT_ALIGNMENT_CENTER);
        imageView.setImageResource(R.drawable.main_logo);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,  Gravity.CENTER_HORIZONTAL);
        imageView.setLayoutParams(layoutParams);
/*
        TextView textView = new TextView(actionBar.getThemedContext());

        textView.setLayoutDirection(View.TEXT_ALIGNMENT_CENTER);
        textView.setText("انهاء");
        textView.setTextSize(50f);

        ActionBar.LayoutParams layoutParams1 = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,  Gravity.RIGHT);
        textView.setLayoutParams(layoutParams1);
        */
        actionBar.setCustomView(imageView);
       // actionBar.setCustomView(textView);
    }



    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.



        UserImage = (ImageView) findViewById(R.id.imageView);
        String Image="";
        if(sharedPreferencesManager.getImage()!=null) {
            Image = sharedPreferencesManager.getImage();
        }
        byte [] encodeByte= Base64.decode(Image, Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        if (bitmap!=null)
            UserImage.setImageBitmap(bitmap);

        getMenuInflater().inflate(R.menu.main, menu);


        item = menu.findItem(R.id.action_Done);

/*
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        int end = spanString.length();
         spanString.setSpan(new RelativeSizeSpan(1.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(spanString);
*/


        this.My_Menu=menu;




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Done) {
            Toast.makeText(this, "شكرا لاستخدامك تطبيق بدي تكسي  ", Toast.LENGTH_LONG).show();
            isInRequest = false;

                FinishRecorder();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.History) {
            // Handle the camera action
            if (!checkNetworkConnection()) {
                Toast.makeText(getApplicationContext(), "الرجاء التحقق من الاتصال بالانترنت", Toast.LENGTH_LONG).show();

            }else {
                Intent i = new Intent(this, My_History.class);
                startActivity(i);
            }
        } else if (id == R.id.HomeItem) {

        } else if (id == R.id.Edit_Profile_item) {
            Intent i = new Intent(this,EditProfile.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try{
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                //place marker at current position
                map.clear();
                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            location.setLatitude(latLng.getLatitude());
//            location.setLongitude(latLng.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("موقعك الحالي");
                mCurrLocation = map.addMarker(markerOptions);
                map.animateCamera(getCameraUpdate(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 7000);


            }

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(3000); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {

        }




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location.hasAccuracy()){
            if ((location.getAccuracy() > 10)){
                return;
            }
        }

        if (mCurrLocation != null) {
            mCurrLocation.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("موقعك الحالي");
        mCurrLocation = map.addMarker(markerOptions);
        if (timeToUpdate < 3){
            return;
        }
        timeToUpdate =0;



        latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        location.setLatitude(latLng.getLatitude());
//        location.setLongitude(latLng.getLongitude());

        map.animateCamera(getCameraUpdate(location.getLatitude(), location.getLongitude()), 7000);
//        List<PropertyInfo> prop = getPropertyInfo(location,speed);
        if (oldLatlag == null){
            oldLatlag = latLng;
            float speed = (float) (location.getSpeed()*3.6);
            Map<String ,Object> hmap = new HashMap<String , Object>();
            hmap.put("lat",location.getLatitude());
            hmap.put("lng", location.getLongitude());
            hmap.put("speed", String.valueOf(speed));
            lat.updateChildren(hmap);
//            speedFirebase.setValue(speed);
//            trackingDate.setValue(location.getLatitude()+","+location.getLongitude()+","+sharedPreferencesManager.getAvailability()+","+speed);

//            try {
//                SoapObject request = new SoapObject(NAMESPACE, "UpdateLocation");
//                for (PropertyInfo p : prop) {
//                    request.addProperty(p);
//                }
//                try {
//                    new MainActivity.Update(request).execute();

//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//
//            }
        }else if (Math.abs(oldLatlag.getLatitude() - latLng.getLatitude()) > 0.0005 || Math.abs(oldLatlag.getLongitude() - latLng.getLongitude()) > 0.0005) {
//            Map<String ,Object> hmap = new HashMap<String , Object>();
//            hmap.put("lat", location.getLatitude());
//            hmap.put("lng", location.getLongitude());
//            lat.updateChildren(hmap);
            float speed = (float) (location.getSpeed()*3.6);
            Map<String ,Object> hmap = new HashMap<String , Object>();
            hmap.put("lat",location.getLatitude());
            hmap.put("lng", location.getLongitude());
            hmap.put("speed", String.valueOf(speed));
            lat.updateChildren(hmap);
            speedFirebase.setValue(speed);
//            trackingDate.setValue(location.getLatitude()+","+location.getLongitude()+","+sharedPreferencesManager.getAvailability()+","+speed);

            if (timer != null && oldLatlag != null){
                distanceInRecorder = (float) (distanceInRecorder+oldLatlag.distanceTo(latLng));
                DecimalFormat df = new DecimalFormat("#.#");
                distanceRecorderView.setText("كم "+String.valueOf(df.format(distanceInRecorder *0.001)));
            }
            oldLatlag = latLng;
//            try {
//                SoapObject request = new SoapObject(NAMESPACE, "UpdateLocation");
//                for (PropertyInfo p : prop) {
//                    request.addProperty(p);
//                }
//                try {
//                    new MainActivity.Update(request).execute();

//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//
//                }

//            } catch (Exception e) {
//
//                e.printStackTrace();
//
//            }
        }

//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
    }


    public MarkerOptions getMarkerOption (double lat,double lng, String title){
        return  new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title);

    }
    public CameraUpdate getCameraUpdate(Double lat, Double lng){

        CameraPosition position =  new CameraPosition.Builder()
                .target(new LatLng(lat, lng)) // Sets the new camera position
                .zoom(15) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        return  CameraUpdateFactory.newCameraPosition(position);

    }
    public void timerUpdate(){
        updatingTime = new Timer();
        updatingTime.scheduleAtFixedRate(new TimerTask() {
            int sec = 0;
            int min = -1;
            int h = 0;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeToUpdate++;
                    }
                });
            }
        }, 1000, 1000);

    }

    private void showAlertDialog (){

        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.main_dialog);

        TextView title = (TextView) dialog.findViewById(R.id.dialog_title);

        title.setText("لديك طلب , هل تريد الموافقة عليه ؟");
        Button dialog_no = (Button) dialog.findViewById(R.id.dialog_button_No);
        dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Response.setValue(0);

            }

        });
        Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_button_Yes);
        dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isInRequest = true;
                Response.setValue(1);


            }
        });
        dialog.show();



    }


    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;


    }

    public List<PropertyInfo> getPropertyInfo(Integer check) {
        List<PropertyInfo> props = new ArrayList<PropertyInfo>();
        PropertyInfo userid = new PropertyInfo();
        userid.setName("DriverID");
        userid.setValue(sharedPreferencesManager.getUserID());
        userid.setType(String.class);
        props.add(userid);
        PropertyInfo codeVer = new PropertyInfo();
        codeVer.setName("Status");
        codeVer.setValue(check);
        codeVer.setType(Integer.class);
        props.add(codeVer);

        return props;


    }

    public List<PropertyInfo> getPropertyInfo(String OrderID) {
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

        PropertyInfo name = new PropertyInfo();
        name.setName("OrderID");
        name.setValue(OrderID);// Generally array index starts from 0 not 1
        name.setType(String.class);
        propertyInfos.add(name);

        return propertyInfos;
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void ShowNot (String Title,String Not){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle(Title);
        builder.setContentText(Not);
        Intent in = new Intent(this,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(in);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Vibration
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        //LED
        builder.setLights(Color.YELLOW, 3000, 3000);
        NM.notify(0, builder.build());


    }

    public List<PropertyInfo> getPropertyInfo (Location location, float speedString) {
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

        PropertyInfo id = new PropertyInfo();
        id.setName("DriverID");
        id.setValue(userID);// Generally array index starts from 0 not 1
        id.setType(String.class);
        propertyInfos.add(id);
        PropertyInfo lat = new PropertyInfo();
        lat.setName("lat");
        lat.setValue(String.valueOf(location.getLatitude()));// Generally array index starts from 0 not 1
        lat.setType(String.class);
        propertyInfos.add(lat);
        PropertyInfo lng = new PropertyInfo();
        lng.setName("lng");
        lng.setValue(String.valueOf(location.getLongitude()));// Generally array index starts from 0 not 1
        lng.setType(String.class);
        propertyInfos.add(lng);
        PropertyInfo speed = new PropertyInfo();
        speed.setName("speed");
        speed.setValue(String.valueOf(speedString));// Generally array index starts from 0 not 1
        speed.setType(String.class);
        propertyInfos.add(speed);
        PropertyInfo isAva = new PropertyInfo();
        isAva.setName("isAvailable");
        if (sharedPreferencesManager.getAvailability()){
            isAva.setValue(String.valueOf(1));// Generally array index starts from 0 not 1
        }else{
            isAva.setValue(String.valueOf(0));// Generally array index starts from 0 not 1
        }
        isAva.setType(String.class);
        propertyInfos.add(isAva);



        return propertyInfos;
    }



    public void startTimer(){
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int sec = 0;
            int min = -1;
            int h = 0;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(sec % 60 == 0){
                            min ++;
                            sec = 0;
                        }
//                        if (min % 60 == 0 && sec % 60 == 0){
//                            min = 0;
//                            h ++;
//                        }
                        String secString = String.valueOf(sec);
                        String minString = String.valueOf(min);
                        String hString = String.valueOf(h);
                        if (secString.trim().length() == 1){
                            secString = 0+secString;
                        }
                        if (minString.trim().length() == 1){
                            minString = 0+minString;
                        }
                        if (hString.trim().length() == 1){
                            hString = 0+hString;
                        }
                        timeRecorderView.setText(hString + ":" + minString + ":" + secString);
                        sec++;
                    }
                });
            }
        }, 1000, 1000);
    }

    public void stopTimer(){
        timer.cancel();
        timer = null;
    }




    public void showSummary(){


    }







    public boolean isInternetOn(Context ctx) {
        ConnectivityManager Connect_Manager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State connected = NetworkInfo.State.CONNECTED;
        NetworkInfo.State connecting = NetworkInfo.State.CONNECTING;
        NetworkInfo.State disconnected = NetworkInfo.State.DISCONNECTED;

        NetworkInfo.State info0 = Connect_Manager.getNetworkInfo(0).getState();
        NetworkInfo.State info1 = Connect_Manager.getNetworkInfo(1).getState();

        // ARE WE CONNECTED TO THE NET
        if (info0 == connected || info0 == connecting || info1 == connecting
                || info1 == connected) {

            return true;
        } else if (info0 == disconnected || info1 == disconnected) {
            return false;
        }
        return false;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

    }

    public static void updateAvailabltyButton(){

        if (isTaxiAvailable && taxiStatus){
            availableButton.setBackgroundColor(Color.parseColor("#FFCD00"));
            isAvailable.setValue(1);
            sharedPreferencesManager.setAvailability(true);


        }else if(isTaxiAvailable && !taxiStatus){
            availableButton.setBackgroundColor(Color.parseColor("#FFF8C6"));
            isAvailable.setValue(2);
            availableButton.setEnabled(false);
        }
        else{
            availableButton.setBackgroundColor(Color.parseColor("#F60505"));
            isAvailable.setValue(0);
            sharedPreferencesManager.setAvailability(false);

        }
    }

//    class Update extends AsyncTask<Void, Void, Void> {
//        String result = "";
//        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//                SoapEnvelope.VER11);
//        public Update (SoapObject soapObject){
//            envelope.dotNet = true;
//            envelope.setOutputSoapObject(soapObject);
//        }
//        final HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,5000);
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                androidHttpTransport.call(SOAP_ACTION, envelope);
//                result = ((SoapPrimitive) envelope.getResponse()).toString();
//
//
//            } catch (Exception e) {
//
////                Toast.makeText(WebAPI.this.context, e.toString(), Toast.LENGTH_LONG).show();
//
//            }
//            return null;
//        }
//    }
//  class UpdateAvailability extends AsyncTask<Void, Void, String> {
//      ProgressDialog progressDialog;
//      boolean availability;
//      String output;
//
//      public UpdateAvailability(boolean b){
//          this.availability =b;
//
//      }
//
//      @Override
//      protected String doInBackground(Void... params) {
//          List<PropertyInfo> props;
//          if(availability) {
//              props = getPropertyInfo(1);
//              isAvailable.setValue(1);
//          }else{
//              props = getPropertyInfo(0);
//              isAvailable.setValue(0);
//          }
//          WebAPI webAPI = new WebAPI(getApplicationContext(),"UpdateAvailability",props);
//          sharedPreferencesManager.setAvailability(availability);
//          output = webAPI.call_request();
//          return output;
//      }
//      @Override
//      protected void onPreExecute() {
//          progressDialog = new ProgressDialog(MainActivity.this);
//          progressDialog.setTitle("Loading...");
//          progressDialog.setMessage("Please Wait ... ");
//          progressDialog.setCancelable(false);
//          progressDialog.show();
//      }
//
//      @Override
//      protected void onPostExecute(String s) {
//          super.onPostExecute(s);
//          progressDialog.dismiss();
//          if (output == null || output.trim().isEmpty()){
//              if(!availability) {
//                  isAvailable.setValue(1);
//              } else {
//                  isAvailable.setValue(0);
//              }
//              sharedPreferencesManager.setAvailability(!availability);
//              showAlertDialog("خطأ","لم نستطلع الاتصال بالسيرفر , يرجى التأكد من الاتصال بالانترنت");
//          }else{
//              if (availability){
//                  availableButton.setBackgroundColor(Color.parseColor("#FFCD00"));
//
//              }else{
//                  availableButton.setBackgroundColor(Color.parseColor("#FFF8C6"));
//
//              }
//          }
//      }
//  }

    public void showAlertDialog (String title,String message){

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "حسناً",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }









}
