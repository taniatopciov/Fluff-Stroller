package com.example.fluffstroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fluffstroller.authentication.AuthenticationActivity;
import com.example.fluffstroller.databinding.ActivityMainBinding;
import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Observer;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private HomePageViewModel homeViewModel;
    private Observer<ProfileData> profileDataObserver;
    private Subject<ProfileData> loggedUserSubject;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(), "Success",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServicesRegistration.getInstance().registerServices();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        ProfileService profileService = ServiceLocator.getInstance().getService(ProfileService.class);

        profileDataObserver = profileDataResponse -> {
            if (profileDataResponse.hasErrors()) {
                Toast.makeText(this, "Error while getting the profile", Toast.LENGTH_SHORT).show();
                return;
            }

            homeViewModel.setProfileData(profileDataResponse.data);
        };

        // todo change with logged user id
        loggedUserSubject = profileService.getProfileData("userId1");
        loggedUserSubject.subscribe(profileDataObserver, false);

        homeViewModel.getDogOwnerProfileData().observe(this, dogOwnerProfileData -> {
            DogWalk currentWalk = dogOwnerProfileData.getCurrentWalk();

            if (currentWalk == null) {
                List<Dog> dogs = dogOwnerProfileData.getDogs();
                if (dogs == null || dogs.isEmpty()) {
                    navController.navigate(R.id.nav_dog_owner_home_no_dogs);
                } else {
                    navController.navigate(R.id.nav_dog_owner_home);
                }
            } else {
                switch (currentWalk.getStatus()) {
                    case PENDING:
                        navController.navigate(R.id.nav_dog_owner_home_waiting_for_stroller);
                        break;
                    case IN_PROGRESS:
                        navController.navigate(R.id.nav_dog_owner_home_walk_in_progress);
                        break;
                    case WAITING_PAYMENT:
                        // todo
                        break;
                    case PAID:
                        // todo
                        break;
                }
            }
        });

        homeViewModel.getStrollerProfileData().observe(this, strollerProfileData -> {
            WalkRequest currentRequest = strollerProfileData.getCurrentRequest();
            if (currentRequest == null) {
                navController.navigate(R.id.nav_stroller_home);
            } else {
                navController.navigate(R.id.nav_stroller_home_walk_in_progress);
            }
        });

        startAuthenticationActivity();
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

            startAuthenticationActivity();

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
        ServiceLocator.getInstance().dispose();

        if (loggedUserSubject != null) {
            if (profileDataObserver != null) {
                loggedUserSubject.unsubscribe(profileDataObserver);
            }
        }
    }

    private void startAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        activityResultLauncher.launch(intent);
    }
}