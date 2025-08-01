package com.example.b07_project21;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.b07_project21.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.example.b07_project21.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_safety, R.id.nav_questionnaire, R.id.nav_emergency, R.id.nav_support, R.id.nav_notification, R.id.nav_exit)
                .setOpenableLayout(drawer)
                .build();
        NavController navController =
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // disable nav bar for questionnaire section, use back button to exit to home instead
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_questionnaire) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        LinearLayout bottomNavButton = findViewById(R.id.bottom_nav_button);
        if (bottomNavButton != null) {
            bottomNavButton.setOnClickListener(v -> exitToGoogle());
        }
        // Add listener for navigation drawer menu items
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_exit) {
                exitToGoogle();
                return true;
            } else {
                // Handle other navigation items normally
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void exitToGoogle() {
    // First, launch Google in browser
    Intent browserIntent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com")
    );
    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(browserIntent);

    // Small delay to ensure browser starts before killing app
    new android.os.Handler().postDelayed(() -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finishAffinity();
        }
    }, 100);
}
}

