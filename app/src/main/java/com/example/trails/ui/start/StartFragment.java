package com.example.trails.ui.start;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Coordinates;
import com.example.trails.model.ImageData;
import com.example.trails.model.Pair;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.trails.MainActivity.setFragment;


public class StartFragment extends Fragment implements OnMapReadyCallback {

    //Chronometer
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private FloatingActionButton startTrail, takePhoto;
    private Button save, clear;
    private TextView kms;


    //MapView
    private MapView mapView;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_User_Location_Code = 99;
    private static final int CAMERA_PIC_REQUEST = 1337;

    private Polyline polyline = null;
    private PolylineOptions polylineOptions;
    private float distance;
    private ArrayList<LatLng> latLngs = new ArrayList<>();

    private String currentImagePath;
    private List<Pair<ImageData, LatLng>> imagesWithCoords;
    private Location lastLocation;
    private int width = 5;

    private Trail loadedTrail;
    private LocationRequest locationRequest;
    private CoordinatorLayout coordinatorLayout;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Centrar e alterar zoom na posição
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                // Apenas centrar na posição
                //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                if (running) {
                    latLngs.add(latLng);
                    for (int i = 0; i < latLngs.size(); i++) {
                        polylineOptions = new PolylineOptions().addAll(latLngs).width(width);
                        polyline = map.addPolyline(polylineOptions);
                    }

                    if (lastLocation != null) {
                        distance += lastLocation.distanceTo(location);
                    }
                }

                kms.setText(String.format("%.2f", distance / 1000));
                lastLocation = location;
            }
        }
    };

    public StartFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.start_fragment, container, false);


        startTrail = root.findViewById(R.id.startTrail);
        save = root.findViewById(R.id.saveTrail);
        clear = root.findViewById(R.id.clearTrail);
        kms = root.findViewById(R.id.num_km);
        takePhoto = root.findViewById(R.id.take_photo);
        coordinatorLayout = (CoordinatorLayout) root;

        if (getArguments() != null) {
            loadedTrail = (Trail) getArguments().getSerializable("trail");
            save.setText("Avaliar");
        }

        RecyclerView mRecyclerView = root.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new StartTrailAdapter(getContext(), getActivity(), LocalDB.getTrailsNameFromAssets(requireContext()));
        mRecyclerView.setAdapter(mAdapter);

        imagesWithCoords = new ArrayList<>();
        checkUserLocationPermission();
        checkCameraPermission();

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
                    if (distance == 0) {
                        kms.setText("0.00");
                    }
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
                Bundle bundle = new Bundle();
                if (loadedTrail != null) {
                    loadedTrail.setImagesWithCoords(imagesWithCoords);

                    bundle.putSerializable("trail", loadedTrail);
                    bundle.putInt("type", 1);
                } else {
                    BigDecimal bd = BigDecimal.valueOf(distance / 1000).setScale(2, RoundingMode.HALF_UP);
                    Characteristics c = new Characteristics(null, null, null, null, bd.floatValue(), Math.round(pauseOffset / 1000));
                    ArrayList<Coordinates> cd = new ArrayList<>();

                    for (LatLng lg : latLngs) {
                        cd.add(new Coordinates(lg.latitude, lg.longitude));
                    }
                    Trail trail = new Trail(c, cd, SingletonCurrentUser.getCurrentUserInstance().getIdUser());
                    trail.setImagesWithCoords(imagesWithCoords);

                    bundle.putSerializable("trail", trail);
                    bundle.putInt("type", 0);
                }

                SaveTrailFragment itt = new SaveTrailFragment();
                itt.setArguments(bundle);

                coordinatorLayout.removeAllViewsInLayout();
                setFragment(R.id.start_fragment, itt, requireActivity());
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.INVISIBLE);
                clear.setVisibility(View.INVISIBLE);
                latLngs.clear();
                distance = 0;
                kms.setText("0.00");
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                lastLocation = null;
                imagesWithCoords.clear();
                map.clear();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File imageFile = null;
                try {
                    imageFile = getImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageFile != null) {
                    Uri uri = FileProvider.getUriForFile(requireContext(), requireActivity().getApplicationContext().getPackageName() + ".provider", imageFile);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAMERA_PIC_REQUEST);
                }
            }
        });

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        latLngs = new ArrayList<>();

        return root;
    }

    private File getImage() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            Uri uri = Uri.fromFile(new File(currentImagePath));

            if (loadedTrail != null) {
                imagesWithCoords.add(new Pair<ImageData, LatLng>(new ImageData(uri, bitmap), null));
            } else {
                imagesWithCoords.add(new Pair<>(new ImageData(uri, bitmap), latLngs.get(latLngs.size() - 1)));
            }
        }
    }

    private void changeButtonStart() {
        if (!running) {
            startTrail.setImageResource(R.drawable.ic_baseline_pause_24);
            if (loadedTrail == null) {
                clear.setVisibility(View.INVISIBLE);
            }
            takePhoto.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);
        } else {
            if (loadedTrail == null) {
                clear.setVisibility(View.VISIBLE);
            }
            save.setVisibility(View.VISIBLE);
            startTrail.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            takePhoto.setVisibility(View.INVISIBLE);
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

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        if (loadedTrail != null) {
            drawTrail(loadedTrail);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    public void checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
        }
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
        }
    }

    public void drawTrail(Trail trail) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Coordinates lg : trail.getCoordinates()) {
            latLngs.add(new LatLng(lg.getLatitude(), lg.getLongitude()));
        }

        for (int i = 0; i < latLngs.size(); i++) {
            polylineOptions = new PolylineOptions().addAll(latLngs).width(width).color(getResources().getColor(R.color.Green));
            polyline = map.addPolyline(polylineOptions);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 17));
        map.addMarker(new MarkerOptions()
                .position(latLngs.get(0)));
        for (Pair<String, Coordinates> imageWithCoords : trail.getImagesCoords()) {
            new RetrieveImagesTask().execute(imageWithCoords);
        }
    }

    private class RetrieveImagesTask extends AsyncTask<Pair<String, Coordinates>, Void, Bitmap> {

        private Coordinates coordinates;

        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(Pair<String, Coordinates>... pairs ) {
            try {
                coordinates = pairs[0].second;
                URL url = new URL(pairs[0].first);

                return BitmapFactory.decodeStream(url.openStream());
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons(image));
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                        .icon(bitmapDescriptor));
            }
        }
    }

    private Bitmap resizeMapIcons(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 150, 200, false);
    }
}