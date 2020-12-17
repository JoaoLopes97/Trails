package com.example.trails.ui.start;

import android.Manifest;
import android.content.SyncStats;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.TerrainType;
import com.example.trails.model.Trail;
import com.example.trails.ui.explore.RecyclerViewAdapter;
import com.example.trails.ui.explore.TrailCard;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class StartFragment extends Fragment implements OnMapReadyCallback {

    private StartViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TrailCard> recyclerList = new ArrayList<>();

    //Chronometer
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private FloatingActionButton startTrail;
    private Button save, clear;
    private TextView kms;

    //MapView
    private MapView mapView;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_User_Location_Code = 99;

    private Polyline polyline = null;
    private PolylineOptions polylineOptions;
    private float distance;
    private ArrayList<LatLng> latLngs = new ArrayList<>();

    private Location lastLocation;
    private int width = 5;

    private LocationRequest locationRequest;
    private DB db;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Centrar e alterar zoom na posição
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                // Apenas centrar na posição
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                latLngs.add(latLng);

                for (int i = 0; i < latLngs.size(); i++) {
                    polylineOptions = new PolylineOptions().addAll(latLngs).width(width);
                    polyline = map.addPolyline(polylineOptions);
                }

                if (lastLocation != null) {
                    distance += lastLocation.distanceTo(location);
                }
                kms.setText(String.format("%.2f", distance / 1000));
                lastLocation = location;
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.start_fragment, container, false);

        startTrail = root.findViewById(R.id.startTrail);
        save = root.findViewById(R.id.saveTrail);
        clear = root.findViewById(R.id.clearTrail);
        kms = root.findViewById(R.id.num_km);

        mRecyclerView = root.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(getActivity(), recyclerList);
        mRecyclerView.setAdapter(mAdapter);
        CreateTrailsCards();

        checkUserLocationPermission();


        db = new DB();

        // Controla o intervalo de tempo entre cada pedido e a ACCURACY
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        chronometer = root.findViewById(R.id.clock);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);
            }
        });

        startTrail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (running) {
                    chronometer.stop();
                    changeButtonStart();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    running = false;
                } else {
                    checkUserLocationPermission();
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    changeButtonStart();
                    running = true;
                }
            }
        });

             save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Characteristics c = new Characteristics("name","desc",3.0f, TerrainType.ALTO,distance / 1000,SystemClock.elapsedRealtime() - chronometer.getBase() - pauseOffset);
                //Trail trail = new Trail(c,latLngs,1,null);
                //db.insertData("trails",trail);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latLngs.clear();
                distance = 0;
                kms.setText("0,0");
                chronometer.setBase(SystemClock.elapsedRealtime());
                lastLocation = null;
                map.clear();
            }
        });

        //if not null
        String str = getArguments().getString("id");
        Trail t;
        if(str != null){
            t = db.retrieveData(str);
            drawTrail(t);
        }
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        latLngs = new ArrayList<>();
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

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        } else {
            return true;
        }
    }

    private void CreateTrailsCards() {
        TrailCard trail = new TrailCard("Trilho da Arrabida", "Arrabida,Setubal,Portugal", (float) 4.5, 57, R.mipmap.portinho_arrabida_1);
        recyclerList.add(trail);
        trail = new TrailCard("Trilho da Arrabida 4", "Arrabida,Setubal,Portugal", (float) 1, 3, R.mipmap.portinho_arrabida_2);
        recyclerList.add(trail);
        trail = new TrailCard("Trilho da Arrabida 5", "Arrabida,Setubal,Portugal", (float) 5, 10, R.mipmap.portinho_arrabida_1);
        recyclerList.add(trail);
        mAdapter.notifyDataSetChanged();
    }

    private void drawTrail(Trail trail){
        System.out.println(trail);
       /* map.moveCamera(CameraUpdateFactory.newLatLng(downloadedTrail.get(0)));
        for (int i = 0; i < downloadedTrail.size(); i++) {
            polylineOptions = new PolylineOptions().addAll(downloadedTrail).width(width);
            polyline = map.addPolyline(polylineOptions);
        }*/
    }
}