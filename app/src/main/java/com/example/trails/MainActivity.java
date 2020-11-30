package com.example.trails;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.trails.ui.explore.ExploreFragment;
import com.example.trails.ui.myTrails.MyTrailsFragment;
import com.example.trails.ui.profile.ProfileFragment;
import com.example.trails.ui.settings.SettingsFragment;
import com.example.trails.ui.start.StartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_explore, R.id.navigation_my_trails, R.id.navigation_start,R.id.navigation_profile,R.id.navigation_settings)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_explore:
                    selectedFragment = new ExploreFragment();
                    break;
                case R.id.nav_my_trails:
                    selectedFragment = new MyTrailsFragment();
                    break;
                case R.id.nav_start:
                    selectedFragment = new StartFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment,selectedFragment).commit();
            return true;
        }
    };
}