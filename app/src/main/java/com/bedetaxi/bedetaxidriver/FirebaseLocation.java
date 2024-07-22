package com.bedetaxi.bedetaxidriver;

import com.firebase.client.Firebase;

/**
 * Created by Alaa on 1/7/2017.
 */
public class FirebaseLocation {


    double lat ;
    double lng ;

    public FirebaseLocation (double lat, double lng){
        this.lat= lat;
        this.lng = lng;
    }
}
