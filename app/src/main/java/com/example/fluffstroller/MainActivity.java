package com.example.fluffstroller;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fluffstroller.databinding.ActivityMainBinding;
import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.pages.main.home.HomeNavFragmentDirections;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PermissionsService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements PermissionsService {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    private int currentPermissionRequestCode = 0;
    private Consumer<Boolean> onPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServicesRegistration.getInstance().registerServices(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_walk_in_progress, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {

            LoggedUserDataService loggedUserDataService = ServiceLocator.getInstance().getService(LoggedUserDataService.class);
            AuthenticationService authenticationService = ServiceLocator.getInstance().getService(AuthenticationService.class);

            loggedUserDataService.setLoggedUserData(null);
            authenticationService.logout();

            navController.navigate(HomeNavFragmentDirections.actionGlobalNavHome());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ServiceLocator.getInstance().dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onPermissionGranted != null) {
            if (requestCode == currentPermissionRequestCode && grantResults.length > 0) {
                onPermissionGranted.accept(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
        onPermissionGranted = null;
    }

    @Override
    public void checkPermission(String permission, Consumer<Boolean> onPermissionGranted) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            this.onPermissionGranted = onPermissionGranted;
            currentPermissionRequestCode++;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, currentPermissionRequestCode);
        } else {
            onPermissionGranted.accept(true);
        }
    }

    @Override
    public void checkPermissions(List<String> permissions, Consumer<Boolean> onPermissionsGranted) {
        AtomicInteger permissionsNumber = new AtomicInteger(0);
        AtomicBoolean permissionNotGranted = new AtomicBoolean(false);

        for (String permission : permissions) {
            checkPermission(permission, granted -> {
                int count = permissionsNumber.incrementAndGet();
                if (!granted) {
                    permissionNotGranted.set(true);
                    if (onPermissionsGranted != null) {
                        onPermissionsGranted.accept(false);
                    }
                } else {
                    if (!permissionNotGranted.get() && count == permissions.size()) {
                        if (onPermissionsGranted != null) {
                            onPermissionsGranted.accept(true);
                        }
                    }
                }
            });
        }
    }
}