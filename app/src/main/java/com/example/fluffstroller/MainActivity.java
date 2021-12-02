package com.example.fluffstroller;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.fluffstroller.databinding.ActivityMainBinding;
import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Observer;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private HomePageViewModel homeViewModel;
    private Observer<ProfileData> profileDataObserver;
    private Subject<ProfileData> loggedUserSubject;

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
}