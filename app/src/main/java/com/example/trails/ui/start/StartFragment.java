package com.example.trails.ui.start;

import android.Manifest;
import android.content.SyncStats;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Comment;
import com.example.trails.model.Coordinates;
import com.example.trails.model.TerrainType;
import com.example.trails.model.Trail;
import com.example.trails.model.TrailDifficulty;
import com.example.trails.model.User;
import com.example.trails.ui.Details.DetailsTrailFragment;
import com.example.trails.ui.explore.RecyclerViewAdapter;
import com.example.trails.ui.explore.TrailCard;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


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

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    if(distance == 0) kms.setText("0,0");
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
                Characteristics c = new Characteristics("name", "desc", TrailDifficulty.Dificil, TerrainType.Alto, distance / 1000, SystemClock.elapsedRealtime() - chronometer.getBase() - pauseOffset);
                ArrayList<Coordinates> cd = new ArrayList<>();
                for (LatLng lg : latLngs) {
                    cd.add(new Coordinates(lg.latitude, lg.longitude));
                }
                Trail trail = new Trail(c, cd, "1");

                InsertTrailFragment itt = new InsertTrailFragment(trail);
                setFragment(R.id.insert_trail_frag,itt);
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

        if (getArguments() != null) {
            String str = getArguments().getString("id");
            if (str != null) {
                loadTrail(str);
            }
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

    private void drawTrail(Trail trail) {
        System.out.println(trail);
        Characteristics ch = trail.getCharacteristics();
        kms.setText(String.format("%.2f", ch.getDistance()));

        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Coordinates lg : trail.getCoordinates()) {
            latLngs.add(new LatLng(lg.getLatitude(), lg.getLongitude()));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0),15));
        map.addMarker(new MarkerOptions()
                .position(latLngs.get(0)));
        for (int i = 0; i < latLngs.size(); i++) {
            polylineOptions = new PolylineOptions().addAll(latLngs).width(width).color(getResources().getColor(R.color.Green));
            polyline = map.addPolyline(polylineOptions);
        }
    }

    private void setFragment(int layout, Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadTrail(String documentId) {
        DocumentReference dc = db.collection("trails").document(documentId);

        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Trail t = documentSnapshot.toObject(Trail.class);
                    t.setId(documentSnapshot.getId());
                    drawTrail(t);
                }
            }
        });
    }
}