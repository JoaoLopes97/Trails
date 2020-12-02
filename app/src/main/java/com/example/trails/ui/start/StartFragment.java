package com.example.trails.ui.start;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StartFragment extends Fragment implements OnMapReadyCallback {

    private StartViewModel mViewModel;

    //MapView
    private MapView mapView;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_User_Location_Code = 99;

    //Chronometer
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private FloatingActionButton startTrail;
    private Button save, clear;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //mViewModel = ViewModelProviders.of(this).get(StartViewModel.class);
        View root = inflater.inflate(R.layout.start_fragment, container, false);

        save = root.findViewById(R.id.save);
        clear = root.findViewById(R.id.clear);

        checkUserLocationPermission();

        chronometer = root.findViewById(R.id.clock);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });

        startTrail = root.findViewById(R.id.startTrail);
        startTrail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(running){
                    chronometer.stop();
                    changeButtonStart();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

                    running = false;
                }else{
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    changeButtonStart();
                    running = true;
                }
            }
        });

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return root;
    }

    private void changeButtonStart() {
        if (!running) {
            startTrail.setImageResource(R.drawable.ic_baseline_pause_24);
            save.setVisibility(View.INVISIBLE);
            clear.setVisibility(View.INVISIBLE);
        } else {
            startTrail.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            save.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title("Você está aqui!"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
    }

    public boolean checkUserLocationPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            return false;
        }
        else
        {
            return true;
        }
    }
}