package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Trail;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapDraw extends Fragment implements OnMapReadyCallback {


    private Polyline polyline = null;
    private PolylineOptions polylineOptions;
    private GoogleMap map;
    private Trail trail;

    public MapDraw() {
    }

    public MapDraw(Trail trail) {
        this.trail = trail;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detail_map_fragment, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.detail_map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setAllGesturesEnabled(false);
        drawTrail(trail);
    }

    private void drawTrail(Trail trail) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Coordinates lg : trail.getCoordinates()) {
            latLngs.add(new LatLng(lg.getLatitude(), lg.getLongitude()));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 13));
        map.addMarker(new MarkerOptions()
                .position(latLngs.get(0)));
        for (int i = 0; i < latLngs.size(); i++) {
            polylineOptions = new PolylineOptions().addAll(latLngs).width(5).color(getResources().getColor(R.color.Green));
            polyline = map.addPolyline(polylineOptions);
        }
        //captureMapScreen();
    }
}
