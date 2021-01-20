package com.example.trails.ui.explore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Trail;
import com.example.trails.ui.details.DetailsTrailFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.trails.controller.DB.db;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private static final int Request_User_Location_Code = 99;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.map_fragment, container, false);

        retrieveTrails(null);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (checkUserLocationPermission()) {

            locationRequest = LocationRequest.create();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /*
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }*/
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void insertMarkers() {
        for (Map.Entry<String, Coordinates> trail : ExploreFragment.trails.entrySet()) {
            String trailId = trail.getKey();
            Coordinates coordinates = trail.getValue();

            setLocation(coordinates.getLatitude(), coordinates.getLongitude(), trailId);
        }
    }

    public void retrieveTrails(Query queryTmp) {
        Query query;
        if (queryTmp != null) {
            query = queryTmp;
        } else {
            query = db.collection("trails");
        }
        query
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Trail t = document.toObject(Trail.class);
                        ExploreFragment.trails.put(document.getId(), t.getCoordinates().get(0));
                    }
                    insertMarkers();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });
    }

    public void setLocation(double lat, double lon, String trailId) {
        LatLng local = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions()
                .position(local)
                .snippet(trailId));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                loadTrail(m.getSnippet());
                return true;
            }
        });
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        } else {
            return true;
        }
    }

    private void loadTrail(String documentId) {
        DocumentReference dc = db.collection("trails").document(documentId);

        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Trail t = documentSnapshot.toObject(Trail.class);
                    t.setId(documentSnapshot.getId());
                    MainActivity.setFragment(R.id.details_frag, new DetailsTrailFragment(t), (AppCompatActivity) getContext());
                }
            }
        });
    }

    public void setQuery(Query query) {
        map.clear();
        ExploreFragment.trails.clear();
        retrieveTrails(query);
    }
}

